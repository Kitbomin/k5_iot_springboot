package com.example.k5_iot_springboot.controller;


import com.example.k5_iot_springboot.dto.G_Auth.request.FindIdRequest;
import com.example.k5_iot_springboot.dto.G_Auth.request.SignInRequest;
import com.example.k5_iot_springboot.dto.G_Auth.request.SignUpRequest;
import com.example.k5_iot_springboot.dto.G_Auth.response.FindIdResponse;
import com.example.k5_iot_springboot.dto.G_Auth.response.SignInResponse;
import com.example.k5_iot_springboot.dto.J_Mail.MailRequest;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.security.UserPrincipal;
import com.example.k5_iot_springboot.service.G_AuthService;
import com.example.k5_iot_springboot.service.J_MailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth") // 토큰없이 처리해주겠다는 곳이니까 회원가입, 로그인, 아이디찾기, 비밀번호 재설정 등을 여기에 배치함
@RequiredArgsConstructor
public class G_AuthController {
    private final G_AuthService authService;
    private final J_MailService mailService;


    /** 회원가입 */
    @PostMapping("/sign-up")
    public ResponseEntity<ResponseDto<Void>> signUp(@Valid @RequestBody SignUpRequest req) {
        authService.signUp(req);
        return ResponseEntity.ok(ResponseDto.setSuccess("회원가입이 완료되었습니다.", null));
    }

    /** 로그인 */
    // 액새스 토큰 + 리프레쉬 토큰도 같이 발급해줌
    @PostMapping("/sign-in")
    // Post는 조회도 할 수 있고 저장도 할 수 있는 유연한 Http 메서드임 그래서 앵간하면 로그인은 Post 를 씀
    public ResponseEntity<ResponseDto<SignInResponse>> signIn(
            @Valid @RequestBody SignInRequest req,
            HttpServletResponse response) {
        ResponseDto<SignInResponse> result = authService.signIn(req, response);
        return ResponseEntity.ok().body(result);
    }

    /** 로그 아웃 (RefreshToken 쿠키 삭제) */
    @PostMapping("/sign-out")
    public  ResponseEntity<?> logout(
            HttpServletResponse response,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) throws IOException {
        // 인증 정보 확인
        if (userPrincipal == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=URF-8");
            response.getWriter().write("""
                    {"success": false, "message": "로그인 정보가 없습니다.}
                    """);
            return ResponseEntity.status(401).body(null);
        }
        authService.deleteRefreshToken(userPrincipal);


        // 쿠키 즉시 만료 처리
        // jakarta.servlet.http.Cookie
        // : 웹 서버가 웹 브라우저에 저장하도록 보내는 정보 조각
        // -> 웹 서버가 직접적으로 브라우저에 접근하는거임
        // - 첫번재 인자(키), 두번째 인자(값)를 통해 정보를 전달함
        // - 자바 서블릿 환경에서 쿠키를 다루기 위한 클래스임
        var cookie = new jakarta.servlet.http.Cookie("refreshToken", null);
        cookie.setHttpOnly(true); // 자바스크립트가 쿠키에 접근 못하게 함 => 쿠키 설정 보안 강화 -> 쿠키의 JS 접근 여부 설정 => true는 접근 불가임
        // cookie.setSecure(true); // 쿠키 설정 보안 강화 -> HTTPS 통신 환경에서만 서버로 전송됨
        cookie.setPath("/"); // 쿠키의 적용 범위를 특정 경로로 제한
        cookie.setMaxAge(0); // 쿠키 유효 시간 설정 - 0: 즉시 삭제
        response.addCookie(cookie);


        return ResponseEntity.ok(ResponseDto.setSuccess("로그아웃 성공", null));
    }

    /** Refresh Token 검증 및 Access 토큰 재발급 */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        try {
            // 1) 클라이언트 요청 쿠키에서 RefreshToken 추출
            String refreshToken = extractRefreshTokenFromCookie(request);
            if (refreshToken == null) {
                return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "message", "Refresh Token이 존재하지 않습니다."
                ));
            }
            // 2) Refresh Token 검증 후 새 Access Token 발급
            String newAccessToken = authService.refreshAccessToken(refreshToken);

            // 3) JSON 형식으로 응답 반환
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("accessToken", newAccessToken)
            ));
        } catch (Exception e) {
            // 검증 실패 -> 401 Unauthorized 반환
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /** private helper 메서드: 요청 쿠키에서 RefreshToken 쿠키를 찾아 반환 */
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }


    /** 이메일 전송 */
    @PostMapping("/send-email")
    public ResponseEntity<ResponseDto<Void>> sendEmail(@Valid @RequestBody MailRequest.SendMail req) {
        mailService.sendEmail(req);

        return ResponseEntity.noContent().build();
    }


    /** 이메일 인증 */
    @GetMapping("/verify")
    public ResponseEntity<ResponseDto<Void>> verifyEmail(@RequestParam String token) {
        mailService.verifyEmail(token);

        return ResponseEntity.noContent().build();
    }


    /** 아이디 찾기 */
    @PostMapping("/find-id")
    public ResponseEntity<ResponseDto<FindIdResponse>> findId(@Valid @RequestBody FindIdRequest req) {
        ResponseDto<FindIdResponse> response = authService.findId(req);
        return ResponseEntity.ok().body(response);
    }

    /**비밀번호 재설정*/
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDto<Void>> resetPassword(@Valid @RequestBody MailRequest.PasswordReset req) {
        authService.resetPassword(req);
        return ResponseEntity.noContent().build();
    }

}
