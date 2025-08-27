package com.example.k5_iot_springboot.service;

import com.example.k5_iot_springboot.dto.G_Auth.request.FindIdRequest;
import com.example.k5_iot_springboot.dto.G_Auth.request.SignInRequest;
import com.example.k5_iot_springboot.dto.G_Auth.request.SignUpRequest;
import com.example.k5_iot_springboot.dto.G_Auth.response.FindIdResponse;
import com.example.k5_iot_springboot.dto.G_Auth.response.SignInResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import jakarta.validation.Valid;

import java.util.List;

public interface G_AuthService {
    void signUp(@Valid SignUpRequest req);

    ResponseDto<SignInResponse> signIn(@Valid SignInRequest req);

    ResponseDto<List<FindIdResponse.UsernameResponse>> findId(FindIdRequest req);
}
