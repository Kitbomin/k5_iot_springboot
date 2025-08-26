package com.example.k5_iot_springboot.dto.G_Auth.request;

import com.example.k5_iot_springboot.common.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/** 회원가입 요청 DTO */

public record SignUpRequest (
        // 기본 불변 객체이기에 private 설정할 필요 없음)
        @NotBlank @Size(min = 4, max = 50)
        String loginId,

        @NotBlank @Size(min = 8, max = 100)
        String password,

        @NotBlank @Email @Size(max = 255)
        String email,

        @NotBlank @Size(max = 50)
        String nickname,

        // 선택값 (null 허용)
        Gender gender
) {

}
