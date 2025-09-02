package com.example.k5_iot_springboot.controller;

import com.example.k5_iot_springboot.common.constants.ApiMappingPattern;
import com.example.k5_iot_springboot.dto.I_Order.request.ProductRequest;
import com.example.k5_iot_springboot.dto.I_Order.response.ProductResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.entity.base.BaseTimeEntity;
import com.example.k5_iot_springboot.security.UserPrincipal;
import com.example.k5_iot_springboot.service.I_ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 제품 등록/수정/조회
 * */

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class I_ProductController extends BaseTimeEntity {

    private final I_ProductService productService;

    // 제품 등록
    // : 권한 -> USER, MANAGER, ADMIN 중 등록/수정은 ADMIN 만 가능하도록
    // - 조회는 누구나 가능
    @PostMapping(ApiMappingPattern.Products.ROOT)
    public ResponseEntity<ResponseDto<ProductResponse.DetailResponse>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProductRequest.Create request
    ) {
        ResponseDto<ProductResponse.DetailResponse> response = productService.create(principal, request);

        return ResponseEntity.ok(response);
    }


    // 제품 수정
    // : 권한 -> ADMIN만 가능
    @PutMapping(ApiMappingPattern.Products.ID_ONLY)
    public ResponseEntity<ResponseDto<ProductResponse.DetailResponse>> update(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProductRequest.Update request
    ) {
        ResponseDto<ProductResponse.DetailResponse> response = productService.update(productId, principal, request);

        return ResponseEntity.ok(response);
    }


    // 제품 단건 조회
    @GetMapping(ApiMappingPattern.Products.ID_ONLY)
    public ResponseEntity<ResponseDto<ProductResponse.DetailResponse>> getProductById(
            @PathVariable Long productId
    ) {
        ResponseDto<ProductResponse.DetailResponse> response = productService.get(productId);

        return ResponseEntity.ok(response);
    }




}
