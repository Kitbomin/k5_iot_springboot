package com.example.k5_iot_springboot.dto.B_Student;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // 필수 - RequestDto의 데이터를 꺼내서 활용
@NoArgsConstructor // - Json을 객체로 변환할 때 기본생성자가 필요함
public class StudentCreateRequestDto {
    private String name;
    private String email;
}
