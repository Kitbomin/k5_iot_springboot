package com.example.k5_iot_springboot.dto.D_Post.response;

import com.example.k5_iot_springboot.dto.D_Comment.response.CommentResponseDto;
import com.example.k5_iot_springboot.entity.D_Comment;
import com.example.k5_iot_springboot.entity.D_Post;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
// : JSON 변환 시 null 인 필드는 응답에서 제외됨
//      >> 불필요한 null 값을 응답에 포함시키지 않음 -> 불필요한 처리를 할 필요가 없으니까.
public record PostDetailResponseDto(

        Long id,
        String title,
        String content,
        String author,

        List<CommentResponseDto> comments

) {
    // 레코드도 클래스니까 메소드같은것도 정의 가능함


    // 정적 메서드 from
    // : D_Post 엔티티를 PostDetailResponseDto로 변환해주는 작업을 해줌
    public static PostDetailResponseDto from (D_Post post) {
        if (post ==null) return null; // NullpointException 방지

        // 댓글 엔티티 가져오기
        List<D_Comment> comments
                = post.getComments() != null ? post.getComments() : Collections.emptyList();

        List<CommentResponseDto> commentDtos = comments.stream()
                .filter(Objects::nonNull)
                .map(CommentResponseDto::from)
                .toList();

        return new PostDetailResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                commentDtos
        );
    }

}
