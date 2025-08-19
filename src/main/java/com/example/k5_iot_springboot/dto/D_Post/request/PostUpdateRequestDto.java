package com.example.k5_iot_springboot.dto.D_Post.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PostUpdateRequestDto(

        @NotBlank(message = "제목은 필수 입력 값입니다.")
        @Size(max = 200, message = "제목은 최대 200자 까지 입력 가능합니다.")
        String title,

        @NotBlank(message = "내용은 필수 입력 값입니다.")
        @Size(max = 10_000, message = "내용은 최대 10,000 자 까지 입력 가능합니다.")
        // 어노테이션엔 1000자리 이상 숫자 입력시 , 대신 _ 을 쓸 수 있음
        String content
) {}
