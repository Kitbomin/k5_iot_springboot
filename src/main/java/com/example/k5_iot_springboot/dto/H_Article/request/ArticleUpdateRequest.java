package com.example.k5_iot_springboot.dto.H_Article.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// record 내부의 필드는 불변성을 가짐
public record ArticleUpdateRequest(
        @NotBlank @Size(max = 200)
        String title,

        @NotBlank //@Lob이 있어서 @Size는 필요없음
        String content
) {
}
