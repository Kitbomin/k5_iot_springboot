package com.example.k5_iot_springboot.provider;

/*
* === Jwt Provider ===
* : JWT(Json Web Token) 토큰을 생성하고 검증하는 역할
*
* cf) JWT
*   : 사용자 정보를 암호화된 토큰으로 저장
*   - 서버에 요청할 때마다 전달이 가능함 (사용자정보 확인용)
*   >> 주로 로그인 인증에 사용됨
*
*   +) HS256 암호화 알고리즘을 사용한 JWT 서명
*       - 비밀키는 Base64 로 인코딩 지정
*       - JWT 만료 기간은 10시간으로 지정 -> 만료시간은 로그인 유지에 사용됨
*           >> 환경변수 설정(jwt.secret/jwt.expiration)
* */

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
// cf)  @Component(클래스 레벨 선언) -> 스프링 런타임 시 컴포넌트 스캔을 통해 자동으로 빈을 찾고 등록함 (의존성 주입)
//      @Bean(메서드 레벨 선언) -> 반환되는 객체를 개발자가 수동으로 빈 등록해야함
public class JwtProvider {
    private static final String BEARER_PREFIX = "Bearer "; //removeBearer 에서 사용


    // 환경변수에 지정한 비밀키와 만료 시간 변수 선언

    private final Key key;

    private final int jwtExpirationMs;

    // 성능 / 안전 : 파서를 생성자에서 1회 구성하여 재사용할거임
//    private final JwtParser parser;


    public JwtProvider(
            @Value("${jwt.secert}") String secret,
            @Value("${jwt.expiration}") int jwtExpirationMs
    ) {
        // 생성자: JWTProvider 객체 생성 시 비밀키와 만료시간 초기화

        // 키 강도 검증(Base64 디코딩 후 256비트 이상 권장)
        byte[] secretBytes = Decoders.BASE64.decode(secret);
        if (secretBytes.length < 32) {
            // 32 bytes == 256 bits

            throw new IllegalArgumentException("jwt.secret은 항상 256 비트 이상을 권장합니다.");
        }

        // HMAC-SHA 알고리즘으로 암호화된 키 생성
        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.jwtExpirationMs = jwtExpirationMs;


    }

//    private final long j
}
