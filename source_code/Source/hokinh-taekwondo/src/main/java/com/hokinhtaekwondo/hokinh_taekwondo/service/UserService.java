package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClassUser.FacilityClassUserBulkCreateDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClassUser.UserInClassDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.*;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.imports.UserImportResult;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.imports.UserImportRowResult;
import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClass;
import com.hokinhtaekwondo.hokinh_taekwondo.model.FacilityClassUser;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityClassRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityClassUserRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.UserRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.utils.exception.DuplicateUsersException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final int COL_MA_VO_SINH = 0;
    private static final int COL_HO_TEN = 1;
    private static final int COL_NGAY_SINH = 2;
    private static final int COL_CAP_DAI = 3;
    private static final int COL_DIA_CHI = 4;
    private static final int COL_SDT = 5;


    private final UserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final FacilityClassUserService facilityClassUserService;
    private final FacilityClassUserRepository facilityClassUserRepository;
    private final FacilityClassRepository facilityClassRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User loadUserByUsername(String userId) throws UsernameNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException(userId));
    }

    // --- Create ---
    public UserInClassResponseDTO create(UserCreateDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // TODO: mã hóa password sau
        user.setRole(dto.getRole());
        user.setBeltLevel(dto.getBeltLevel());
        user.setLoginPin(0);

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
            user.setPassword(passwordEncoder.encode(dto.getPassword())); // TODO: mã hoá
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

    public List<UserOption> getAllManagersAsOptions() {
        List<User> allManagers = userRepository.findAllByRole(1);
        List<UserOption> managerOptions = new ArrayList<>();
        for (User user : allManagers) {
            UserOption userOption = new UserOption();
            userOption.setUserId(user.getId());
            userOption.setUserName(user.getName());
            managerOptions.add(userOption);
        }
        return managerOptions;
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
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setLoginPin(0);
            System.out.println(dto.getPassword());

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
        savedUsers.forEach(u -> u.setPassword(""));

        return savedUsers;
    }

    @Transactional
    public List<User> createMembersForClass(List<UserCreateForClassDTO> userList) {
        if (userList == null || userList.isEmpty()) {
            throw new IllegalArgumentException("Danh sách người dùng không được trống.");
        }
        List<String> userIds = new ArrayList<>(userList.size());
        for (UserCreateForClassDTO user : userList) {
            userIds.add(user.getId());
        }

        List<String> duplicatedUserIds = userRepository.findExistingIds(userIds);
        if(!duplicatedUserIds.isEmpty()) {
            throw new DuplicateUsersException(duplicatedUserIds);
        }

        List<User> usersToSave = new ArrayList<>();
        List<UserInClassDTO> usersInClass = new ArrayList<>();

        for (UserCreateForClassDTO dto : userList) {

            User user = new User();
            System.out.println(dto.getId());
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
            user.setLoginPin(0);
            user.setPassword(passwordEncoder.encode("12345678"));
            System.out.println(user.getPassword());


            // --- Liên kết cơ sở (Facility) ---
            if (dto.getFacilityId() != null) {
                Facility facility = facilityRepository.findById(dto.getFacilityId())
                        .orElseThrow(() ->
                                new IllegalArgumentException("Không tìm thấy cơ sở có ID = " + dto.getFacilityId()));
                user.setFacility(facility);
            } else {
                throw new IllegalArgumentException("Mỗi người dùng phải có FacilityId hợp lệ.");
            }

            UserInClassDTO userInClass = new UserInClassDTO();
            userInClass.setUserId(dto.getId());
            userInClass.setRoleInClass(dto.getRoleInClass());
            userInClass.setIsActiveInClass(dto.getIsActiveInClass());

            usersInClass.add(userInClass);
            usersToSave.add(user);
        }

        FacilityClassUserBulkCreateDTO facilityClassUserBulkCreateDTO = new FacilityClassUserBulkCreateDTO();
        facilityClassUserBulkCreateDTO.setFacilityClassId(userList.getFirst().getClassId());
        facilityClassUserBulkCreateDTO.setUsers(usersInClass);

        List<User> savedUsers = new ArrayList<>();
        // --- Lưu tất cả trong cùng Transaction ---
        try {
            savedUsers = userRepository.saveAll(usersToSave);
        }
        catch (Exception e) {
            System.out.println("Create: " + e.getMessage());
        }
        // --- Đưa người dùng mới tạo vào lớp học ---
        try {
            facilityClassUserService.bulkCreate(facilityClassUserBulkCreateDTO);
        }
        catch (Exception e) {
            System.out.println("In Class: " + e.getMessage());
        }

        return savedUsers;
    }


    public Page<UserWithFacilityClass> getActiveStudentsByName(String searchKey, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return userRepository.findByIsActiveTrueAndRoleAndNameContainingIgnoreCase(4, searchKey, pageable)
                .map(this::toUserWithFacilityClass);
    }

    public Page<UserWithFacilityClass> getActiveCoachInstructorByName(String searchKey, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        // Lọc role = 2 hoặc 3
        return userRepository.findByIsActiveTrueAndRoleInAndNameContainingIgnoreCase(
                Arrays.asList(2, 3), searchKey, pageable).map(this::toUserWithFacilityClass);
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
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
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

    public UserImportResult importUsers(MultipartFile file, String type, Integer classId) {
        Integer roleType = 4;
        if(type.equals("instructor")) {
            roleType = 3;
        }
        else if(type.equals("coach")) {
            roleType = 2;
        }
        FacilityClass facilityClass = facilityClassRepository.findById(classId).orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với id là " + classId));
        List<UserImportRowResult> results = new ArrayList<>();

        try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = wb.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                String userId = get(row, COL_MA_VO_SINH);
                String fullName = get(row, COL_HO_TEN);
                LocalDate dateOfBirth = getDate(row, COL_NGAY_SINH);
                String beltLevel = get(row, COL_CAP_DAI);
                String address = get(row, COL_DIA_CHI);
                String phoneNumber = get(row, COL_SDT);

                try {
                    validate(userId, fullName, dateOfBirth, beltLevel, phoneNumber);

                    if(userRepository.existsById(userId)) {
                        throw new IllegalArgumentException("Mã người dùng đã tồn tại trên hệ");
                    }
                    // create new user
                    User user = new User();
                    user.setId(userId);
                    user.setName(fullName);
                    user.setPhoneNumber(phoneNumber);
                    user.setDateOfBirth(dateOfBirth);
                    user.setBeltLevel(beltLevel);
                    user.setAddress(address);
                    user.setRole(roleType);
                    userRepository.save(user);
                    // add to class
                    FacilityClassUser facilityClassUser = new FacilityClassUser();
                    facilityClassUser.setUserId(userId);
                    facilityClassUser.setFacilityClass(facilityClass);
                    facilityClassUser.setRoleInClass(type);
                    facilityClassUser.setIsActive(true);
                    facilityClassUser.setCreatedAt(LocalDateTime.now());
                    facilityClassUserRepository.save(facilityClassUser);
                    // add to result
                    results.add(new UserImportRowResult(
                            i + 1, userId, fullName, dateOfBirth, beltLevel, address, phoneNumber, null
                    ));

                } catch (Exception ex) {
                    results.add(new UserImportRowResult(
                            i + 1, userId, fullName, dateOfBirth, beltLevel, address, phoneNumber, ex.getMessage()
                    ));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Không đọc được file excel. Lỗi: ", e);
        }

        return new UserImportResult(results);
    }

    private LocalDate getDate(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }

        if (cell.getCellType() == CellType.STRING) {
            String value = cell.getStringCellValue().trim();
            if (value.isEmpty()) return null;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(value, formatter);
        }

        throw new IllegalArgumentException("Ngày sinh không hợp lệ");
    }


    private void validate(
            String userId,
            String fullName,
            LocalDate dateOfBirth,
            String beltLevel,
            String phoneNumber
    ) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("Mã võ sinh không được để trống");
        }
        if (fullName == null || fullName.isEmpty()) {
            throw new IllegalArgumentException("Họ và tên không được để trống");
        }
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Ngày sinh không hợp lệ");
        }
        if (beltLevel == null || beltLevel.isEmpty()) {
            throw new IllegalArgumentException("Cấp đai không được để trống");
        }
        if (phoneNumber != null && !phoneNumber.matches("^0\\d{9,10}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        }
    }


    private String get(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString();
                }
                yield BigDecimal.valueOf(cell.getNumericCellValue())
                        .stripTrailingZeros()
                        .toPlainString();
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK -> "";
            default -> "";
        };
    }


    private UserWithFacilityClass toUserWithFacilityClass(User user) {
        UserWithFacilityClass userWithFacilityClass = new UserWithFacilityClass();
        userWithFacilityClass.setId(user.getId());
        userWithFacilityClass.setRole(user.getRole());
        userWithFacilityClass.setName(user.getName());
        userWithFacilityClass.setPhoneNumber(user.getPhoneNumber());
        userWithFacilityClass.setDateOfBirth(user.getDateOfBirth());
        userWithFacilityClass.setEmail(user.getEmail());
        userWithFacilityClass.setAvatar(user.getAvatar());
        userWithFacilityClass.setFacilityId(user.getFacility().getId());
        userWithFacilityClass.setBeltLevel(user.getBeltLevel());
        userWithFacilityClass.setPassword(null);
        userWithFacilityClass.setIsActive(user.getIsActive());
        userWithFacilityClass.setCreatedAt(user.getCreatedAt());
        userWithFacilityClass.setClassId(facilityClassUserService.getCurrentClassIdOfUser(user));

        return userWithFacilityClass;
    }
}
