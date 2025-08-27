package com.example.k5_iot_springboot.repository;

import com.example.k5_iot_springboot.entity.G_User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface G_UserRepository extends JpaRepository<G_User, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<G_User> findByLoginId(String loginId);

    // roles의 지연로딩(LAZY)로 인한 LazyInitializationException 위험
    // 해결 방법 1) 리포지토리에 fetch-join 쿼리 추가: u.roles 컬렉션을 한번에 가져오기 때문에 N+1 문제를 방지함
    @Query("""
        select u from G_User u 
        left join fetch u.roles
        where u.loginId = :loginId    
    """)
    Optional<G_User> findWithRolesByLoginId(@Param("loginId") String loginId);

    // 해결 방법 2) JPA 의 @EntityGraph를 사용해서 fetch join을 자동으로 적용하는 방식
    //          - @EntityGraph: DATA JPA 에서 fetch 조인을 어노테이션으로 대신하는 기능


    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    @EntityGraph(attributePaths = "roles")
    Optional<G_User> findWithRolesById(
            @NotNull(message = "userId는 필수입니다.")
            @Positive(message = "userId는 양수여야합니다.")
            Long id
    );

//    Optional<G_User> findByNicknameAndEmail(String nickname, String email);
}
