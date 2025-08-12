package com.example.k5_iot_springboot.service;

import com.example.k5_iot_springboot.dto.B_Student.StudentCreateRequestDto;
import com.example.k5_iot_springboot.dto.B_Student.StudentResponseDto;
import com.example.k5_iot_springboot.dto.B_Student.StudentUpdateRequestDto;
import com.example.k5_iot_springboot.entity.B_Student;

import java.util.List;

public interface B_StudentService {


    StudentResponseDto createStudent(StudentCreateRequestDto requestDto);

    List<StudentResponseDto> getAllStudents();

    StudentResponseDto getStudentById(Long id);

    StudentResponseDto updateStudent(Long id, StudentUpdateRequestDto requestDto);

    void deleteStudent(Long id);

    List<StudentResponseDto> filterStudentByName(String name);
}
