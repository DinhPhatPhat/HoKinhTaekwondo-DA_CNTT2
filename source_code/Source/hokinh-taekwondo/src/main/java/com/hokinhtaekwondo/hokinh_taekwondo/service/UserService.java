package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.UserRequestDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.UserResponseDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public UserResponseDTO create(UserRequestDTO dto) {
        User user = new User();
        user.setId(dto.getId()); // id nhập thủ công
        user.setName(dto.getName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // TODO: mã hóa password sau
        user.setAvatar(dto.getAvatar());
        try {
            user.setRole(User.Role.valueOf(dto.getRole()));
            user.setBeltLevel(User.BeltLevel.valueOf(dto.getBeltLevel()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Role hoặc BeltLevel không hợp lệ");
        }
        user.setFacility(facilityRepository.findById(dto.getFacilityId()).orElse(null));
        User saved = userRepository.save(user);
        return toResponseDTO(saved);
    }

    // --- Update ---
    public void update(UserRequestDTO dto, MultipartFile image) throws IOException {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + dto.getId()));

        user.setName(dto.getName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(dto.getPassword()); // TODO: mã hoá
        }
        user.setAvatar(dto.getAvatar());
        user.setRole(User.Role.valueOf(dto.getRole()));
        user.setBeltLevel(User.BeltLevel.valueOf(dto.getBeltLevel()));
        user.setFacility(facilityRepository.findById(dto.getFacilityId()).orElseThrow(EntityNotFoundException::new));
        if (image != null && !image.isEmpty()) {
            String imagePath = upLoadImage(image, dto.getId());
            user.setAvatar(imagePath);
        }
        User updated = userRepository.save(user);
        toResponseDTO(updated);
    }

    // --- Delete ---
    public void delete(String id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // --- Get by Id ---
    public UserResponseDTO getById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return toResponseDTO(user);
    }

    // --- Get all ---
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }

    // Wrong id or password -> false
    // Correct email and password -> true
    public boolean checkLogin(String id, String password){
        Optional<User> user = userRepository.findById(id);
        return user.isPresent() && user.get().getPassword().equals(password);
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
    private UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setRole(String.valueOf(user.getRole()));
        dto.setBeltLevel(String.valueOf(user.getBeltLevel()));
        dto.setFacilityId(user.getFacility().getId());
        return dto;
    }
}
