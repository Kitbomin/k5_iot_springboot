package com.example.k5_iot_springboot.controller;

import com.example.k5_iot_springboot.dto.I_Order.request.OrderRequest;
import com.example.k5_iot_springboot.dto.I_Order.response.OrderResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.entity.base.BaseTimeEntity;
import com.example.k5_iot_springboot.security.UserPrincipal;
import com.example.k5_iot_springboot.service.I_OrderService;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
/**
 * 주문 생성 / 승인 / 취소 + 검색
 * */
public class I_OrderController extends BaseTimeEntity {
    private final I_OrderService orderService; // 연결되어있는 파일 간의 빈 연동


    /** 주문 생성: 인증 주체의 userId를 사용 */
    @PostMapping
    // cf) ResponseEntity(HttpStatus 상태코드, HttpHeaders 요청/응답에 대한 요구사항, HttpBody 응답 본문)
    //     ResponseDto(HttpBody 응답 본문 타입) - 데이터 전송 객체
    //          >> result(boolean), message(String), data(T): 실제 요청 데이터 타입
    public ResponseEntity<ResponseDto<OrderResponse.Detail>> create (
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody OrderRequest.OrderCreateRequest request
    ) {
        ResponseDto<OrderResponse.Detail> response = orderService.create(principal, request);

//        return ResponseEntity.ok(response);
         return ResponseEntity.ok().body(response);
        // => 둘다 똑같은 기능이긴 함 | 근데 클라이언트에게 제공할 정보를 상세히 보여줄려면 이거 써야함
    }


    /** 주문 승인: ADMIN/MANAGER 만 가능 */
    @PostMapping("/{orderId}/approve")
    public ResponseEntity<ResponseDto<OrderResponse.Detail>> approve(
            @AuthenticationPrincipal UserPrincipal principal, //주문 승인자 정보를 저장할 경우 필요함
            @PathVariable Long orderId
    ) {
        ResponseDto<OrderResponse.Detail> response = orderService.approve(principal, orderId);
//        return ResponseEntity.ok().body(response);
        return ResponseEntity.ok(response);
    }


    /** 주문 취소:  */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ResponseDto<OrderResponse.Detail>> cancel(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long orderId
    ) {
        ResponseDto<OrderResponse.Detail> response = orderService.cancel(principal, orderId);
        return ResponseEntity.ok(response);
    }

    // 검색



}
