package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.dto.D_Post.request.PostCreateRequestDto;
import com.example.k5_iot_springboot.dto.D_Post.request.PostUpdateRequestDto;
import com.example.k5_iot_springboot.dto.D_Post.response.PostDetailResponseDto;
import com.example.k5_iot_springboot.dto.D_Post.response.PostListResponseDto;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.entity.D_Post;
import com.example.k5_iot_springboot.repository.D_PostRepository;
import com.example.k5_iot_springboot.service.D_PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //읽기 전용 모드 => 읽기 아닌 애들은 그 때가서 조건 변경하면 됨
public class D_PostServiceImpl implements D_PostService {
    private final D_PostRepository postRepository;

    // 1) 게시글 생성
    @Override
    @Transactional // 쓰기 트랜잭션
    public ResponseDto<PostDetailResponseDto> createPost(PostCreateRequestDto dto) {
        D_Post post = D_Post.create(dto.title(), dto.content(), dto.author());
        D_Post saved = postRepository.save(post);

        return ResponseDto.setSuccess("SUCCESS", PostDetailResponseDto.from(saved));
    }


    @Override
    public ResponseDto<PostDetailResponseDto> getPostById(Long id) {
        D_Post post = postRepository.findByIdWithComments(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 id의 게시글을 찾을 수 없습니다."));

        return ResponseDto.setSuccess("SUCCESS", PostDetailResponseDto.from(post));
    }


    @Override
    public ResponseDto<List<PostListResponseDto>> getAllPosts() {
        List<D_Post> posts = postRepository.findAllOrderByIdDesc(); //최신 순 반환
        List<PostListResponseDto> result = posts.stream()
                .map(PostListResponseDto::from)
                .toList();

        return ResponseDto.setSuccess("SUCCESS", result);
    }


    @Override
    @Transactional
    public ResponseDto<PostDetailResponseDto> updatePost(Long id, PostUpdateRequestDto dto) {
        D_Post post = postRepository.findByIdWithComments(id)
                .orElseThrow(()-> new EntityNotFoundException("해당 id의 게시글을 찾을 수 없습니다."));

        post.changeTitle(dto.title().trim());
        post.changeContent(dto.content().trim());

        // 자동 저장이 됨! (save가 없음)
        // Dirty Checking 으로 저장이 되어버림(영속성 컨텍스트에 담긴 엔티티의 상태 변화를 자동 감지)

        return ResponseDto.setSuccess("SUCCESS", PostDetailResponseDto.from(post));
    }


    @Override
    @Transactional
    public ResponseDto<Void> deletePost(Long id) {
        D_Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 id의 게시글을 찾을 수 없습니다."));

        //orphanRemoval & cascade 설정으로 댓글은 자동 정리됨
        postRepository.delete(post);
        return ResponseDto.setSuccess("SUCCESS", null);
    }
}
