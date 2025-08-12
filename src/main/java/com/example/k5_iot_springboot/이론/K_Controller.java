package com.example.k5_iot_springboot.이론;

// === Spring Controller 매핑에서 사용하는 주요 어노테이션 === //

/*
* 1. @PathVariable
*   : 경로 변수
*   - URI 자원 경로 자체에 포함된 변수를 매핑(연결)하여 받는 어노테이션
*
*   EX) GET "/books/{isbn}"
*       : 책들 중 해당 isbn의 책을 GET 가져오기
*       - isbn 값을 동적으로 가져올 수 있음
*
*       - 특정 리소스에 접근, 수정, 삭제에 사용됨
*
*   특징 - GET, PUT, DELETE 에 사용됨 (POST에 사용 안함)
*
*   1) 리소스를 특정할 수 있는 PK 값을 주로 사용하고
*   2) 경로 내에 {} 로 값을 감싸서 표현함
*   3) {} 내의 데이터가 @PathVariable 뒤의 매개변수와 매핑됨
*           >> Long isbn
*
*   cf) 옵션
*       : 변수명과 파라미터명이 다를 경우 @PathVariable("이름") <- 이렇게 명시 해줘야함
*       - @PathVariable("isbn") Long bookId
*
*
----------------------------------------------------------------
*
* 2. @RequestBody
*   : 클라이언트의 HTTP 요청 본문(Body)에 담긴 JSON, XML 데이터를 자바 객체로 변환해서 메서드의 파라미터로 매핑할 때 사용됨
*   - JSON, XML 형태의 데이터를 DTO 객체로 자동 변환해줌 (RequestDto)
*
*       @PostMapping
*   EX) public String createUser(@RequestBody UserCreateRequestDto dto) {...} 식으로 쓰임
*       - 주로 POST, PUT, DELETE 요청에 주로 사용됨 (GET 사용을 하지 않음)
*
*   주의점
*   1) 반드시 요청 본문이 존재해야함 -> 없으면 예외 발생함
*   2) 클라이언트는 "Content-Type: application/json" 헤더 설정을 해서 요청을 보내야 함
*   3) DTO 객체는 반드시 Getter/Setter 또는 @Data 가 필요함
*
*   - 복잡한 데이터 전송(객체 구조 필요), 민감한 데이터 전송에 사용됨
*       : URL에 노출되지 않고 Body에 숨겨 전송이 가능하기 때문임
*
----------------------------------------------------------------
*
* 3. @RequestParam
*   : 클라이언트가 보낸 URL의 쿼리 스트링 또는 폼 데이터를 메서드 파라미터로 바인딩 할 때 사용함
*   - URL에 노출되기 때문에 민감하지 않은 데이터에 적합함
*   - 주로 GET 요청에 사용됨
*
*   - 간단한 검색 조건, 필터링 & 페이징 기능 등 보안이 크게 중요하지 않은 데이터를 전달함
*
*   주의점
*   - 중요한 정보는 절대 담으면 안됨... 보안이 위험해애
*
*   cf) 옵션 정리
*       @RequestParam(required = true): 값이 없으면 오류 <기본값>
*       @RequestParam(required = false): 값이 없어도 허용(null 허용)
*       @RequestParam(defaultValue = "값"): 기본값 설정
* */



public class K_Controller {
}
