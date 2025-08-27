package com.example.k5_iot_springboot.repository;

import com.example.k5_iot_springboot.entity.G_User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface G_UserRepository extends JpaRepository<G_User, Long> {

    @EntityGraph(attributePaths = "roles")
    // @EntityGraph: DATA JPA에서 fetch 조인을 어노테이션으로 대신하는 기능
    Optional<G_User> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

//    Optional<G_User> findByNicknameAndEmail(String nickname, String email);
}
