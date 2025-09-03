package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.repository.I_OrderRepository;
import com.example.k5_iot_springboot.service.I_OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 필드 OR @NonNull 필드란을 매개변수로 가지는 생성자
@Transactional(readOnly = true)
public class I_OrderServiceImpl implements I_OrderService {
    private final I_OrderRepository orderRepository;

}
