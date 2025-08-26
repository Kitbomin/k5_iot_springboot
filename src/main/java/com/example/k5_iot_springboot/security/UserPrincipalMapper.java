package com.example.k5_iot_springboot.security;

import com.example.k5_iot_springboot.entity.G_User;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * === UserPrincipalMapper ===
 * : 엔티티(G_User) -> 보안 표현(UserPrincipal)로 변환시킴
 *
 * +) 현재 G_User 에는 roles가 없으므로 기본 ROLE_USER를 부여해줄거임
 *
 * >> 추후 역할/ 권한 도입 시 해당 클래스만 변경하면 전역 반영이 가능함
 * */

@Component
public class UserPrincipalMapper {

    @NonNull
    public UserPrincipal map(@NonNull G_User user) {
        Collection<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return UserPrincipal.builder()
                .id(user.getId())
                .username(user.getLoginId())
                .password(user.getPassword())
                .authorities(authorities)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }


}
