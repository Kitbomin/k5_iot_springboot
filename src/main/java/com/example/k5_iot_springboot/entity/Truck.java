package com.example.k5_iot_springboot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trucks")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Setter
public class Truck {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // 트럭을 운영하는 사용자 ID
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_trucks_user"))
    private G_User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String category;

    @Column(length = 50)
    private String region;

    @Column(length = 255)
    private String description;

    // == 예약 리스트 == //
    // 연관 관계 주인이 아님을 뜻하는 메서드 : mappedBy => 반대편 엔티티의 연관관계 필드 이름임
    // - 주인) FK를 직접 관리하는 쪽: @JoinColumn을 가진 쪽
    // - 비주인) mappedBy를 가진 쪽: DB에는 컬럼이 없음 (읽기 전용)
    @OneToMany(mappedBy = "truck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setTruck(this);
    }
}
