package com.example.k5_iot_springboot.provider;

/*
* === Jwt Provider ===
* : JWT(Json Web Token) 토큰을 생성하고 검증하는 역할
*       >> 로그인 후 서버가 만들어서 클라이언트(브라우저)에게 전달하는 문자열 토큰
*
* cf) JWT
*   : 사용자 정보를 암호화된 토큰으로 저장
*   - (클라이언트가) 서버에 요청할 때마다 (해당 토큰)전달이 가능함 (사용자정보 확인용, 형식: [Authorization: Bearer <Token>] )
*   - 서버는 토큰을 검증하여 누가 요청했는지 판단할 수 있음
*   >> 주로 로그인 인증에 사용됨
*
*   +) JWT 구조
*       - header: 어떤 알고리즘으로 서명했는지
*       - payload: 사용자 정보(예: username, loginId, Roles ... )
*           -> 페이로드는 유저가 제공해야하기에 매개변수로 받아야함
*       - signature: 토큰 변조 방지용 서명
*
*       * 헤더랑 시그니처는 그냥 변조 방지용임 근데 payload가 워낙 중요하니까...
*
*   +) http 헤더 안에 JWT 가 들어가있음
*       헤더에 Authorization 파트 > 토큰 > JWT 가 들어가있음
*
*   +) HS256 암호화 알고리즘을 사용한 JWT 서명
*       - 비밀키는 Base64 로 인코딩 지정
*       - JWT 만료 기간은 10시간으로 지정 -> 만료시간은 로그인 유지에 사용됨
*           >> 환경변수 설정(jwt.secret/jwt.expiration)
*
*
*   # JwtProvider 클래스 전체 역할 #
*   1) 토큰 생성 (발급)                                 -> generateJwtToken
*       토큰은 요청 시 생성됨
*
*   2) Bearer 제거                                    -> removeBearer
*       토큰을 전달 할 때마다 실행해야함
*
*   3) 토큰 검증/파싱                                  -> parseClaimsInternal
*
*
*   4) payload 에 저장된 데이터 추출 (username, roles)  -> getUsernameFromJwt, getRolesFromJwt
*
*   5) 만료까지 남은 시간 계산                          -> getRemainingMillis
* */

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
// cf)  @Component(클래스 레벨 선언) -> 스프링 런타임 시 컴포넌트 스캔을 통해 자동으로 빈을 찾고 등록함 (의존성 주입)
//      @Bean(메서드 레벨 선언) -> 반환되는 객체를 개발자가 수동으로 빈 등록해야함
public class JwtProvider {
    // ==== 상수 필드 선언 ==== //

    /* Authorization의 접두사 */
    public static final String BEARER_PREFIX = "Bearer "; //removeBearer 에서 사용

    /* 커스텀 클레임 키 */
    public static final String CLAIM_ROLES = "roles";


    /* 서명용 비밀키, 액세스 토큰 만료시간(ms), 만료 직후 허용할 시계 오차(s) => application.properties에 선언함 */
    // 환경변수에 지정한 비밀키와 만료 시간 변수 선언
    private final SecretKey key;
    private final long jwtExpirationMs;
    private final int clockSkewSeconds;

    /* 검증이나 파싱을 하는 파서 역할 */
    // 검증 / 파싱 파서 : 파서를 생성자에서 1회 구성하여 재사용할거임 -> 성능/일관성 보장됨 (JJWT의 파서 객체)
    private final JwtParser parser;



    // === 생성자: 환경 변수로부터 설정을 주입 + 파서 준비 === //
    // 생성자: JWTProvider 객체 생성 시 비밀키와 만료시간 초기화
    public JwtProvider(
            // @Value: application.properties 나 application.yml 과 같은 설정 파일의 값을 클래스 변수에 주입하는 방식 * yml = 확장자명
            //          >> 데이터 타입을 자동 인식함
            @Value("${jwt.secert}") String secret,                      // cf) Base64 인코딩된 비밀키 문자열이어야한다는 전제조건이 붙음
            @Value("${jwt.expiration}") long jwtExpirationMs,
            @Value("${jwt.clock-skew-seconds:0}") int clockSkewSeconds  // 기본값 = 0 - 옵션
    ) {

        // 키 강도 검증(Base64 디코딩 후 256비트 이상 권장)
        byte[] secretBytes = Decoders.BASE64.decode(secret); // 바이트 배열로 변환함
        if (secretBytes.length < 32) {
            // 32 bytes == 256 bits
            // : HS256에 적당한 강도의 키를 강제해 보안을 강화함
            throw new IllegalArgumentException("jwt.secret은 항상 256 비트 이상을 권장합니다.");
        }

        // HMAC-SHA 알고리즘으로 암호화된 키 생성
        this.key = Keys.hmacShaKeyFor(secretBytes);             // HMAC-SHA 용 SecretKey 객체 생성함
        this.jwtExpirationMs = jwtExpirationMs;
        this.clockSkewSeconds = Math.max(clockSkewSeconds, 0);  // 최소값은 0보다 크다는걸 보장하는 음수 방지용

        this.parser = Jwts.parser()
                .verifyWith(this.key)                           // 해당 키로 서명 검증을 수행하는 파서(이후 파싱마다 반복 설정을 할 필요가 없음)
                .build();
    }

    /*
    * === 토큰 생성 ===
    * */

    /*
     * 액세스 토큰 생성
     * @Param username      sub(Subject) 에 저장할 사용자 식별자
     * @Param roles         권한 목록(중복 제거용 Set 권장) -> JSON은 Set을 인식하지 못함. 그래서 JSON 배열로 직렬화를 시켜야함
     *
     * subject=sub(username), roles는 커스텀 클레임 사용
     * */
    public String generateJwtToken(String username, Set<String> roles) {
        long now = System.currentTimeMillis(); // 토큰이 생성되는 시간을 측정
        Date iat = new Date(now);
        Date exp = new Date(now + jwtExpirationMs);
        // 더 깔끔하게 정리하면 위와 같이 할 수 있음

        // List로 변환하여 직렬화 시 타입 안정성이 확보됨
        List<String> roleList = (roles == null) ? List.of() : new ArrayList<>(roles);



        return Jwts.builder()
                // 표준 클레임 sub(Subject)에 사용자 아이디 (또는 고유 식별자) 설정
                .setSubject(username)                       // payload 저장
                .claim(CLAIM_ROLES, roleList)               // 커스텀 클레임 키에 권한 목록 저장(payload 내용임)
                .setIssuedAt(iat)                           // 표준 클레임의 현재 시간 설정 (발행 시간)
                .setExpiration(exp)                         // 현재시간에 만료시간을 더한 설정 (만료시간)
//                .signWith(key, SignatureAlgorithm.HS256)
                .signWith(key)                              // 비밀키를 서명(this.key = Keys.hmacShaKeyFor(secretBytes);)
                .compact(); // 빌더를 압축해 최종 JWT 문자열을 생성시킴
    }

    /*
     * === Bearer 처리 ===
     * */

    /* HTTP Authorization 헤더에서 "Bearer " 공백 제거 */
    public String removeBearer(String bearerToken) { // 입력의 형태는 "Bearer <Token>"
        if (bearerToken == null || !bearerToken.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Authorization 형식이 올바르지 않습니다.");
        }

        // subString 인자가 한 개인 경우: index 0 부터 인자값 "전" 까지 잘라내기
        return bearerToken.substring(BEARER_PREFIX.length()).trim(); //순수 토큰 반환

    }

    /*
     * === 검증 / 파싱  ===
     * */

    /* 내부 파싱(검증 포함) - 서명 검증 + 구조 검증 뒤 Claims(페이로드)를 반환
    *  만료 시 clock-skew 허용 옵션  */
    // 서명(sign Key)을 검증하고 payload(Claim) 을 반환해주는 역할을 함
    private Claims parseClaimsInternal(String token, boolean allowClockSkewOnExpiry) {
        // allowClockSkewOnExpiry: 만료 직후 허용 오차적용 여부
        try {
            // 파서 안썼으면 여기서 토큰 처리 했어야 했었음
            // 서명 및 기본 구조 검증 후 페이로드(Claims)만 추출해 반환함

            return parser.parseSignedClaims(token).getPayload();
            // 1) 토큰 서명 검증 (key로 signature 확인)
            // 2) JWT 기본 구조 검사(header, payload, signature가 맞는지)
            // 3) 성공 시 Claims 꺼내기 가능 (.getPayload() 가능)

        } catch (ExpiredJwtException ex) {
            // 토큰이 만료된 경우 발생하는 JJWT 전용 예외처리
            // : 만료 시간이 지난 토큰에도(예외 안에도) Claims 정보는 들어가있음
            // - 서버랑 클라이언트 사이의 시간 공백이 있을 수 있기에 오차를 보정

            if (allowClockSkewOnExpiry && clockSkewSeconds > 0 && ex.getClaims() != null) {
                // 호출부가 "만료직후 오차 허용" 을 활성화 하였고, 설정값이 0보다 큰지 확인
                Date exp = ex.getClaims().getExpiration(); // 만료 시각(exp) 추출

                if (exp != null) {
                    long skewMs = clockSkewSeconds * 1000L; // long 타입이니까 L, 허용 오차(초)를 밀리초로 변환함
                    long now = System.currentTimeMillis();

                    if (now - exp.getTime() <= skewMs) {
                        // 현재시각 - 만료시각 <= 허용오차 이내면 "방금 만료"했다고 간주
                        return ex.getClaims(); // 예외에서 Claims를 꺼내 그대로 유효한 것으로 반환
                    }
                }
            }
            throw ex; // 허용 오차 범위를 벗어나면 원래의 만료 예외를 다시 던짐

            // EX)  토큰 만료가 12:00:00 이고 서버가 12:00:45 로 45초 빠른 경우
            //      clockSkewSeconds가 60이기 때문에 유효한 요청으로 판단해 Claims 반환
        }

    }

    /*  토큰 유효성 검사(서명/만료 포함)
    *   clock-skew 허용 적용
    *
    * >> 컨트롤러/필터에서 사용가능한 토큰인지의 여부를 확인함
    * 반환값이 없고 확인만 해주는 친구
    * */
    public boolean isValidToken(String tokenWithoutBearer) {
        try {
            // 검증 - 서명 불일치, 변조, 포맷 이상, 만료(허용오차 초과) 등 모든 예외는 catch로 전달되어 false로 반환됨
            //  => 이걸 parser.parseSignedClaims(token).getPayload(); 가 해줌
            parseClaimsInternal(tokenWithoutBearer, true); // clock-skew 허용 적용
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    /* Claims 추출 (검증 포함)
    *   여기서 반환됨
    * */
    public Claims getClaims(String tokenWithoutBearer) {
        // 유효성 검사 + 파싱을 한번에 처리하고, payload(Claims)를 반환함
        return parseClaimsInternal(tokenWithoutBearer, true);
    }

    /* 실제 페이로드값(Claims 값 추출)
    *  : sub               - .getSubject()
    *  : 사용자 커스텀 클레임 - .get("클레임명")
    * */
    public String getUsernameFromJwt(String tokenWithoutBearer) {
        return getClaims(tokenWithoutBearer).getSubject();
    }


    /* roles >> Set<String> 변환 작업
    *  List여도, Set이어도 변환 가능하게 만듬
    * */
    @SuppressWarnings("unchecked") //제네릭 캐스팅 경고 억제 (런타임 타입 확인으로 보완)
    public Set<String> getRolesFromJwt(String tokenWithoutBearer) {
        // get("roles")로 커스텀 클레임을 가져오면, JSON 파싱 결과가 List로 반환이 일반적임
        //          >> 문자열 집합(Set<String>) 으로 표준화해서 반환함
        Object raw = getClaims(tokenWithoutBearer).get(CLAIM_ROLES); // 보통은 List로 나오는게 일반적임
        if (raw == null) return Set.of(); // 권한 없음

        if (raw instanceof List<?> list) {
            Set<String> result = new HashSet<>(); // 중복 제거 목적
            for (Object o: list) if (o != null) result.add(o.toString());
            return result;
        }

        if (raw instanceof Set<?> set) {
            Set<String> result = new HashSet<>();
            for (Object o: set) if (o != null) result.add(o.toString());
            return result;
        }

        return Set.of(raw.toString());
    }

    /* 남은 만료 시간(ms)이 음수면 이미 만료 */
    public long getRemainingMillis (String tokenWithoutBearer) {
        Claims c = parseClaimsInternal(tokenWithoutBearer, true);
        return c.getExpiration().getTime() - System.currentTimeMillis();
    }



}
