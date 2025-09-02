package com.example.k5_iot_springboot.entity;

import com.example.k5_iot_springboot.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table( name = "stocks",
        indexes = {@Index(name = "idx_stocks_product_id", columnList = "product_id")})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class I_Stock extends BaseTimeEntity { //재고가 바뀔 때마다 create,update 변화를 주기 위해서 상품 테이블에서 분리함

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull // 참조되는 값이 PK 값이기 때문에 비원질 수 없음
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "fk_stocks_product")) //FK 설정은 칼럼 내에서 설정함
    private I_Product product;

    @Min(0)
    @Column(nullable = false)
    private int quantity;

    @Builder
    private I_Stock(I_Product product) {
        this.product = product;
        this.quantity = 0;      // 재고 생성 시 - 수량이 초기화됨
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
