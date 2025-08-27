package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.common.enums.RoleType;
import com.example.k5_iot_springboot.dto.G_Admin.request.RoleManageRequest;
import com.example.k5_iot_springboot.dto.G_Admin.response.RoleManageResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.entity.G_User;
import com.example.k5_iot_springboot.repository.G_UserRepository;
import com.example.k5_iot_springboot.security.UserPrincipal;
import com.example.k5_iot_springboot.service.G_AdminService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class G_AdminServiceImpl implements G_AdminService {
    private final G_UserRepository userRepository;


    @Override
    @Transactional
    public ResponseDto<RoleManageResponse.UpdateRolesResponse> replaceRoles(UserPrincipal principal, RoleManageRequest.@Valid UpdateRolesRequest req) {

        // 1) 갱신당할 사용자 정보 조회
        G_User user = userRepository.findWithRolesById(req.userId())
                .orElseThrow(() -> new EntityNotFoundException("해당 id의 유저가 없습니다.")) ;

        // 2) 전체 교체 - 요청에 @NotEmpty 이므로 최소 1개 이상의 권한을 보장함
        user.getRoles().clear();
        req.roles().forEach(user::addRole);

        // 갱신
        userRepository.flush();

        RoleManageResponse.UpdateRolesResponse data = new RoleManageResponse.UpdateRolesResponse(
                user.getId(),
                user.getLoginId(),

                Set.copyOf(user.getRoles()),
                // 복사해서 가져가는 이유 -> 방어적 복사
                // -> JPA 엔티티 컬렉션 필드를 DTO 내부에서 조작할 경우 캡슐화 깨짐 위험 발생 -> 그래서 읽기 전용모드로 가져와서 씀

                user.getUpdatedAt()
        );

        return ResponseDto.setSuccess("SUCCESS", data);
    }

    @Override
    @Transactional
    public ResponseDto<RoleManageResponse.AddRoleResponse> addRole(UserPrincipal principal, RoleManageRequest.@Valid AddRoleRequest req) {
        G_User user = userRepository.findWithRolesById(req.userId())
                .orElseThrow(() -> new EntityNotFoundException("해당 id의 사용자가 없습니다."));

        RoleType added = req.role();
        user.addRole(added);

        userRepository.flush(); //UpdatedAt 값이 바로 전달됨

        RoleManageResponse.AddRoleResponse data = new RoleManageResponse.AddRoleResponse(
                user.getId(),
                user.getLoginId(),
                added,
                Set.copyOf(user.getRoles()),
                user.getUpdatedAt()
        );

        return ResponseDto.setSuccess("SUCCESS", data);
    }

    @Override
    @Transactional
    public ResponseDto<RoleManageResponse.RemoveRoleResponse> removeRole(UserPrincipal principal, RoleManageRequest.@Valid RemoveRoleRequest req) {
        G_User user = userRepository.findWithRolesById(req.userId())
                .orElseThrow(() -> new EntityNotFoundException("해당 id의 사용자가 없습니다."));

        RoleType removed = req.role();
        user.removeRole(removed);

        // 비워지는 경우 기본 USER 권한은 유지해야함 (최소 1개 이상의 권한을 가질 것을 보장하는 정책)
        if (user.getRoles().isEmpty()) {
            user.addRole(RoleType.USER);
        }

        userRepository.flush(); //UpdatedAt 값이 바로 전달됨

        RoleManageResponse.RemoveRoleResponse data = new RoleManageResponse.RemoveRoleResponse(
                user.getId(),
                user.getLoginId(),
                removed,
                Set.copyOf(user.getRoles()),
                user.getUpdatedAt()
        );

        return ResponseDto.setSuccess("SUCCESS", data);
    }
}
