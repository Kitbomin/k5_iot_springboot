package com.example.k5_iot_springboot.service.impl;

import com.example.k5_iot_springboot.common.enums.RoleType;
import com.example.k5_iot_springboot.dto.G_Admin.request.RoleManageRequest;
import com.example.k5_iot_springboot.dto.G_Admin.response.RoleManageResponse;
import com.example.k5_iot_springboot.dto.ResponseDto;
import com.example.k5_iot_springboot.entity.G_Role;
import com.example.k5_iot_springboot.entity.G_User;
import com.example.k5_iot_springboot.repository.G_RoleRepository;
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
    private final G_RoleRepository roleRepository;


    @Override
    @Transactional
    public ResponseDto<RoleManageResponse.UpdateRolesResponse> replaceRoles(UserPrincipal principal, RoleManageRequest.@Valid UpdateRolesRequest req) {

        // 1) 갱신당할 사용자 정보 조회
        G_User user = userRepository.findWithRolesById(req.userId())
                .orElseThrow(() -> new EntityNotFoundException("해당 id의 유저가 없습니다.")) ;

        // 2) 전체 교체 - 요청에 @NotEmpty 이므로 최소 1개 이상의 권한을 보장함
        user.getUserRoles().clear();
//        req.roles().forEach(user::addRole);

        // 갱신
        userRepository.flush();

        RoleManageResponse.UpdateRolesResponse data = new RoleManageResponse.UpdateRolesResponse(
                user.getId(),
                user.getLoginId(),

                Set.copyOf(user.getRoleTypes()),
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

//        RoleType added = req.role();
//        user.addRole(added);

        G_Role role = roleRepository.findById(req.role())
                        .orElseThrow(() -> new EntityNotFoundException("해당 권한을 찾을 수 없ㅅ브니다."));
        user.grantRole(role);

        userRepository.flush(); //UpdatedAt 값이 바로 전달됨

        RoleManageResponse.AddRoleResponse data = new RoleManageResponse.AddRoleResponse(
                user.getId(),
                user.getLoginId(),
                req.role(),
                Set.copyOf(user.getRoleTypes()),
                user.getUpdatedAt()
        );

        return ResponseDto.setSuccess("SUCCESS", data);
    }

    // 권한 제거(권한 회수 - 최소 1개 권한 보장: 모두 제거되면 USER 유지)
    @Override
    @Transactional
    public ResponseDto<RoleManageResponse.RemoveRoleResponse> removeRole(UserPrincipal principal, RoleManageRequest.@Valid RemoveRoleRequest req) {
        G_User user = userRepository.findWithRolesById(req.userId())
                .orElseThrow(() -> new EntityNotFoundException("해당 id의 사용자가 없습니다."));

//        RoleType removed = req.role();
//        user.removeRole(removed);

//        if (user.getRoles().isEmpty()) {
//            user.addRole(RoleType.USER);
//        }

        G_Role role = roleRepository.findById(req.role())
                .orElseThrow(() -> new EntityNotFoundException("해당 권한을 찾을 수 업습니다."));

        user.revokeRole(role); // orphanRemoval = true -> 고아 객체 제거됨

        userRepository.flush(); //UpdatedAt 값이 바로 전달됨

        // 비워지는 경우 기본 USER 권한은 유지해야함 (최소 1개 이상의 권한을 가질 것을 보장하는 정책)
        if (user.getUserRoles().isEmpty()) {
            user.grantRole(roleRepository.getReferenceById(RoleType.USER));
        }


        RoleManageResponse.RemoveRoleResponse data = new RoleManageResponse.RemoveRoleResponse(
                user.getId(),
                user.getLoginId(),
                req.role(),
                Set.copyOf(user.getRoleTypes()),
                user.getUpdatedAt()
        );

        return ResponseDto.setSuccess("SUCCESS", data);
    }
}
