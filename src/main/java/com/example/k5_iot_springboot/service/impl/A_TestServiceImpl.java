package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.entity.A_Test;
import com.example.k5_iot_springboot.repository.A_TestRepository;
import com.example.k5_iot_springboot.service.A_TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // 생성자 주입 대신에 이것도 많이 씀
public class A_TestServiceImpl implements A_TestService {
    private final A_TestRepository testRepository;

//    //생성자 주입
//    public A_TestServiceImpl(A_TestRepository testRepository) { //new 연산자를 쓰지 않아도 됨!
//        this.testRepository = testRepository;
//    }

    @Override
    public A_Test createTest(A_Test test) {
        return null;
    }

    @Override
    public List<A_Test> getAllTests() {
        return List.of();
    }

    @Override
    public A_Test getTestByTestId(Long testId) {
        return null;
    }

    @Override
    public A_Test updateTest(Long testId, A_Test test) {
        return null;
    }

    @Override
    public void deleteTest(Long testId) {

    }
}
