package com.example.k5_iot_springboot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // DB 테이블고 ㅏ매핑, JPA 엔티티임을 선언 (반드시 PK값 필요)
// : JPA는 엔티티를 통해 객체와 테이블 간 데이터를 자동 변환(ORM)

@Table(name = "students", uniqueConstraints = {
        // 2) 복합 컬럼 제약
        //      : name + email 조합이 동일하면 저장 불가
        @UniqueConstraint(columnNames = {"email", "name"})})
// name 옵션: 클래스명과 테이블명이 다를 경우 반드시 명시해야함
// cf) 클래스 명은 단수 명사, UpperCamelCase 사용 / DB명은 복수 명사, lower_snake_case 사용

@Getter
@Setter
@NoArgsConstructor //JPA는 엔티티 생성 시 기본 생성자가 필수임
public class B_Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @RequiredArgsConstructor 사용도 가능은 함 - final 또는 @NonNull 필드를 매개변수로 사용하는 생성자
    private String name;

    // 엔티티 UNIQUE 제약 조건
    // 1) 컬럼 단위 명시 - 기본 값이 false
    // 2) 테이블을 지정할 때 테이블에 지정하는 방법 @Table(name~, uniqueConstraints = {@UniqueConstraint(ColumnNames = {"email", 'name"})})
    @Column(unique = true)
    private String email;

    public B_Student(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
