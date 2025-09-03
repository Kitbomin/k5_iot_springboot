package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.common.enums.OrderStatus;
import com.example.k5_iot_springboot.common.utils.DateUtils;
import com.example.k5_iot_springboot.dto.I_Order.request.OrderRequest;
import com.example.k5_iot_springboot.dto.I_Order.response.OrderResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.entity.G_User;
import com.example.k5_iot_springboot.entity.I_Order;
import com.example.k5_iot_springboot.entity.I_OrderItem;
import com.example.k5_iot_springboot.entity.I_Product;
import com.example.k5_iot_springboot.repository.I_OrderRepository;
import com.example.k5_iot_springboot.repository.I_ProductRepository;
import com.example.k5_iot_springboot.repository.I_StockRepository;
import com.example.k5_iot_springboot.security.UserPrincipal;
import com.example.k5_iot_springboot.service.I_OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor // final 필드 OR @NonNull 필드란을 매개변수로 가지는 생성자
@Transactional(readOnly = true)
public class I_OrderServiceImpl implements I_OrderService {
    private final I_StockRepository stockRepository;
    private final I_ProductRepository productRepository;
    private final I_OrderRepository orderRepository;
    private final EntityManager em; // 사용자 참조 - getReference 등


    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseDto<OrderResponse.Detail> create(UserPrincipal principal, OrderRequest.OrderCreateRequest request) {
        OrderResponse.Detail data = null;

        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("주문항목 안에 아무것도 없어용");
        }

        // principal 에서 userId 추출
        Long authUserId = principal.getId();

        /**
         * EntityManager.getReference() VS JPA.findById()
         * 1) EntityManager.getReference()
         *  : 단순히 연관관계 주입만 필요할 때 사용함
         *      - 실제 SQL SELECT 문을 실행하지 않고 프록시 객체를 반환함
         *      >> 어차피 Order 엔티티의 user를 참조하는 데 실제 User의 다른 필드가 필요 없는 경우 효율적으로 사용 가능
         *       진짜 딱 읽기만하는 애
         *
         * 2) JPA.findById()
         *  : DB 조회 쿼리를 날리고 G_User 엔티티를 반환받음
         *      - 직접적으로 데이터에 접근해서 뒤져가면서 찾아옴
         *      >> 존재하지 않는 userId면 예외를 던져줌 (안정성이 더 높음)
         * */

        // 인증 주체인 authUserId로 G_User 프록시(대리인, 중계자) 획득 (UserRepository 없이도 가능함!)
        G_User userRef = em.getReference(G_User.class, authUserId);

        I_Order order = I_Order.builder()
                .user(userRef)
                .orderStatus(OrderStatus.PENDING) //기본값 pending임
                .build();

        for (OrderRequest.OrderItemLine line: request.items()) {
            if (line.quantity() <= 0) throw new IllegalArgumentException("수량은 1 이상 이어야합니다.");
            I_Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없어요. id=" + line.productId()));

            I_OrderItem item = I_OrderItem.builder()
                    .product(product)
                    .quantity(line.quantity())
                    .build();
            order.addItem(item);
        }

        I_Order saved = orderRepository.save(order);

        data = toOrderResponse(saved);

        return ResponseDto.setSuccess("주문이 성공적으로 등록되었어요", data);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseDto<OrderResponse.Detail> approve(UserPrincipal principal, Long orderId) {
        return null;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @authz.canCancel(#orderId, authentication)")
    public ResponseDto<OrderResponse.Detail> cancel(UserPrincipal principal, Long orderId) {
        return null;
    }

    @Override
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @authz.isSelf(#principal.id, authentication)")
    public ResponseDto<List<OrderResponse.Detail>> search(UserPrincipal principal, Long userId, OrderStatus status, LocalDateTime from, LocalDateTime to) {
        return null;
    }

    // === 변환 유틸 === //
    private OrderResponse.Detail toOrderResponse(I_Order order) {
        // 각 주문 항목 변환
        List<OrderResponse.OrderItemList> items = order.getItems().stream()
                .map(item -> {
                    int price = item.getProduct().getPrice();
                    int quantity = item.getQuantity();
                    int lineTotal = (int) price * quantity;

                    return new OrderResponse.OrderItemList(
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            price, quantity, lineTotal
                    );
                }).toList();
        // 총액 계산 (long)
        int totalAmount = items.stream()
                .mapToInt(OrderResponse.OrderItemList::lineTotal)
                .sum();

        // 총 수량 계산
        int totalQuantity = items.stream()
                .mapToInt(OrderResponse.OrderItemList::quantity)
                .sum();

        return new OrderResponse.Detail(
                order.getId(),
                order.getUser().getId(),
                order.getOrderStatus(),
                totalAmount,
                totalQuantity,
                DateUtils.toKstString(order.getCreatedAt()),
                items
        );
    }

}
