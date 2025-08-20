package com.example.k5_iot_springboot.dto.D_Post.response;


import com.example.k5_iot_springboot.entity.D_Post;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostWithCommentCountResponseDto(
        Long id,
        String title,
        String author,
        Long commentCount //댓글 개수

) {
    public static PostWithCommentCountResponseDto from(D_Post post, long commentCount) {
        // from은 엔터티를 DTO 로 만드는 도구임
        if (post == null) return null;
        return new PostWithCommentCountResponseDto(
                post.getId(),
                post.getTitle(),
                post.getAuthor(),
                commentCount
        );
    }
}
