package com.example.k5_iot_springboot.controller;


import com.example.k5_iot_springboot.dto.G_Auth.request.FindIdRequest;
import com.example.k5_iot_springboot.dto.G_Auth.request.SignInRequest;
import com.example.k5_iot_springboot.dto.G_Auth.request.SignUpRequest;
import com.example.k5_iot_springboot.dto.G_Auth.response.FindIdResponse;
import com.example.k5_iot_springboot.dto.G_Auth.response.SignInResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.service.G_AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth") // 토큰없이 처리해주겠다는 곳이니까 회원가입, 로그인, 아이디찾기, 비밀번호 재설정 등을 여기에 배치함
@RequiredArgsConstructor
public class G_AuthController {
    private final G_AuthService authService;


    /** 회원가입 */
    @PostMapping("/sign-up")
    public ResponseEntity<ResponseDto<Void>> signUp(@Valid @RequestBody SignUpRequest req) {
        authService.signUp(req);
        return ResponseEntity.ok(ResponseDto.setSuccess("회원가입이 완료되었습니다.", null));
    }



    /** 로그인 */
    @PostMapping("/sign-in") // Post는 조회도 할 수 있고 저장도 할 수 있는 유연한 Http 메서드임 그래서 앵간하면 로그인은 Post 를 씀
    public ResponseEntity<ResponseDto<SignInResponse>> signIn(@Valid @RequestBody SignInRequest req) {
        ResponseDto<SignInResponse> response = authService.signIn(req);
        return ResponseEntity.ok().body(response);
    }


    /** 이메일 전송 */
    @PostMapping("/send-email")
    public ResponseEntity<ResponseDto<Void>> sendEmail(@Valid @RequestBody SendMailRequestDto req) {
        authService.senEmail(req);

        return ResponseEntity.noContent().build();
    }
    /** 이메일 인증 */
    @GetMapping("/verify")
    public ResponseEntity<ResponseDto<Void>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);

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
    public ResponseEntity<ResponseDto<Void>> resetPassword(@Valid @RequestBody PasswordResetResponseDto req) {
        authService.resetPassword(req);
        return ResponseEntity.noContent().build();
    }

}
