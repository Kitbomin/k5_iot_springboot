package com.example.k5_iot_springboot.entity;

import com.example.k5_iot_springboot.common.enums.Gender;
import com.example.k5_iot_springboot.common.enums.RoleType;
import com.example.k5_iot_springboot.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.usertype.UserType;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserEntity
 *  - 테이블(users)와 1:1 매칭
 *  - 생성/수정 시간은 BaseTimeEntity 에서 자동 세팅
 *  - UserDetails 책임은 분리(별도의 어댑터/매퍼가 담당)
 *  */


@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_login_id", columnNames = "login_id"),
                @UniqueConstraint(name = "uk_users_nickname", columnNames = "nickname")
            })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 외부에서 new 연산자 사용 방지, JPA 프록시/리플렉션 용도로 사용
// cf) 프록시(Proxy): 객체의 대리인 역할, 리플렉션(reflection): 객체의 정보를 동적으로 가져오고 조작하는 기술
public class G_User extends BaseTimeEntity {


    /** PK: 고유번호 */
    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 로그인 아이디(유니크) */
    @Column(name = "login_id" ,updatable = false, nullable = false, length = 50)
    private String loginId;

    /** 로그인 비밀번호 (해시 저장 권장 - 암호화) */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /** 이메일 (유니크)  */
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    /** 닉네임 (유니크) */
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    /** 성별(Enum) 선택값, Null 허용 가능 */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    /** 여러 권한 보유  */
//    @ElementCollection(fetch = FetchType.LAZY) // JWT에 roles를 저장하는 구조 - LAZY 가능
//    @CollectionTable(
//            name = "user_roles",
//            joinColumns =
//            @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_roles_user")),
//            uniqueConstraints = @UniqueConstraint(name = "uk_user_roles", columnNames = {"user_id", "role"})
//    )
//    @Column(name = "role", length = 30, nullable = false)
//    @Enumerated(EnumType.STRING)
//    private Set<RoleType> roles = new HashSet<>();

    /**
     * === 권한 컬렉션 생성 (조인 엔티티) ===
     * mappedBy = "user" : G_UserRole 엔티티 안의 user 필드가 연관관계의 주인임을 뜻함
     * cascade = CascadeType.ALL: G_UserRole 생성/삭제 전파
     * orphanRemoval = true: 컬렉션에서 제거되면 join row 삭제
     * */

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<G_UserRole> userRoles = new HashSet<>();


    /** 생성 편의 메서드 */
    @Builder
    private G_User(String loginId, String password, String email, String nickname, Gender gender) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;

        // userRoles는 서비스에서 부여할거기에 일단 주석
//        this.roles = (roles == null || roles.isEmpty()) ? new HashSet<>(Set.of(RoleType.USER)) : roles;
    }

    /** 변경/수정 메서드
     * 비밀번호 암호화 복호화 필요없으면 이 메서드도 필요 없음 */
    public void changePassword(String password) {
        this.password = password;
    }

    /** 변경/수정 메서드 */
    public void changeProfile(String nickname, Gender gender) {
        this.nickname = nickname;
        this.gender = gender;
    }

//    public void addRole(RoleType role) { this.roles.add(role); }
//    public void removeRole(RoleType role) { this.roles.remove(role); }


    // == 권한 부여/회수 편의 메서드 == //
    // 1. 이미 같은 Role이 있는 경우 중복추가되지 않게 설정
    public void grantRole(G_Role role) {
        boolean exists = userRoles.stream().anyMatch(ur -> ur.getRole().equals(role));

        if (!exists) {
            // 존재하지 않는 Role이 추가 된 경우(정상작동)
            userRoles.add(new G_UserRole(this, role));
        }

    }

    // 2. 권한 회수
    public void revokeRole(G_Role role) {
        // 현재의 권한을 순회해 삭제할 권한(매개변수값) 이 존재하면 삭제
        // 그렇지 않으면 삭제하지 않음
        userRoles.removeIf(ur -> ur.getRole().equals(role));
    }

    // 3. DTO 응답 생성 시 사용 - 응답 DTO 에서 권한 엔티티 자체를 넘기지 않고 단순화 된 역할 타입(RoleType - enum)만 전달
    // G_UserRole 타입 (권한 엔티티 전체) >> RoleType 타입 (enum 타입) 으로 변환
    // : 엔티티를 단순 enum 집합으로 변환하는 헬퍼 메서드임
    public Set<RoleType> getRoleTypes() {
        return userRoles.stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toUnmodifiableSet());
    }







}