package com.example.k5_iot_springboot.handler;

import org.springframework.web.bind.annotation.RestControllerAdvice;

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
public class GlobalExceptionHandler {
}
