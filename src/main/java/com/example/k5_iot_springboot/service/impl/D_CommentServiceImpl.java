package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.dto.D_Comment.request.CommentCreateRequestDto;
import com.example.k5_iot_springboot.dto.D_Comment.request.CommentUpdateRequestDto;
import com.example.k5_iot_springboot.dto.D_Comment.response.CommentResponseDto;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.service.D_CommentService;
import org.springframework.stereotype.Service;

@Service
public class D_CommentServiceImpl implements D_CommentService {
    @Override
    public ResponseDto<CommentResponseDto> createComment(Long postId, CommentCreateRequestDto dto) {
        return null;
    }

    @Override
    public ResponseDto<CommentResponseDto> updateComment(Long postId, Long commentId, CommentUpdateRequestDto dto) {
        return null;
    }

    @Override
    public ResponseDto<CommentResponseDto> deleteComment(Long postId, Long commentId) {
        return null;
    }
}
