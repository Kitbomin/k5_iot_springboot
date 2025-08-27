package com.example.k5_iot_springboot.dto.G_Auth.response;

import com.example.k5_iot_springboot.entity.G_User;

public class FindIdResponse {

        public record UsernameResponse(
                String username
        ) {
                public static UsernameResponse from (G_User user) {
                        return new UsernameResponse(
                                user.getLoginId()
                        );
                }
        }
}
