package com.example.k5_iot_springboot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// ORM 에서 사용되는 주요 어노테이션들

// 1. @Entity
//  : 클래스를 DB 테이블과 매핑할 때 사용됨
//  - name 옵션 추가 가능 @Entity(name = "~")
//      : Entity 이름을 지정, 테이블명과 클래스명이 다를경우 반드시 명시해야함
//          EX) @Entity(name="test" // 테이블명은 test, A_Test는 클래스 명이됨
@Entity

// 2. @Table
//  : 클래스가 어떤 Table과 매핑되는 지를 명시함
//  - 생략 시 기본으로 클래스 이름이 테이블 명과 매핑됨
//  - name 옵션을 테이블에서 많이 사용함
@Table(name = "test")
@NoArgsConstructor
@Getter
@Setter

public class A_Test {
    //지속성 엔티티 'A_Test'에는 기본 키가 포함되어야 합니다.

    @Id
    // 1) @Id: 기본키 설정 어노테이션 (PK키)
    //      - 필드에 첨부함, 옵션 없이 사용 가능함 | 다른 어노테이션과 함께 기본키 생성 방식이나 타입 지정이 가능함

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 2) @GeneratedValue: MySQL의 AUTO_INCREMENT 에 맞춰 자동 증가됨
    // 데이터를 삽입하지 않고 자동으로 생성되는 친구 괄호 안에는 어떤 정책으로 갈건지 결정함

    @Column(name = "test_id", updatable = false)
    // 3) @Column: 필드를 특정 테이블의 열과 매핑해줌
    //      - 생략 시 기본으로 필드 이름이 열 이름으로 사용됨 | lowerCamelCase -> lower_snake_case 로 자동 변환됨
    //      +) 옵션이 디게 많음
    //          > name 옵션: 열 이름 지정
    //          > nullable 옵션: 열이 null 값을 허용할지의 여부를 설정함 (기본값이 True, null값 들어갈 수 있ㅇ므)
    //          > length 옵션: String 타입의 열 길이를 지정 (기본값: 255)
    //          > updatable 옵션: 열이 수정을 허용할지 여부 설정 (기본값: True)
    //          > unique 옵션: 해당 필드의 값이 유일해야 하는지의 여부를 지정함 (기본값: False)
    //      >> 각 옵션은 콤마, 로 구분해 나열 가능함
    private Long id;


    @Column(name = "name", nullable = false)
    private String name;


}

// JPA (ORM, 객체와 RDBMS를 연결함) VS MyBatis(SQL Mapper, SQL 중심의 접근)
