package com.example.k5_iot_springboot.service;

import com.example.k5_iot_springboot.dto.I_Order.request.ProductRequest;
import com.example.k5_iot_springboot.dto.I_Order.response.ProductResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.security.UserPrincipal;
import jakarta.validation.Valid;

public interface I_ProductService {
    ResponseDto<ProductResponse.DetailResponse> create(UserPrincipal principal, ProductRequest.@Valid Create request);

    ResponseDto<ProductResponse.DetailResponse> update(Long productId, UserPrincipal principal, ProductRequest.@Valid Update request);

    ResponseDto<ProductResponse.DetailResponse> get(Long productId);
}
