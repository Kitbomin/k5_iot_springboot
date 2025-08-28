package com.example.k5_iot_springboot.handler;

import com.example.k5_iot_springboot.common.enums.ErrorCode;
import com.example.k5_iot_springboot.common.errors.ErrorResponse;
import com.example.k5_iot_springboot.common.errors.FieldErrorItem;
import com.example.k5_iot_springboot.dto.ResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 보안 설정상의 예외를 처리할 수 없음

// @RestControllerAdvice: 해당 프로젝트 전역의 @RestController 에서 발생하는 예외를 처리해줌
/*
* 1. 단일 책임 원칙 (SRP)
*   - 예외 처리를 Controller가 아닌 GEH 에서 담당함
*
* 2. 재사용성 증가
*   - 모든 Controller에 대한 예외 처리가 한 곳에서 관리
*
* 3. 가독성 향상
* */
@RestControllerAdvice
// 스프링이 빈으로 등록해서 전역 예외가 처리 되도록 설정해줌
// 예외를 가로 채어 JSON 표준 응답을 반환해줌

@Slf4j
// lombok 어노테이션 - 로깅에 대한 추상 레이어를 제공하는 인터페이스 모음임
// 로그 기록을 함에 있어서 다양한 형식의 틀을 제공함

public class GlobalExceptionHandler {
    // == 공통 응답 생성 유틸 == //
    private ResponseEntity<ResponseDto<Object>> fail(
            ErrorCode code, String reason, List<FieldErrorItem> errors
            // : 실제 응답을 한 곳에서 조립함
    ) {
        // 1) reason값 설정: 비워질 경우 ErrorCode의 기본 메시지 값을 사용함
        String finalReason = (reason != null && !reason.isBlank()) ? reason : code.defaultMessage;

        // 2) 클라이언트가 해석할 수 있는 표준 에러 응답 본문 조립
        ErrorResponse body = ErrorResponse.of(code.code, finalReason, errors);

        return ResponseEntity.status(code.status).body(ResponseDto.setFailed(finalReason, body));
    }

    // == 검증(@Valid(@RequestBody) 실패 항목을 표준 형식으로 변환 == //
    private List<FieldErrorItem> toFieldErrors(MethodArgumentNotValidException e) {
        List<FieldErrorItem> list = new ArrayList<>(); // 수집용 리스트 -> 예외 발생하면 차곡차곡 담을거임


        // 필드 단위 검증 실패 항목 순회 -> DTO 단위 자체를 검증하겠다는 거 -> 그걸 예외객체 리스트에 담을거임
        e.getBindingResult().getFieldErrors().forEach(err -> {
            list.add(new FieldErrorItem(
                    err.getField(),                                                     // 실패한 필드명
                        Objects.toString(err.getRejectedValue(), null),     // 거부된 값(널이면 null)
                        err.getDefaultMessage()                                         // 제약 메시지
            ));
        });

        // 글로벌 에러 검증
        e.getBindingResult().getGlobalErrors().forEach(err -> {
            // 필드명 대신 오브젝트명 사용
            list.add(new FieldErrorItem(err.getObjectName(), "", err.getDefaultMessage()));
        });

        return list;
    }

    // === 400 Bad Request 그룹: 잘못된 인자/상태 (서비스 레벨 방어 예외 등...) === //
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class}) //세부적인 내용을 이걸로 처리함
    public ResponseEntity<ResponseDto<Object>> handleBadRequest(Exception e) {
        log.warn("Bad Request: {}" , e.getMessage());
        return fail(ErrorCode.BAD_REQUEST, null, null);
    }

    // === 400 Validation Error: @Valid @RequestBody 검증 실패 === //
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Object>> handleValidation(MethodArgumentNotValidException e) {
        log.warn("Validation failed: {}", e.getMessage());
        return fail(ErrorCode.VALIDATION_ERROR, null, toFieldErrors(e));
    }

    // === 401 ~~
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseDto<Object>> handleAuth(AuthenticationException e) {
        log.warn("UnAuthorized: {}", e.getMessage());
        return fail(ErrorCode.UNAUTHORIZED, null, null);
    }

    // === 403 Forbidden: 접근 거부 === //
    // AccessDeniedException.class => 접근에 대한 제어
    // AuthorizationDeniedException.class => 접근 권한에 대한 제어
    // => 이게 없으면 에러를 500번대로 받음 >> 접근권한 관련해서 에러 받아야하니까 400번대에서 받으려면 여기에 접근 권한 예외 넣어야함
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ResponseDto<Object>> handleAccessDenied(AccessDeniedException e) {
        log.warn("AccessDenied: {}", e.getMessage());
        return fail(ErrorCode.FORBIDDEN, null, null);
    }

    // === 404 NOT FOUND: 엔티티 조회 실패 === //
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseDto<Object>> handleNotFound(EntityNotFoundException e) {
        log.warn("Not Found: {}", e.getMessage());
        return fail(ErrorCode.NOT_FOUND, null, null);
    }

    // === 409 Conflict: 무결성 위반(중복/제약조건) === //
    @ExceptionHandler(DataIntegrityViolationException.class) //unique 키 충돌, FK 위반 등
    public ResponseEntity<ResponseDto<Object>> handleConflict(DataIntegrityViolationException e) {
        log.warn("Conflict: {}" , e.getMessage());
        return fail(ErrorCode.CONFLICT, null, null);
    }

    // === 500 Internal Server Error: 그 밖의 모든 예외에 대한 최종 안정망 === //
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handleException(Exception e) {
        log.error("Internal error", e);
        return fail(ErrorCode.INTERNAL_ERROR, null, null);
    }

}
