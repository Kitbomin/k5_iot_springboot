package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.dto.H_Article.request.ArticleCreateRequest;
import com.example.k5_iot_springboot.dto.H_Article.request.ArticleUpdateRequest;
import com.example.k5_iot_springboot.dto.H_Article.response.ArticleDetailResponse;
import com.example.k5_iot_springboot.dto.H_Article.response.ArticleListResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.repository.H_ArticleRepository;
import com.example.k5_iot_springboot.security.UserPrincipal;
import com.example.k5_iot_springboot.service.H_ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class H_ArticleServiceImpl implements H_ArticleService {
    private final H_ArticleRepository articleRepository;


    /** 게시글 생성: 인증된 사용자만 생성 가능 */
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseDto<ArticleDetailResponse> createArticle(UserPrincipal principal, ArticleCreateRequest request) {



        return null;
    }

    @Override
    public ResponseDto<List<ArticleListResponse>> getAllArticles() {
        return null;
    }

    @Override
    public ResponseDto<ArticleDetailResponse> getArticleById(Long id) {
        return null;
    }

    @Override
    @Transactional
    public ResponseDto<ArticleDetailResponse> updateArticle(Long id, ArticleUpdateRequest request) {
        return null;
    }

    @Override
    @Transactional
    public ResponseDto<Void> deleteArticle(UserPrincipal principal, Long id) {
        return null;
    }

    /** 공통 유틸: 제목 / 내용 유효성 검사 => 비워지면 안됨 */
    private void validateTitleAndContent(String title, String content) {
        if (!StringUtils.hasText(title)) {
            throw new IllegalArgumentException("TITLE_REQUIRED");
        }

        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("CONTENT_REQUIRED");
        }
        
    }

}
