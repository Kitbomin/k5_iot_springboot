package com.example.k5_iot_springboot.service;

import com.example.k5_iot_springboot.dto.G_Auth.response.SignInResponse;
import com.example.k5_iot_springboot.dto.G_User.request.RoleModifyRequest;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.security.UserPrincipal;
import jakarta.validation.Valid;

/** Admin의 권한 기능만 담은 비즈니스 로직 - UserRoleCommandService */
public interface G_AdminService {
    ResponseDto<Void> replaceRoles(UserPrincipal principal, @Valid RoleModifyRequest req);

    ResponseDto<SignInResponse> addRoles(UserPrincipal principal, @Valid RoleModifyRequest req);

    ResponseDto<Void> removeRoles(UserPrincipal principal, @Valid RoleModifyRequest req);
}
