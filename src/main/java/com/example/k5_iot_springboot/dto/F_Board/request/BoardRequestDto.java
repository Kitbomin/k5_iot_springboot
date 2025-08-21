package com.example.k5_iot_springboot.dto.F_Board.request;

/*
* 게시글 요청 DTO
* - Controller 바인딩 용
* */

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BoardRequestDto {
    /*
    * 게시글 생성 요청
    * */
    public record CreateRequest(

            @NotBlank(message = "비울 수 없음")
            @Size(max = 100, message = "제목은 최대 100자까지")
            String title,

            @NotBlank(message = "비울 수 없음")
            // Lob은 사이즈 설정 안함
            String content

    ) {}


    /*
     * 게시글 수정 요청
     * */
    public record UpdateRequest(

            @NotBlank(message = "비울 수 없음")
            @Size(max = 100, message = "제목은 최대 100자까지")
            String title,

            @NotBlank(message = "비울 수 없음")
            // Lob은 사이즈 설정 안함
            String content

    ) {}

}
