package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.dto.Reservation.ReservationResponseDto;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.repository.ReservationRepository;
import com.example.k5_iot_springboot.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;

    @Override
    public ResponseDto<List<ReservationResponseDto>> getReservationsByTruck(Long truckId) {
        return null;
    }
}
