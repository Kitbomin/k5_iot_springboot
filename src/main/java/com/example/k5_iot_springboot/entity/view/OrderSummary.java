package com.example.k5_iot_springboot.entity.view;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

/**
 * order_summary 뷰 매핑용 읽기 전용 엔티티
 * : 조인 결과(행 단위) 제공
 * - 리포트/목록에 활용됨
 * -> LIST 컬렉션 프레임워크에 담길지도
 * */

@Entity
@Table(name = "order_summary")
@Getter
@NoArgsConstructor
@Immutable // 뷰 읽기 전용 선언
public class OrderSummary {
    @Id @Column(name = "order_id")
    private Long orderId;

    // 뷰 컬럼은 컬럼명 그대로 쓰기도 함

    private Long user_id;           // userId
    private String order_status;    // 문자열 칼럼( 필요시 enum 변환은 서비스에서 진행 )
    private String product_name;    // 제품 이름
    private Integer quantity;       // 주문 수량
    private Integer price;          // 제품 가격

    private Long total_price;    // (oi.quantity * p.price) AS total_price

    @Column(name = "ordered_at")
    private LocalDateTime orderedAt;  // 생성 시각
}
