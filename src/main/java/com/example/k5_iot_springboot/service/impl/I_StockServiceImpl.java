package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.dto.I_Order.request.StockRequest;
import com.example.k5_iot_springboot.dto.I_Order.response.StockResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.entity.I_Stock;
import com.example.k5_iot_springboot.repository.I_StockRepository;
import com.example.k5_iot_springboot.security.UserPrincipal;
import com.example.k5_iot_springboot.service.I_StockService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class I_StockServiceImpl implements I_StockService {
    private final I_StockRepository stockRepository;

    @Override
    @Transactional
    public ResponseDto<StockResponse.Response> adjust(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                      @Valid @RequestBody StockRequest.StockAdjust request) {
        // 재고 증감: delta 값이 양수 -> 입고/반품 음수-> 출고/차감
        StockResponse.Response data = null;

        I_Stock stock = stockRepository.findByProductIdForUpdate(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("재고 정보를 찾을 수 없습니다."));

        int newQuantity = stock.getQuantity() + request.delta();

        if (newQuantity < 0) throw new IllegalArgumentException("재고가 부족합니다.");

        stock.setQuentity(newQuantity);

        data = new StockResponse.Response(
                stock.getProduct().getId(), stock.getQuantity()
        );

        return ResponseDto.setSuccess("재고가 성공적으로 증감되었습니다.", data);
    }

    @Override
    @Transactional
    public ResponseDto<StockResponse.Response> set(UserPrincipal userPrincipal, StockRequest.@Valid StockSet request) {
        return null;
    }

    @Override
    public ResponseDto<StockResponse.Response> get(Long productId) {
        return null;
    }
}
