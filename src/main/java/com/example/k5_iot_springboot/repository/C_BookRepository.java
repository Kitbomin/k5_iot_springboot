package com.example.k5_iot_springboot.repository;

import com.example.k5_iot_springboot.entity.C_Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface C_BookRepository extends JpaRepository<C_Book, Long> {
}
