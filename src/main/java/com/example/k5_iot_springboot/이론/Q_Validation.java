package com.example.k5_iot_springboot.이론;

/*
* === Spring Boot Validation ===
* validation - 유효성
*
* 1. Validation?
*  : 유효성 검증
*   - 사용자가 입력한 값이 올바른 형식과 조건을 만족하는지 확인하는 과정
*   - 사용) jakarta.validation 표준 어노테이션 사용
*
* 2. Validation 사용 목적
*   - 잘못된 입력값(DTO) 에 대한 DB 저장 방지(데이터 무결성)
*   - 사용자의 잘못된 입력을 빠르게 알려줌으로써 UX 향상(User Experience: 사용자 경험)
*
* 3. Validation 사용 흐름
*   1) DTO (Data Transfer Object)에 어노테이션을 적용
*   2) Controller 에서 @Valid 또는 @Validation 과 함께 적용
*   3) 입력값이 조건을 만족하지 못하면 -> 자동 예외 발생 시킴(Method Argument Not Valid Exception)
*
*   +) @RestControllerAdvice 또는 GlobalExceptionHandler 로 처리 가능
*
*
* 4. 주요 Validation 어노테이션
*
*   +) 각 어노테이션은 message 속성을 가짐
*        - 검증 실패 시 사용자에게 보여줄 오류 메시지를 직접 지정하는 역할임
*
*
*   1) @NotNull
*       - 값이 Null이 아니어야함
*       - 단, 빈 문자열("") 은 허용됨
*
*   2) @NotEmpty | NotNull 보다 엄격함
*       - 값이 Null이 아니고 빈 문자열도 아님
*       - 단, 공백(" ") 은 허용됨
*
*   3) @NotBlank
*       - 값이 Null도 아니고, 빈 문자열도 아니고, 공백만 있는 경우도 아님
*       - 문자열 필드 검증 시 가장 많이 사용됨
*
*   cf) NonNull -> 롬복 꺼임 Validation꺼 아님
*       : Lombok 어노테이션
*       - null 값이 아니어야 함
*       - 반드시 초기화가 이루어져야하기 때문에 @RequiredArgsConstructor 와 주로 사용됨
*
*   4) @Size
*       - 문자열, 배열, List, Set 등의 길이(크기)를 제한함
*       - 내부에서  @Size(min = 5, max = 10) 이렇게 최대 최소값 설정도 가능함
*
*   5) @Min(0) / @Max(100)
*       - 숫자 범위를 지정함
*       - @Min(value = 0) @Max(value = 100) 이렇게 설정도 가능
*
*   6) @Email
*       - 이메일 형식을 검증하는 뇨속
*       - 자동검증이 됨
*
*   7) @Pattern
*       - 정규표현식으로 직접 패턴 검증이 가능함(사용자 정의 무결성을 위함)
*
*   8) @Positive, @PositiveOrZero, @Negative, @NegativeOrZero
*       - 양수, 응수만 허용 (0 포함 여부 선택 가능)
*
*   9) @Future, @Past
*       - 날짜 검증
*       - @Future: 예약일 같은 그런거 설정
*       - @Past : 생년월일같은 그런거 설정
*

* */


public class Q_Validation {
}
