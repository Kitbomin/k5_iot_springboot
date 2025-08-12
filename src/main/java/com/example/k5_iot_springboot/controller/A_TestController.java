package com.example.k5_iot_springboot.controller;


import com.example.k5_iot_springboot.entity.A_Test;
import com.example.k5_iot_springboot.service.A_TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Controller // A_TestController가 스프링 컨테이너에 등록되어있음
// : 웹 요청을 처리하는 클래스임을 명시함 (반환되는 데이터 타입이 유연함 -> JSP, Thymeleaf 등)

// @ResponseBody
// : 데이터를 반환활 때 HTTP 응답 본문에 직접 출력함 / 뷰 리졸버를 거치지 않음 / 데이터 직렬화 수행(JSON, XML 등)

@RestController
// : @Controller + @ResponseBody가 포함되어있음

@RequestMapping("/test")
// : 클라이언트의 특정 URI 요청이 올 때, 특정 클래스나 메서드와 연결시켜주는 어노테이션 (매핑을 담당함)
// >> @RequestMapping("URI 경로")

public class A_TestController {
    // controller: 클라이언트의 요청을 "처리"
    //      >>> service(비즈니스 로직)에 전달

    @Autowired // 필드 주입 방식: 필드에 해당 DS를 삽입 시켜줌 / 많이 쓰이진 않음
    A_TestService testService;


    // @메서드Mapping("추가 URI 지정")
    // : 메서드(POST/GET/PUT/DELETE) + localhost:8080/RequestMapping 경로/추가 URI
    @PostMapping  //어떤 동작을 할건지 명시 해줘야함
    public A_Test createTest(@RequestBody A_Test test) {
        A_Test result = testService.createTest(test);
        return result;
    }


    // 요청 구조: HTTP 메서드 + URI 경로 -> URI 자원에 어떠한 HTTP 동작을 실행할 것인지 명시 해서 요청함
    @GetMapping("/all")
    public List<A_Test> getAllTests() {
        List<A_Test> result = testService.getAllTests();
        return result;
    }

    @GetMapping("/{testId}")
    public A_Test getTestByTestId(@PathVariable Long testId) {
        A_Test result = testService.getTestByTestId(testId);
        return result;
    }


    @PutMapping("/{testId}")
    public A_Test updateTest(@PathVariable Long testId, @RequestBody A_Test test) {
        A_Test result = testService.updateTest(testId, test);
        return result;
    }

    @DeleteMapping("/{testId}")
    public void deleteTest(@PathVariable Long testId) {
        testService.deleteTest(testId);
    }


}
