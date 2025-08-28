package com.example.k5_iot_springboot.dto.H_Article.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArticleCreateRequest(
        @NotBlank @Size(max = 200)
        String title,

        @NotBlank //@Lob이 있어서 @Size는 필요없음
        String content
) {
}
