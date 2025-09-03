package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.common.enums.OrderStatus;
import com.example.k5_iot_springboot.common.utils.DateUtils;
import com.example.k5_iot_springboot.dto.I_Order.request.OrderRequest;
import com.example.k5_iot_springboot.dto.I_Order.response.OrderResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.entity.*;
import com.example.k5_iot_springboot.repository.I_OrderRepository;
import com.example.k5_iot_springboot.repository.I_ProductRepository;
import com.example.k5_iot_springboot.repository.I_StockRepository;
import com.example.k5_iot_springboot.security.UserPrincipal;
import com.example.k5_iot_springboot.service.I_OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        OrderResponse.Detail data = null;
        I_Order order = orderRepository.findDetailById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없어요 id=" + orderId));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("PENDING 상태에서만 승인이 가능합니다.");
        }

        // Map 컬렉션 프레임워크 사용
        // 주문 항목: 상품 A X 2 / 상품 B X 3 / 상품 A X 3
        //          >> 단순히 리스트로 순회하며 차감 시 상품 A 재고를 2번 차감하게 됨
        //      -> 그래서 Map<Long, Integer>: key=productId, value=누적수량 (수량을 합해 한번에 차감/복원이 가능하게끔)

        Map<Long, Integer> needMap = new HashMap<>();
        order.getItems().forEach(item -> needMap.merge(
                item.getProduct().getId(),
                item.getQuantity(),
                Integer::sum)
        );

        // 재고 확인 & 차감(productId 단위로 처리)
        for (Map.Entry<Long, Integer> e: needMap.entrySet()) {
            Long productId = e.getKey();
            int need = e.getValue();
            I_Stock stock = stockRepository.findByProductIdForUpdate(productId)
                    .orElseThrow(() -> new IllegalArgumentException("재고 정보가 없어요 id=" + productId));

            if (stock.getQuantity() < need) {
                // 상태가 이상하니까 상태 예외
                throw new IllegalStateException(
                        "재고 부족: productId=%d , 필요=%d, 보유=%d"
                                .formatted(productId, need, stock.getQuantity())
                );
            }
            stock.setQuantity(stock.getQuantity() - need);
        }

        order.setOrderStatus(OrderStatus.APPROVED);
        // 상태변경 트리거가 order_logs에 자동 기록해줌

        data = toOrderResponse(order);

        return ResponseDto.setSuccess("주문이 정상적으로 승인되었어요", data);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @authz.canCancel(#orderId, authentication)")
    public ResponseDto<OrderResponse.Detail> cancel(UserPrincipal principal, Long orderId) {
        OrderResponse.Detail data = null;

        I_Order order = orderRepository.findDetailById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다. id=" + orderId));

        // 이미 취소된 주문일 경우 그대로 반환 (또는 예외 발생)
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 주문입니댜ㅏ.");
            // return ResponseDto.setFailed("이미 취소된 주문입니다.");
        }

        // === MANAGER와 ADMIN은 PENDING 상태가 아니어도 (APPROVED 상태라도) 취소 가능하다면? === //
        // 상태별 분기
        if (order.getOrderStatus() == OrderStatus.PENDING) {
            // 승인 전 (PENDING): 권한 확인 필요 없음
            // +) 재고 차감이 없었기 때문에 복원할 필요 없음
            order.setOrderStatus(OrderStatus.CANCELLED);
        } else if (order.getOrderStatus() == OrderStatus.APPROVED) {
            // 승인 후 (APPROVED): 권한확인 필요함
            // +) MANAGER/ADMIN 만 취소 허용
            // +) 재고 차감이 있었기 때문에 복원해야함
            if (!hasManagerOrAdmin(principal)) {
                // hasManagerOrAdmin의 결과값이 false인 경우
                throw new IllegalArgumentException("승인된 주문은 관리자 권한(ADMIN/MANAGER) 만 취소 가능합니다.");
            }
            // 권한이 있는 경우
            // 재고 복원 되야함
            Map<Long, Integer> restoreMap = new HashMap<>();

            // 같은 상품 정보에 수량에 대한 중복 제거(단일 수량으로 합치기)
            for (I_OrderItem item: order.getItems()) {
                // 아이템 순회하면서 복구할거임
                Long productId = item.getProduct().getId();             // 해당 주문 항목의 상품 고유 ID를 순회하여 저장
                int quantity = item.getQuantity();                      // 해당 주문 항목의 주문 수량을 순회해 저장
                Integer prev = restoreMap.get(productId);               // 상품 ID를 Key로, 수량을 value로 저장하는 Map
                                                                        // >> 현재 productId에 해당하는 기존 수량을 다시 가져옴
                                                                        //      해당 key가 없다면 null로 반환될거임

                restoreMap.put(productId, (prev == null ? quantity : prev + quantity));
            }

            // 중복없는 구매의 제품 Id에 대해 취소 재고를 복구
            for (Map.Entry<Long, Integer> e: restoreMap.entrySet()) {
                Long productId = e.getKey();
                int quantity = e.getValue();    // 재고 복원 데이터

                // 재고 레코드 행 단위 잠금
                I_Stock stock = stockRepository.findByProductIdForUpdate(productId)
                        .orElseThrow(() -> new IllegalStateException("재고 정보가 없습니다. productId = " + productId));

                stock.setQuantity(stock.getQuantity() + quantity);      // 변경 감지로 업데이트 시킨거임
            }
            order.setOrderStatus(OrderStatus.CANCELLED);
        } else {
            throw new IllegalArgumentException("취소할 수 없는 주문 상태입니다: " + order.getOrderStatus());
        }


        // PENDING이 아니면 취소 불가능한 로직
//        if (order.getOrderStatus() != OrderStatus.PENDING){
//            throw new IllegalArgumentException("PENDING 상태의 주문만 취소할 수 있습니다.");
//        }

        order.setOrderStatus(OrderStatus.CANCELLED);

        data = toOrderResponse(order);

        // +) 변경 정보 자동 저장
        // +) 변경 발생 시 DB 트리거에 의해 로그 기록 생성

        return ResponseDto.setSuccess("주문 취소가 정상적으로 진행되었어요", data);
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

    // == 호출자 권한이 MANAGER/ADMIN 인지 확인하는 유틸 == //
    private boolean hasManagerOrAdmin(UserPrincipal principal) {

        // 권한에 아무것도 없으면?
        if (principal == null || principal.getAuthorities() == null) return  false;

        for (GrantedAuthority auth: principal.getAuthorities()) {
            String role = auth.getAuthority();
            if ("ROLE_ADMIN".equals(role) || "ROLE_MANAGER".equals(role)) return true;
        }

        return false;
    }

}
