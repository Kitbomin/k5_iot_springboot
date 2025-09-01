package com.example.k5_iot_springboot.dto.G_Auth.response;

import com.example.k5_iot_springboot.entity.G_User;

public record FindIdResponse (
        String username,
        String nickname,
        String email
        ){
        public static FindIdResponse from(G_User user) {
                return new FindIdResponse(
                        user.getLoginId(),
                        user.getEmail(),
                        user.getNickname()
                );
        }
}
