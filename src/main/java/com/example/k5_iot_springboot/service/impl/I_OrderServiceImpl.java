package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.common.enums.OrderStatus;
import com.example.k5_iot_springboot.dto.I_Order.request.OrderRequest;
import com.example.k5_iot_springboot.dto.I_Order.response.OrderResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.repository.I_OrderRepository;
import com.example.k5_iot_springboot.security.UserPrincipal;
import com.example.k5_iot_springboot.service.I_OrderService;
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
    private final I_OrderRepository orderRepository;


    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseDto<OrderResponse.Detail> create(UserPrincipal principal, OrderRequest.OrderCreateRequest request) {
        return null;
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
}
