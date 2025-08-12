package com.example.k5_iot_springboot.이론;

/*
* === URI VS URL ===
*
* 1) URI (Uniform Resource Identifier)
*   : 웹의 자원을 식별(누구인지) 하는 이름표 -> 문자열로 표기됨
*   - 웹 페이지, 이미지, 파일, 서비스 "엔드 포인트"
*   -- 자원 자체를 가르킴 --
*
* 2) URL (Uniform Resource Locator)
*   : 그 자원이 어떻게, 어디로 가서 접근하는지 알려주는 주소 + 방법의 체계
*   - 자원의 위치를 나타내는 문자열
*   - 웹 주소를 의미함
*
*   cf) URL은 URI의 한 종류 (URI가 더 포괄적인 개념임)
*
* URL https://n.news.naver.com/mnews/article/366/0001099426?sort=asc
*   - https: 스킴 (프로토콜, 접근 방법)
*   - n.news.naver.com : 호스트 + 포트 (localhost:8080 이랑 비슷한 개념 -> 어느 컴퓨터인지, 어떤 서버인지, ...)
*   - mnews/article: 경로(path, 실질적인 자원을 나타냄)
*   - sort=asc: 쿼리(추가 조건)
*
*
* === @RequestMapping 은 URI 자원을 명시 ===
* : 해당 요청으로 어떤 자원에 접근할 것인지를 작성함
*
*
* === HTTP 메서드와 @RequestMapping ===
* @RequestMapping("/test") -> 클래스와의 연결을 의미함
* : http://localhost:8080/test -> 현재 test 컨트롤러와 연결됨
*  -> 그래서 클래스 내부의 내용에 접근하려면 아래 어노테이션들이 필요함
*
* @PostMapping
*
*
* @GetMapping("/all")
* : http://localhost:8080/test/all
*
* @GetMapping("/{id}")
* : http://localhost:8080/test/1
*   - 경로의 id 값을 사용해 데이터를 구분함
*
*
* @PutMapping
*
* @DeleteMapping("/{id}")
*
* */


public class J_URI {
}
