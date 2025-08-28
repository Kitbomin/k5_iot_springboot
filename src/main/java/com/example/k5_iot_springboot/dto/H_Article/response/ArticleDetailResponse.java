package com.example.k5_iot_springboot.dto.H_Article.response;

import java.time.LocalDateTime;

public record ArticleDetailResponse(

        Long id,

        String title,

        String content,

        String authorLoginId,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}
