package com.example.k5_iot_springboot.repository;

import com.example.k5_iot_springboot.entity.I_Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface I_ProductRepository extends JpaRepository<I_Product, Long> {
}
