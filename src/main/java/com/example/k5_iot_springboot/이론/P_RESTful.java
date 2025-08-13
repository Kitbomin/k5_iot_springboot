package com.example.k5_iot_springboot.이론;

/*
* === RESTful API
*
* 1. REST란?
*   REpresentation State Transfer
*   : 자원(Resource) 을 URI로 식별하고, HTTP 메서드를 이용해 해당 자원에 대한 행위를 정의하는 아키텍쳐 스타일
*
*   - 자원의 표현: JSON, XML 등
*   - 상태 전달: 요청(Request), 응답(Response) 으로 상태 전송
*   - HTTP 표준 메서드를 활용
*
*   >> 네트워크 상에서 클라이언트와 서버 간의 통신 방식 중 하나임
*
* 2. RESTful API?
*   : REST의 제약조건을 준수하는 API
*   - URI에 명사를 사용 (행위는 HTTP 메서드로 표현함)
*   - 계층 구조를 URI로 표현
*   - 대소문자, 언더바 사용 규칙을 준수해야함
*   - HTTP 상태코드로 결과를 명확하게 전달해야함
*
* 3. RESTful API 베스트 프렉티스
*
*   1) 명사 사용 - 리소스명을 동사가 아닌 명사로 작성
*       옳은 것) /users, /products, /carts, /meetings 등
*       아닌 것) /getUsers, /matchMembers ...
*
*   2) 소문자 사용 - 대문자 금지
*       옳은 것) /menus, /colors
*       아닌 것) /Menus, /Products ...
*
*   3) 언더바 사용 금지, 단어의 구분은 무조건 하이픈 사용
*       옳은 것) /user-profiles, /school-numbers
*
*   4) 마지막엔 슬래시 금지 -> URI 끝에 / 를 남기지 않음
*       옳은 것) /users/1, /products/search/name ...
*
*   5) 계층 구조를 표현해야할 것 -> 관계는 / 로 표현
*       옳은 것) /users/{userId}/orders/{orderId} - 특정 사용자의 주문들 중 특정 주문
*
*   6) 복수 형태의 명사를 사용 -> 특정 데이터는 복수형 명사 뒤 계층 구조로 표시할 것
*       옳은 것) /users, /posts, /books, /menus ...
*
*
*   +) 버저닝 사용 - API 의 시작은 api/v1 형태로 사용할 것
*
*
* === RESTful API 예시 ===
*
* 케이스 1) 사용자/인증
*   - 회원가입: POST /api/v1/auth/signup
*   - 로그인: POST /api/v1/auth/login
*   - 내 정보 조회: GET /api/v1/users/me (로그인 상태 - 로그인 한 자신의 정보 조회)
*   - 이메일 중복 체크: GET /api/v1/auth/check-email?email=XXX.... @RequestParam 으로
*
* 케이스 2) 쇼핑몰
*   - 상품 목록: GET /api/v1/products
*   - 상품 리뷰 조회: GET /api/v1/products/{productId}/reviews
*   - 장바구니: GET /api/v1/users/me/cart | 내 안에 장바구니가 여러개는 아니니까 단수 사용 가능함
*   - 주문 생성 POST /api/v1/orders
*
* 케이스 3) 게시판
*   - 게시글 목록: GET /api/v1/posts?category=notice&search=java&page=1&size=10
*       category=notice -> 공지만
*       &search=java    -> 자바만
*       &page=1         -> 1부터
*       &size=10        -> 10까지
*
*   - 게시글 수정: PUT /api/v1/posts/{postId}
*   - 댓글 추가: POST /pai/v1/posts/{postId}/comments
*
 *
*
*
* */


public class P_RESTful {
}
