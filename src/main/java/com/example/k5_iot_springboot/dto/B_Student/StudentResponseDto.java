package com.example.k5_iot_springboot.dto.B_Student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter // 필수 - JSON 변환 시 필드 접근을 위해 필요함
@Builder // 서비스 컨트롤러에서 객체 생성할 때 가독성 증진을 위해
@AllArgsConstructor
public class StudentResponseDto {
    private Long id;
    private String name;
}
