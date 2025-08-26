package com.example.k5_iot_springboot.filter;

/*
* === JwtAuthenticationFilter ===
* : JWT 인증 필터
* - 요청에서 JWT 토큰을 추출
*   >> request의 header 에서 토큰을 추출해 검증 (유효한 경우 SecurityContext 정보에 인증 정보를 저장함)
*
* cf) Spring Security 가 OncePerRequestFilter 를 상속받아 매 요청마다 1회 실행함
* */

import com.example.k5_iot_springboot.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component // 스프링이 해당 클래스를 관리하도록 지정, 의존성 주입
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // === 상수 및 필드 선언 === //
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = JwtProvider.BEARER_PREFIX;

    private final JwtProvider jwtProvider; //의존성 주입


    /* OncePerRequestFilter 내부 추상 메서드 -> 반드시 구현해야함
    *  Spring Security 필터가 매 요청마다 호출하는 핵심 메서드임
    *
    *  @Param request       - 현재 HTTP 요청 객체
    *  @Param response      - 현재 HTTP 요청 응답 객체
    *  @Param filterChain   - 다음 필터로 넘기기 위한 체인
    * */

    /* 요청은 다 받아주고, 인증에 문제가 있으면 예외 걸어주는 역할 */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
        ) throws ServletException, IOException {
        try {
            // 0) 사전 스킵 조건 : 이미 인증된 컨텍스트가 있으면 그대로 진행(스킵) (다른 필터가 인증처리를 한 경우, 중복 인증을 방지함)
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                // 현재 스레드(요청) 컨텍스트에 이미 인증 정보가 들어있는지 확인
                // - 다른 필터가 먼저 인증을 끝낸 경우 굳이 중복 인증을 하지 않음 -> 다음으로 진행될 수 있게끔
                filterChain.doFilter(request, response);
                return;
            }

            // 1) Preflight(OPTIONS, 사전요청) 는 통과 (CORS의 사전 요청) (정보값이 없음)
            // cf) OPTIONS 메서드 - 특정 리소스(URL)에 대한 통신 옵션 정보를 요청하는 데에 사용됨
            if (HttpMethod.OPTIONS.matches(request.getMethod())) {
                filterChain.doFilter(request, response);

                return;
            }

            // 2) Authorization 헤더에서 JWT 토큰 추출
            String authorization = request.getHeader(AUTH_HEADER);

            // 3) 헤더가 없으면 (= 비로그인 요청) 그냥 통과 -> 보호 리소스는 뒤에서 401/403으로 처리
            if (authorization == null || authorization.isBlank()) {
                filterChain.doFilter(request, response);

                return;
            }

            // 4) "Bearer " 접두가사 없으면 형식 오류 - 401 즉시 응답
            if (!authorization.startsWith(BEARER_PREFIX)) {
                unauthorized(response, "Authorization 헤더는 'Bearer <token>' 형식이어야 합니다.");

                return;
            }

            // 5) 접두사 제거 -> 순수 토큰 가져오기 ("Bearer " 공백 제거)
            String token = jwtProvider.removeBearer(authorization);
            if (token.isBlank()) {
                unauthorized(response, "토큰이 비어있습니다.");

                return;
            }

            // 헤더에서 토큰을 파싱하여 가져옴
//            String token =
//                    (authorization != null && authorization.startsWith(BEARER_PREFIX))
//                    ? jwtProvider.removeBearer(authorization) : null;

//
//            토큰이 없거나 유효하지 않으면 필터체인을 타고 다음단계로 이동
//            if (token == null || token.isEmpty()) {
//                filterChain.doFilter(request, response);
//            }

            // 6) 토큰 유효성 검사(서명/만료 포함)
            if (!jwtProvider.isValidToken(token)) {
                unauthorized(response, "토큰이 유효하지 않거나 완료되었습니다."); // 토큰이 유효하지 않은 경우 - 시큐리티 설정 없이 로직 실행
                return;
            }


            // 7) 사용자 식별자 & 권한 추출
            String username = jwtProvider.getUsernameFromJwt(token);
            Set<String> roles = jwtProvider.getRolesFromJwt(token);

            // 8) 권한 문자열 - GrantedAuthority 로 매핑("ROLE_" 접두어 보장)
            // : 스프링 시큐리티가 이해하는 권한 타입으로 변환작업을 함
            // >> 권한 명 앞에 "ROLE_" 접두사가 필요함 (없어도 되긴 함)
            Collection<? extends GrantedAuthority> authorities = toAuthorities(roles);

            // 9) SecurityContext에 인증 저장
            // : 여기서 인증 객체를 만들고 SecurityContext 에 저장함
            // >> 해당 시점부터 현재 요청은 "username 이라는 사용자가 authorities 권한으로 인증됨" 상태로 변환됨
            setAuthenticationContext(request, username ,authorities);

        }catch (Exception e) {
            logger.warn("JWT filter error", e);
            unauthorized(response, "인증 처리 중 오류가 발생하였습니다.");

            return;
        }

        // 10) 다음 필터로 진행
        filterChain.doFilter(request, response);
    }




    /* SecurityContextHolder에 인증 객체를 세팅
    *
    * */
    private void setAuthenticationContext(
            HttpServletRequest request,
            String username,
            Collection<? extends GrantedAuthority> authorities
        ) {

        // 0) 사용자 아이디 (또는 고유 데이터)를 바탕으로 인증 토큰 생성
        // UsernamePasswordAuthenticationToken 클래스는 스프링 시큐리티에서 자주 쓰이는 "인증 토큰 구현체"
        // - 첫번째 인자 Principal (추후 해당 요청에서 파라미터 값으로 해당 값을 자동 추출해줌)
        // - 두번째 인자 Credentials (이미 토큰 검증을 마쳤으므로 null 전달해도 괜찮음, 중복 검증을 할 필요는 없음)
        // - 세번째 인자 권한목록
        //      >> "username이라는 사용자가 authorities 권한으로 인증됨" 상태가 됨

        // cf) 권한이 있는경우(비워지지 않은 경우) - isAuthenticated = true 값이 됨
        AbstractAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        // 요청에 대한 세부 정보 설정
        // : 생성된 인증 토큰에 요청의 세부 사항 설정(예: 원격 IP, 세션 ID 등을 저장함) -> 이걸 securityContext에 넣을거임
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 빈 SecurityContext 객체 생성 = 인증 토큰 주입
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationToken); // 방금 만든 인증 토큰을 달아줌

        // SecurityContextHolder 에 생성된 컨텍스트 설정
        // : 이후 컨트롤러나 서비스에서 SecurityContextHolder.getContext().getAuthentication() 으로 현재 사용자 정보를 꺼내 쓸 수 있음
        SecurityContextHolder.setContext(context);
    }

    /* USER/ADMIN -> "ROLE_USER" / "ROLE_ADMIN" 으로 매핑 */
    private List<GrantedAuthority> toAuthorities(Set<String> roles) {
        if (roles == null || roles.isEmpty()) return List.of(); // 권한이 없으면 빈 배열 반환

        return roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                // SimpleGrantedAuthority 로 시큐리티가 이해할 수 있는 타입으로 변환시킴
                .collect(Collectors.toList());

        // cf) "ROLE_" 첨부 이유
        //      스프링 시큐리티의 기본 hasRole("권한") 메서드는 내부적으로 ROLE_ 가 첨부된 권한 문자열을 찾으려 함
        //      그래서 접두사를 강제해두면 애플리케이션 전반에서 일관성 유지가 가능함

        // +) hasAuthority("권한") 는 명시된 문자열 그대로 권한을 확인하는 친구임 -> 자동으로 ROLE_ 이 붙혀짐
        //     그래서 굳이 "ROLE_" 첨부를 하지 않아도 됨 킹치만 hasAuthority나 toAuthorities나 둘 다 안정성을 위해선 그냥 ROLE을 붙히는게 맞는거 같음

    }

    /* 401 응답 헬퍼 (JSON) */
    private void unauthorized(HttpServletResponse response, String message) throws IOException{
        // HTTP 상태코드, 문자 인코딩 설정, 응답 본문 형식, JSON 문자열의 응답 본문을 정의 & 기록 하고있음

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.getWriter().write("""
                {"result": "fail","message":"%s"}
                """.formatted(message));
    }

}
