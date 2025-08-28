package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.dto.H_Article.request.ArticleCreateRequest;
import com.example.k5_iot_springboot.dto.H_Article.request.ArticleUpdateRequest;
import com.example.k5_iot_springboot.dto.H_Article.response.ArticleDetailResponse;
import com.example.k5_iot_springboot.dto.H_Article.response.ArticleListResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.entity.G_User;
import com.example.k5_iot_springboot.entity.H_Article;
import com.example.k5_iot_springboot.repository.G_UserRepository;
import com.example.k5_iot_springboot.repository.H_ArticleRepository;
import com.example.k5_iot_springboot.security.UserPrincipal;
import com.example.k5_iot_springboot.service.H_ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final G_UserRepository userRepository;

    /** 게시글 생성: 인증된 사용자만 생성 가능 */
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseDto<ArticleDetailResponse> createArticle(UserPrincipal principal, ArticleCreateRequest request) {

        // 유효성 검사
        validateTitleAndContent(request.title(), request.content());

        // 작성자 조회
        final String loginId = principal.getUsername();
        G_User author = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("AUTHOR_NOT_FOUND"));

        // 엔티티 생성 및 저장
//        H_Article article = H_Article.create(request.title(), request.content(), author);
//        H_Article saved = articleRepository.save(article);

        // 이렇게 해도 됨
        H_Article saved = articleRepository.save(H_Article.create(request.title(), request.content(), author));

        // 데이터에 saved 내용 넣어주기
        ArticleDetailResponse data = ArticleDetailResponse.from(saved);

        // 출력해주기
        return ResponseDto.setSuccess("SUCCESS", data);
    }


    @Override
    public ResponseDto<List<ArticleListResponse>> getAllArticles() {
        // 초기 데이터 비워두기
        List<ArticleListResponse> data = null;

        // 전체 순환 돌면서 data에 담기
        data = articleRepository.findAll().stream()
//                .map(article -> ArticleListResponse.from(article))
                .map(ArticleListResponse::from)
                .toList();

        // 출력해주기
        return ResponseDto.setSuccess("SUCCESS", data);
    }

    @Override
    public ResponseDto<ArticleDetailResponse> getArticleById(Long id) {
        // 초기 데이터 비워두기
        ArticleDetailResponse data = null;

        // id가 비워져있는 경우 필수값이라 경고하는 체킹
        if (id == null) throw new IllegalArgumentException("ARTICLE_ID_REQUIRED");

        // 뭔가 값은 있는데 그 값으로 article을 찾아올 수 없을 때 발생하는 예외처리
        H_Article article = articleRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("ARTICLE_NOT_FOUND"));

        // 데이터에 article 값 넣어주기
        data = ArticleDetailResponse.from(article);

        // 출력해주기
        return ResponseDto.setSuccess("SUCCESS", data);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @authz.isArticleAuthor(#articleId, authentication) ")
    // @authz = Authorization 에서 따옴
    // 빈으로 등록된 AuthorizationChecker를 어노테이션화 한 기능 -> 커스텀 어노테이션 호출 -> @Component("authz") 로 이름을 붙혔으니까 가능
    // cf) PreAuthorize | PostAuthorize 내부의 기본 변수
    //  - authentication: 현재 인증 객체 (자동 캐치)
    //  - principal: authentication.getPrincipal() 만 가져오게 됨 (UserDetails 구현체) => AuthorizationChecker에서 어떤 타입으로 받아오느냐에 따라 로직이 달라짐
    //  - #변수명: 메서드 파라미터 중 이름이 해당 변수명인 데이터

    public ResponseDto<ArticleDetailResponse> updateArticle(Long articleId, ArticleUpdateRequest request) {
        // 유효성 검사
        validateTitleAndContent(request.title(), request.content());

        // id가 비워져있는 경우 필수값이라 경고하는 체킹
        if (articleId == null) throw new IllegalArgumentException("ARTICLE_ID_REQUIRED");

        // 뭔가 값은 있는데 그 값으로 article을 찾아올 수 없을 때 발생하는 예외처리
        H_Article article = articleRepository.findById(articleId)
                .orElseThrow( () -> new IllegalArgumentException("ARTICLE_NOT_FOUND"));

        // 업데이트 할 값 넣어주기
        article.update(request.title(), request.content());

        // 저장하기
        articleRepository.flush();

        // data에 변환된 값 넣어주기
        ArticleDetailResponse data = ArticleDetailResponse.from(article);

        // 출력해주기
        return ResponseDto.setSuccess("SUCCESS", data);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authz.isArticleAuthor(#id, authentication)")
    public ResponseDto<Void> deleteArticle(UserPrincipal principal, Long id) {
        // id가 비워져있는 경우 필수값이라 경고하는 체킹
        if (id == null) throw new IllegalArgumentException("ARTICLE_ID_REQUIRED");

        // 뭔가 값은 있는데 그 값으로 article을 찾아올 수 없을 때 발생하는 예외처리
        H_Article article = articleRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("ARTICLE_NOT_FOUND"));

        // 리포지토리에서 지우기
        articleRepository.delete(article);

        // 출력해주기
        return ResponseDto.setSuccess("SUCCESS", null);
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
