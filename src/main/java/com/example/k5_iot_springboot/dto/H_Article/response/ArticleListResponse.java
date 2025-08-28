package com.example.k5_iot_springboot.dto.H_Article.response;

import java.time.LocalDateTime;

public record ArticleListResponse(

        Long id,

        String title,

        String authorLoginId,

        LocalDateTime createdAt

) {
}
