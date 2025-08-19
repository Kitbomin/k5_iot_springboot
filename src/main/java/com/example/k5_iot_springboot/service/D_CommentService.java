package com.example.k5_iot_springboot.service;

import com.example.k5_iot_springboot.dto.D_Comment.request.CommentCreateRequestDto;
import com.example.k5_iot_springboot.dto.D_Comment.request.CommentUpdateRequestDto;
import com.example.k5_iot_springboot.dto.D_Comment.response.CommentResponseDto;
import com.example.k5_iot_springboot.dto.ResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

public interface D_CommentService {
    ResponseDto<CommentResponseDto> createComment(@Positive(message = "postID는 1 이상의 정수여야합니다.") Long postId, @Valid CommentCreateRequestDto dto);

    ResponseDto<CommentResponseDto> updateComment(@Positive(message = "postID는 1 이상의 정수여야합니다.") Long postId, @Positive(message = "commentId는 1 이상의 정수여야합니다.") Long commentId, @Valid CommentUpdateRequestDto dto);

    ResponseDto<CommentResponseDto> deleteComment(@Positive(message = "postID는 1 이상의 정수여야합니다.") Long postId, @Positive(message = "commentId는 1 이상의 정수여야합니다.") Long commentId);
}
