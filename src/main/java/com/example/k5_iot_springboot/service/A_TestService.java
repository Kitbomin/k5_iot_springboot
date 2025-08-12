package com.example.k5_iot_springboot.service;


import com.example.k5_iot_springboot.entity.A_Test;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface A_TestService {
    A_Test createTest(A_Test test);

    List<A_Test> getAllTests();
}
