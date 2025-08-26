package com.example.k5_iot_springboot.controller;


import com.example.k5_iot_springboot.common.constants.ApiMappingPattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiMappingPattern.Boards.ROOT)
@RequiredArgsConstructor
@Validated
public class G_AuthController {
}
