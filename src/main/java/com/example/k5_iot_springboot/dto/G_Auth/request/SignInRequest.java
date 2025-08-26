package com.example.k5_iot_springboot.dto.G_Auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInRequest(
        // 기본 불변 객체이기에 private 설정할 필요 없음)
        @NotBlank @Size(min = 4, max = 50)
        String loginId,

        @NotBlank @Size(min = 8, max = 100)
        String password

        ) {
}
