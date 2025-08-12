package com.example.k5_iot_springboot.controller;

import com.example.k5_iot_springboot.entity.B_Student;
import com.example.k5_iot_springboot.service.B_StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

// cf) RESTful API => REST API를 잘 따르는 아키텍처 타입

// cf) RequestMapping 베스트 프렉틱스
//      : 주로 버저닝(/api/v1) + 복수 형태의 명사(/students) 같이 사용


@RestController // @Controller + @ResponseBody (RESTful 웹 서비스의 컨트롤러임을 명시함)
@RequestMapping("/api/v1/students") // 해당 컨트롤러의 공동 URL prefix (접두사) (아래 메서드 경로는 모두 /students 로 시작된다는 뜻)
@RequiredArgsConstructor
public class B_StudentController {

    // 비즈니스 로직을 처리하는 service 객체 주입 (생성자 주입)
    private final B_StudentService studentService;

    // 응답과 요청에 대한 데이터 정의 (Request, Response)

    // 1) 새로운 학생 등록(POST)
    // - 성공 201 Created + Location 헤더 사용 (/students/{id}) + 생성데이터
    // cf) 리소스 생성 성공은 201 Created가 표준임
    @PostMapping
    public ResponseEntity<B_Student> createStudent(@RequestBody B_Student student, UriComponentsBuilder uriComponentsBuilder) {
        B_Student created = studentService.createStudent(student);

        // Location 헤더 생성
        // : 서버의 응답이 다른 곳에 있음을 알려주고 해당 위치 (URI)를 지정
        // - 리다이렉트 할 페이지의 URL을 나타냄
        // - 201, 3xx (Redirect 시) 응답 상태와 주로 사용됨
        URI location = uriComponentsBuilder // 현재 HTTP 요청의 정보를 바탕으로 설정함
                .path("/{id}") // 현재 경로 + /{id}
                .buildAndExpand(created.getId()) // 템플릿 변수 치환 - 동적 데이터 처리에 사용함
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public B_Student findeAll(){}

    @GetMapping





}
