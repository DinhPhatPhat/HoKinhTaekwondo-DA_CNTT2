package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.UserCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.UserInClassResponseDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.UserUpdateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final JwtService jwtService;

    // --- Create ---
    public UserInClassResponseDTO create(UserCreateDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // TODO: mã hóa password sau
        user.setRole(dto.getRole());
        user.setBeltLevel(dto.getBeltLevel());

        user.setFacility(facilityRepository.findById(dto.getFacilityId()).orElse(null));
        User saved = userRepository.save(user);
        return toResponseDTO(saved);
    }

    // --- Update ---
    public void update(UserUpdateDTO dto) throws IOException {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("không tìm thấy người dùng cần cập nhật: " + dto.getId()));
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getDateOfBirth() != null) {
            user.setDateOfBirth(dto.getDateOfBirth());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(dto.getPassword()); // TODO: mã hoá
        }

        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }

        if (dto.getBeltLevel() != null && !dto.getBeltLevel().isBlank()) {
            user.setBeltLevel(dto.getBeltLevel());
        }

        if (dto.getIsActive() != null) {
            user.setIsActive(dto.getIsActive());
        }

        if (dto.getFacilityId() != null) {
            user.setFacility(
                    facilityRepository.findById(dto.getFacilityId())
                            .orElseThrow(EntityNotFoundException::new)
            );
        }

        if (dto.getAvatar() != null && !dto.getAvatar().isBlank()) {
            user.setAvatar(dto.getAvatar());
        }
        try {
            userRepository.save(user);
        }
        catch (Exception e)
        {throw new RuntimeException(e);}
    }

    // --- Get by Id ---
    public User getById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    // --- Get all ---
    public List<UserInClassResponseDTO> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }

    // Wrong id or password -> 0
    // Correct email and password and is inactive-> 1
    // Correct email and password and is active-> 2
    public int checkLogin(String id, String password){
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent() && user.get().getPassword().equals(password)){
            if(user.get().getIsActive()){
                return 2;
            }
            else{
                return 1;
            }
        }
        return 0;
    }

    public boolean isManagerOfFacility(String currentUserId, int facilityId) {
        Facility facility = facilityRepository.findById(facilityId).orElse(null);
        if (facility == null) {
            return true;
        }
        return facility.getManager().getId().equals(currentUserId);
    }


    public String upLoadImage(MultipartFile imageFile, String userId) throws IOException {

        String uploadDir = "uploads/image/user/";
        String accessPath = "/uploads/image/user/";

        return UploadService.upLoadImage(imageFile, userId, uploadDir, accessPath);
    }

    public User getCurrentUser(HttpSession session, String token) {
        // Check session
        User userFromSession = (User) session.getAttribute("user");
        if (userFromSession != null) {
            return userFromSession;
        }

        // If session is not exists, then check cookie
        if (token != null && jwtService.isTokenValid(token)) {
            // Encrypt id from token
            String id = jwtService.extractUserId(token);
            if (id != null) {
                // Take user from id
                User user = userRepository.findById(id).orElse(null);
                // If user found, create session
                if (user != null) {
                    session.setAttribute("user", user);  // Create session
                    return user;
                }
            }
        }
        // If session and cookie are not exist, then return null
        return null;
    }

    // --- Mapper ---
    private UserInClassResponseDTO toResponseDTO(User user) {
        UserInClassResponseDTO dto = new UserInClassResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setRole((user.getRole()));
        dto.setBeltLevel(String.valueOf(user.getBeltLevel()));
        dto.setFacilityId(user.getFacility().getId());
        return dto;
    }

    @Transactional
    public List<User> bulkCreateUsers(List<UserCreateDTO> userList) {

        if (userList == null || userList.isEmpty()) {
            throw new IllegalArgumentException("Danh sách người dùng không được trống.");
        }

        List<User> usersToSave = new ArrayList<>();

        for (UserCreateDTO dto : userList) {
            User user = new User();
            user.setId(dto.getId());
            user.setName(dto.getName());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setDateOfBirth(dto.getDateOfBirth());
            user.setEmail(dto.getEmail());
            user.setAvatar(dto.getAvatar());
            user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            user.setRole(dto.getRole());
            user.setBeltLevel(dto.getBeltLevel());
            user.setIsActive(true);
            user.setPassword(dto.getPassword());

            // --- Liên kết cơ sở (Facility) ---
            if (dto.getFacilityId() != null) {
                Facility facility = facilityRepository.findById(dto.getFacilityId())
                        .orElseThrow(() ->
                                new IllegalArgumentException("Không tìm thấy cơ sở có ID = " + dto.getFacilityId()));
                user.setFacility(facility);
            } else {
                throw new IllegalArgumentException("Mỗi người dùng phải có FacilityId hợp lệ.");
            }

            usersToSave.add(user);
        }

        // --- Lưu tất cả trong cùng Transaction ---
        List<User> savedUsers = userRepository.saveAll(usersToSave);

        // --- Xóa mật khẩu khi trả về ---
        savedUsers.forEach(u -> u.setPassword(null));

        return savedUsers;
    }


    public Page<User> getActiveStudentsByName(String searchKey, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return userRepository.findByIsActiveTrueAndRoleAndNameContainingIgnoreCase(4, searchKey, pageable);
    }

    public Page<User> getActiveCoachInstructorByName(String searchKey, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        // Lọc role = 2 hoặc 3
        return userRepository.findByIsActiveTrueAndRoleInAndNameContainingIgnoreCase(
                Arrays.asList(2, 3), searchKey, pageable);
    }

    @Transactional
    public void bulkUpdateUsers(List<UserUpdateDTO> userList) {
        for (UserUpdateDTO dto : userList) {
            User user = userRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng có ID = " + dto.getId()));

            if (StringUtils.hasText(dto.getName())) {
                user.setName(dto.getName());
            }
            if (StringUtils.hasText(dto.getPhoneNumber())) {
                user.setPhoneNumber(dto.getPhoneNumber());
            }
            if (dto.getDateOfBirth() != null) {
                user.setDateOfBirth(dto.getDateOfBirth());
            }
            if (StringUtils.hasText(dto.getEmail())) {
                user.setEmail(dto.getEmail());
            }
            if (StringUtils.hasText(dto.getAvatar())) {
                user.setAvatar(dto.getAvatar());
            }
            if (dto.getRole() != null) {
                user.setRole(dto.getRole());
            }
            if (dto.getIsActive() != null) {
                user.setIsActive(dto.getIsActive());
            }
            if (StringUtils.hasText(dto.getBeltLevel())) {
                user.setBeltLevel(dto.getBeltLevel());
            }

            if (StringUtils.hasText(dto.getPassword())) {
                user.setPassword(dto.getPassword());
            }

            // Nếu có facilityId mới
            if (dto.getFacilityId() != null) {
                Facility facility = facilityRepository.findById(dto.getFacilityId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy cơ sở có ID = " + dto.getFacilityId()));
                user.setFacility(facility);
            }

            userRepository.save(user);
        }
    }

}
