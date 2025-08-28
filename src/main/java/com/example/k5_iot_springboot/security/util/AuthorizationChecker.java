package com.example.k5_iot_springboot.security.util;

// @PreAuth~~ 랑 @hasRoles 만으로도 앵간한건 다 할 수 있지만, 여기서 또 인가 체커를 만드는 이유는 더 세세한 인가 체크를 구현하기 위해서
// 소유자 검사 및 리포지토리 접근 체크를 해보자

// 역할 체크 VS 소유자 검사 및 리포지토리 접근 체크
// 1. 역할 체크
//  : @PreAuthorize("hasRole('ADMIN')") 만으로도 충분함
// 2. 소유자 검사(게시글 작성자만 수정 / 삭제 가능), 리포지토리 접근이 필요한 조건 (팀원 여부, 프로젝트 멤버쉽)이 있다면
//  : 컨트롤러/서비스에 비즈니스 로직을 섞지 않기 위해 빈(Bean) 으로 분리 권장

import com.example.k5_iot_springboot.entity.H_Article;
import com.example.k5_iot_springboot.repository.H_ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("authz")
@RequiredArgsConstructor
public class AuthorizationChecker { //권한, 롤 만으로는 체크하기 힘든 사항을 검증해줌
    private final H_ArticleRepository articleRepository;


    /** principal(LoginId)이 해당 articledId의 작성자인지 검사 */
    public boolean isArticleAuthor(Long articleId, Authentication principal) {
        if (principal == null || articleId == null) return false;

        String loginId = principal.getName(); //JwtAuthenticationFilter에서 username으로 주입시키는 값임

        H_Article article = articleRepository.findById(articleId).orElse(null);

        if (article == null) return false;

        return article.getAuthor().getLoginId().equals(loginId);
        // loginId와 article의 작성자가 일치하면 true 반환, 아닐 경우 false 반환
    }


}
