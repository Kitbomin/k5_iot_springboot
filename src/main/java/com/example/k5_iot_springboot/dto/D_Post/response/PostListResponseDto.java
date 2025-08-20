package com.example.k5_iot_springboot.dto.D_Post.response;

import com.example.k5_iot_springboot.entity.D_Post;
import com.example.k5_iot_springboot.repository.D_PostRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostListResponseDto(

        Long id,
        String title,
        String content,
        String author

) {

    public static PostListResponseDto from(D_Post post){
        if (post == null) return null;
        return new PostListResponseDto(

                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor()
        );
    }

    public static PostListResponseDto from(D_PostRepository.PostListProjection p){
        if (p == null) return null;
        return new PostListResponseDto(
                p.getId(),
                p.getTitle(),
                p.getContent(),
                p.getAuthor()
        );
    }

    // 내용이 너무 길면 뒤에 글자 떼서 ... 시켜버리는거
    public PostListResponseDto summerize(int maxLen) {
        String summerized = content == null ? null :
                (content.length() <= maxLen ? content : content.substring(0, maxLen) + "...");

//        return new PostListResponseDto(id, title, summerized, author);
        return new PostListResponseDto(id, summerized, summerized, author);
    }

}
