package com.example.k5_iot_springboot.dto.G_Auth.request;

import jakarta.validation.constraints.NotBlank;

public record FindIdRequest (

        @NotBlank
        String nickname,

        @NotBlank
        String email
){
}
