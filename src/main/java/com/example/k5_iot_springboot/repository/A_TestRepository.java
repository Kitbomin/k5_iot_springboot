package com.example.k5_iot_springboot.repository;

import com.example.k5_iot_springboot.entity.A_Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// A extends B : A와 B의 형태가 동일해야함 (클래스-클래스, 인터페이스-인터페이스)
// C implements D : C는 클래스, D는 인터페이스여야함


// === Repository === //
// : DB에 접근하는 객체 (DAO)
// - DB에 데이터를 읽고 쓰는 CRUD 담당 계층
// - JpaRepository 를 상속받음 <엔티티타입, PK 타입> 형태로 연결할 테이블을 명시해야함

// cf) Entity는 테이블 자체를 1:1로 매핑 (그냥 슬쩍 보고만 오는거)
//     Repository는 Entity 테이블의 CRUD 작업을 수행함 (직접적인 접근이 가능)

@Repository
public interface A_TestRepository extends JpaRepository<A_Test, Long> {
//    A_Test save(A_Test test); => 이거 이미 내장 되어있음 / 기본 CRUD가 내장되어있는 상태라 생각하면 됨
}
