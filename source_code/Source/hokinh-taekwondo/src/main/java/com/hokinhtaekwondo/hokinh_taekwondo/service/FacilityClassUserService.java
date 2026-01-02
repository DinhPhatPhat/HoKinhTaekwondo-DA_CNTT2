package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClassUser.*;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.UserInClassResponseDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClassUser;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityClassRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityClassUserRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.UserRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.utils.ValidateRole;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityClassUserService {
    private final FacilityClassRepository facilityClassRepository;
    private final FacilityClassUserRepository facilityClassUserRepository;
    private final UserRepository userRepository;

    public void createFacilityClassUser(@Valid FacilityClassUserCreateDTO dto) {
        FacilityClassUser facilityClassUser = new FacilityClassUser();
        facilityClassUser.setUserId(dto.getUserId());
        facilityClassUser.setRoleInClass(dto.getRoleInClass());
        facilityClassUser.setIsActive(dto.getIsActive());
        FacilityClass facilityClass = facilityClassRepository.findById(dto.getFacilityClassId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp có ID = " + dto.getFacilityClassId()));

        facilityClassUser.setFacilityClass(facilityClass);
        facilityClassUserRepository.save(facilityClassUser);
    }


    public FacilityClassUser getById(Integer id) {
        return facilityClassUserRepository.findById(id).orElse(null);
    }

    public void updateFacilityClassUser(Integer id, @Valid FacilityClassUserUpdateDTO dto) {
        FacilityClassUser facilityClassUser = facilityClassUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi FacilityClassUser có ID = " + id));

        // Nếu có facilityClassId mới → cập nhật lớp
        if (dto.getFacilityClassId() != null) {
            FacilityClass facilityClass = facilityClassRepository.findById(dto.getFacilityClassId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp có ID = " + dto.getFacilityClassId()));
            facilityClassUser.setFacilityClass(facilityClass);
        }

        // Nếu có cập nhật role
        if (StringUtils.hasText(dto.getRoleInClass())) {
            facilityClassUser.setRoleInClass(dto.getRoleInClass());
        }

        // Nếu có cập nhật trạng thái
        if (dto.getIsActive() != null) {
            facilityClassUser.setIsActive(dto.getIsActive());
        }

        facilityClassUserRepository.save(facilityClassUser);
    }

    public void deleteFacilityClassUser(Integer id) {
        facilityClassUserRepository.deleteById(id);
    }

    public List<UserInClassResponseDTO> getActiveUsersByClassId(Integer classId) {
        List<FacilityClassUser> classUsers = facilityClassUserRepository.findByFacilityClass_IdAndIsActiveTrue(classId);

        return classUsers.stream()
                .map(classUser -> {
                    User user = userRepository.findById(classUser.getUserId()).orElse(null);
                    // Chỉ lấy user có tài khoản đang hoạt động
                    if (user == null || !Boolean.TRUE.equals(user.getIsActive())) return null;

                    UserInClassResponseDTO dto = new UserInClassResponseDTO();
                    dto.setId(user.getId());
                    dto.setName(user.getName());
                    dto.setPhoneNumber(user.getPhoneNumber());
                    dto.setDateOfBirth(user.getDateOfBirth());
                    dto.setEmail(user.getEmail());
                    dto.setAvatar(user.getAvatar());
                    dto.setRole(user.getRole());
                    dto.setIsActiveInClass(classUser.getIsActive());
                    dto.setBeltLevel(user.getBeltLevel());
                    dto.setFacilityId(
                            user.getFacility() != null ? user.getFacility().getId() : null
                    );

                    dto.setClassId(
                            classUser.getFacilityClass() != null ? classUser.getFacilityClass().getId() : null
                    );
                    dto.setRoleInClass(classUser.getRoleInClass());

                    return dto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    public List<UserInClassResponseDTO> getInActiveUsersByClassId(Integer classId) {
        List<FacilityClassUser> classUsers = facilityClassUserRepository
                .findByFacilityClass_IdAndIsActiveFalse(classId);

        return classUsers.stream()
                .map(classUser -> {
                    User user = userRepository.findById(classUser.getUserId()).orElse(null);
                    // Chỉ lấy user có tài khoản đang hoạt động, chỉ là không hoạt động trong lớp
                    if (user == null || !Boolean.TRUE.equals(user.getIsActive())) return null;

                    UserInClassResponseDTO dto = new UserInClassResponseDTO();
                    dto.setId(user.getId());
                    dto.setName(user.getName());
                    dto.setPhoneNumber(user.getPhoneNumber());
                    dto.setDateOfBirth(user.getDateOfBirth());
                    dto.setEmail(user.getEmail());
                    dto.setAvatar(user.getAvatar());
                    dto.setRole(user.getRole());
                    dto.setIsActiveInClass(classUser.getIsActive());
                    dto.setBeltLevel(user.getBeltLevel());
                    dto.setFacilityId(
                            user.getFacility() != null ? user.getFacility().getId() : null
                    );

                    dto.setClassId(
                            classUser.getFacilityClass() != null ? classUser.getFacilityClass().getId() : null
                    );
                    dto.setRoleInClass(classUser.getRoleInClass());

                    return dto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional
    public void bulkCreate(FacilityClassUserBulkCreateDTO dto, User creator) {
        // --- Kiểm tra lớp tồn tại ---
        FacilityClass facilityClass = facilityClassRepository.findById(dto.getFacilityClassId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp có ID = " + dto.getFacilityClassId()));

        User facilityManager = facilityClass.getFacility().getManager();
        if(!ValidateRole.isResponsibleForFacility(creator, facilityManager)) {
            throw new RuntimeException("Bạn không có quyền thêm người dùng trong lớp");
        }
        List<FacilityClassUser> toSave = new ArrayList<>();
        // Check if exist club head or manager
        List<String> userIds = new ArrayList<>();
        for(UserInClassDTO userDTO : dto.getUsers()) {
            userIds.add(userDTO.getUserId());
        }
        List<String> checkAdminExist = userRepository.existManagerOrClubHead(userIds);
        if(!checkAdminExist.isEmpty()) {
            throw new RuntimeException("Bạn không có quyền thêm các người dùng này");
        }
        // Add users into class
        for (UserInClassDTO userDto : dto.getUsers()) {
            if(userDto.getRoleInClass().equals("student")) {
                if(facilityClassUserRepository.existsByUserId(userDto.getUserId())) {
                    throw new RuntimeException("Võ sinh ID là " + userDto.getUserId() + " đã thuộc lớp khác. Chỉ có thể thêm võ sinh chưa thuộc lớp nào !");
                }
                else {
                    // if target is student, change facility to current facility
                    User user = userRepository.findById(userDto.getUserId()).orElse(null);
                    if(user == null) {
                        continue;
                    }
                    user.setFacility(facilityClass.getFacility());
                }
            }

            FacilityClassUser entity = new FacilityClassUser();
            entity.setFacilityClass(facilityClass);
            entity.setUserId(userDto.getUserId());
            entity.setRoleInClass(userDto.getRoleInClass());
            entity.setIsActive(true);

            toSave.add(entity);
        }

        facilityClassUserRepository.saveAll(toSave);
    }

    @Transactional
    public void bulkUpdate(FacilityClassUserBulkUpdateDTO dto,  User creator) {
        // --- Kiểm tra lớp tồn tại ---
        FacilityClass facilityClass = facilityClassRepository.findById(dto.getFacilityClassId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp có ID = " + dto.getFacilityClassId()));
        User facilityManager = facilityClass.getFacility().getManager();
        if(!ValidateRole.isResponsibleForFacility(creator, facilityManager)) {
            throw new RuntimeException("Bạn không có quyền thay đổi người dùng trong lớp");
        }
        List<FacilityClassUser> toSave = new ArrayList<>();

        for (UserInClassDTO userDto : dto.getUsers()) {
            FacilityClassUser entity = facilityClassUserRepository
                    .findByFacilityClassIdAndUserId(dto.getFacilityClassId(), userDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Cập nhật người dùng trong lớp không hợp lệ"));
            Optional<UserInClassDTO> safeUserDTO = Optional.of(userDto);
            if(!entity.getRoleInClass().equals("student")) {
                if(!userDto.getRoleInClass().equals("student")) {
                    entity.setRoleInClass(safeUserDTO.map(UserInClassDTO::getRoleInClass).orElse(entity.getRoleInClass()));
                }
            }
            entity.setIsActive(safeUserDTO.map(UserInClassDTO::getIsActiveInClass).orElse(entity.getIsActive()));

            toSave.add(entity);
        }

        facilityClassUserRepository.saveAll(toSave);
    }

    public Integer getCurrentClassIdOfUser(User user) {
        FacilityClassUser facilityClassUser =  facilityClassUserRepository.findFirstByUserIdAndIsActiveTrue(user.getId())
                .orElse(new FacilityClassUser());
        FacilityClass defaultFacilityClass = new FacilityClass();
        return Optional.ofNullable(facilityClassUser.getFacilityClass()).orElse(defaultFacilityClass).getId();
    }

    @Transactional
    public void deleteClassMembers(List<String> memIds, Integer classId, User creator) {
        FacilityClass facilityClass = facilityClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp có ID = " + classId));

        User facilityManager = facilityClass.getFacility().getManager();
        if(!ValidateRole.isResponsibleForFacility(creator, facilityManager)) {
            throw new RuntimeException("Bạn không có quyền xóa người dùng trong lớp");
        }
        facilityClassUserRepository.deleteAllByFacilityClass_IdAndUserIdIn(classId, memIds);
        for (String  memId : memIds) {
            User user = userRepository.findById(memId).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            if(user.getRole() == 4) {
                user.setFacility(null);
            }
        }
    }
}
