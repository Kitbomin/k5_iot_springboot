package com.example.k5_iot_springboot.service;

import com.example.k5_iot_springboot.dto.I_Order.request.StockRequest;
import com.example.k5_iot_springboot.dto.I_Order.response.StockResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

public interface I_StockService {


    ResponseDto<StockResponse.Response> adjust(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                               @Valid @RequestBody StockRequest.StockAdjust request);

    ResponseDto<StockResponse.Response> set(UserPrincipal userPrincipal, StockRequest.@Valid StockSet request);

    ResponseDto<StockResponse.Response> get(Long productId);
}
