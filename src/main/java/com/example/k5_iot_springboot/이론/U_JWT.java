package com.example.k5_iot_springboot.이론;

/*
* === JWT (JSON Web Token)===
* : JSON 객체를 이용해 인증 정보를 안전하게 전달하는 토큰 기반 인증 방식
* - 서버가 클라이언트(사용자)에게 발급하는 디지털 서명이 된 토큰
*   >> 사용자의 인증 상태를 유지하고 서버에 전달 가능하도록 함
*
* - JWT Stateless(무상태성) 방식
*   >> 서버에 세션을 직접 저장하지 않음
*   +) 서버는 클라이언트 인증 상태를 메모리에 유지하지 않고, 클라이언트가 가진 토큰으로 인증을 진행함
*
* 1. JWT와 인증 / 인가
*   1) 인증 (Authentication)
*       : 인증(로그인) 시 서버는 사용자 정보를 기반으로 JWT 발급
*       - 이후 사용자는 요청마다 JWT를 서버에 전달해 인증
*
*   2) 인가
*       : JWT 사용자 권한 정보(ROLE, AUTHORITY 등) 가 포함될 수 있음
*       - 서버는 JWT를 해석해 해당 사용자가 어떤 API나 리소스에 접근할 수 있는지를 판단
*
*
* 2. JWT 인증 과정
*   1) 사용자가 로그인 시 서버는 JWT 발급
*   2) 사용자가 서버에 요청을 보낼 때 "HTTP 요청 헤더(Authorization)" 에 JWT 포함
*       "Authorization: Bearer <JWT 토큰값>
*   3) 서버는 전달받은 JWT를 검증해 요청 사용자의 인증 여부를 판단
*
*   cf) Bearer Token
*       : 소지자(Bearer)
*       - 토큰을 소지한 사람(클라이언트)이 곧 인증된 사용자임을 의미함
*       - OAuth 2.0 인증 방식 중 하나, API 요청 시 인증을 위해 사용됨
*       >> JWT == Bearer Token 으로 활용 (하는 시각도 있음)
*
* 3. JWT 구조 (Bearer Token 구조)
*   : 마침표를 기준으로 헤더(header).내용(payload).서명(signature)로 이루어짐
*       1) 헤더: 토큰타입, 해싱 알고리즘 정보 포함
*       2) 내용: 사용자 정보나 권한 같은 클레임(claim)을 포함
*           - Base64로 인코딩하기 때문에 암호화를 하진 않음 -> 중요한 정보의 저장을 삼가해야함
*       3) 서명: header나 payload를 조합하여 주어진 비밀 키로 서명한 값
*
* */

public class U_JWT {
}
