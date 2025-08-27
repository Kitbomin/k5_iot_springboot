package com.example.k5_iot_springboot.common.enums;

/** 시스템 내 역할(롤) 정의
 * 한명의 사용자가 여러개의 권한을 가질 수 있다 가정 */
public enum RoleType {
    USER,               // 일반 사용자
    MANAGER,            // 관리자 보조
    ADMIN               // 최상위 관리자
}
