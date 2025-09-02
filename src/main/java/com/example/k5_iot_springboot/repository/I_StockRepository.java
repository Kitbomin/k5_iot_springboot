package com.example.k5_iot_springboot.repository;

import com.example.k5_iot_springboot.entity.I_Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface I_StockRepository extends JpaRepository<I_Stock, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE) // 수정에 대한 처리 요구
    // 비관적 잠금(재고 경험 제어를 위함)
    // : 데이터를 수정하기 전에 해당 데이터에 대한 다른 트랜잭션의 접근을 막아 데이터의 정합성을 보장함
    // - 읽기 쓰기 모두 막는 역할임 
    @Query("""
        select s
        from I_Stock s
        where s.product.id = :productId
    """)
    Optional<I_Stock> findByProductIdForUpdate(@Param("productId") Long productId);


    Optional<I_Stock> findByProductId(Long productId);
}
