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
import com.hokinhtaekwondo.hokinh_taekwondo.utils.ValidateRole;
import com.hokinhtaekwondo.hokinh_taekwondo.utils.exception.DuplicateUsersException;
import com.hokinhtaekwondo.hokinh_taekwondo.utils.time.VietNamTime;
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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    public boolean isManagerOfFacility(String currentUserId, int facilityId) {
        Facility facility = facilityRepository.findById(facilityId).orElse(null);
        if (facility == null) {
            return true;
        }
        return facility.getManager().getId().equals(currentUserId);
    }

    public Page<UserManagementDTO> getUserByFacilityId(int facilityId, Boolean isActive, User user, int page, int size) {
        if(size > 40) {
            throw new  IllegalArgumentException("Mỗi trang không được vượt quá 40");
        }
        Facility facility = facilityRepository.findById(facilityId).orElse(null);
        if(!ValidateRole.isResponsibleForFacility(user, facility != null ? facility.getManager() : null)) {
            throw new RuntimeException("Bạn không có quyền xem những người dùng này");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        System.out.println("page: " + page);
        return userRepository.findUsersByFacilityIdForManager(facilityId, isActive, pageable)
                    .map(this::toManagementResponseDTO);
    }

    public Page<UserManagementDTO> getAllUsersByManager(Boolean isActive, User user, int page, int size) {
        if(size > 40) {
            throw new  IllegalArgumentException("Mỗi trang không được vượt quá 40");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        if(user.getRole() == 0) {
            return userRepository.findAllByIsActiveAndRoleGreaterThan(isActive, 1, pageable)
                    .map(this::toManagementResponseDTO);
        }
        else if(user.getRole() == 1) {
            List<Integer> facilities = facilityRepository.findAllByManager_Id(user.getId())
                    .stream().map(Facility::getId).toList();
            return userRepository.findAllUsersInFacilities(facilities, isActive, pageable)
                    .map(this::toManagementResponseDTO);
        }
        return null;
    }

    public List<ManagerManagementDTO> getAllManagers(User user, Boolean isActive) {
        System.out.println(isActive);
        if(user.getRole() != 0) {
            throw new RuntimeException("Bạn không có quyền xem quản lý");
        }
        List<User> lstManagers = userRepository.findAllByRoleAndIsActive(1, isActive);
        List<Facility>  lstFacilities = facilityRepository.findAll();
        HashMap<String, List<String>> responsibleFacilities = new HashMap<>();

        for(Facility facility : lstFacilities) {
            User manager = facility.getManager();
            if(manager != null) {
                if(!responsibleFacilities.containsKey(manager.getId())) {
                    responsibleFacilities.putIfAbsent(manager.getId(), new ArrayList<String>());
                }
                responsibleFacilities.get(manager.getId()).add(facility.getName());
            }
        }

        List<ManagerManagementDTO> result = new ArrayList<>();
        for(User manager : lstManagers) {
            ManagerManagementDTO dto = new ManagerManagementDTO();
            dto.setId(manager.getId());
            dto.setName(manager.getName());
            dto.setAvatar(manager.getAvatar());
            dto.setEmail(manager.getEmail());
            dto.setDateOfBirth(manager.getDateOfBirth());
            dto.setPhoneNumber(manager.getPhoneNumber());
            dto.setFacilityNames(responsibleFacilities.get(manager.getId()));
            result.add(dto);
        }

        return result;
    }

    public Page<UserManagementDTO> searchAllUsersByIdAndName(
            User user, String searchKey, Boolean isActive, int page, int size) {

        if (size > 40) {
            throw new IllegalArgumentException("Mỗi trang không được vượt quá 40");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        // Club head
        if (user.getRole() == 0) {
            return userRepository
                    .findAllUserBySearchKeyForClubHead(searchKey, isActive, pageable)
                    .map(this::toManagementResponseDTO);
        }

        // Manager
        if (user.getRole() == 1) {
            List<Integer> facilities = facilityRepository
                    .findAllByManager_Id(user.getId())
                    .stream()
                    .map(Facility::getId)
                    .toList();
            if (facilities.isEmpty()) {
                return Page.empty(pageable);
            }

            return userRepository
                    .findAllUserBySearchKeyForManager(searchKey, facilities, isActive, pageable)
                    .map(this::toManagementResponseDTO);
        }

        return Page.empty(pageable);
    }


    public Page<UserManagementDTO> searchUsersInFacilityByIdAndName(int facilityId, Boolean isActive, User user, String searchKey, int page, int size) {
        if(size > 40) {
            throw new  IllegalArgumentException("Mỗi trang không được vượt quá 40");
        }
        Facility facility = facilityRepository.findById(facilityId).orElse(null);
        if(!ValidateRole.isResponsibleForFacility(user, facility != null ? facility.getManager() : null)) {
            throw new RuntimeException("Bạn không có quyền xem những người dùng này");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        if(user.getRole() == 0) {
            return userRepository.findAllFacilityUserBySearchKeyForClubHead(searchKey, facilityId, isActive, pageable)
                    .map(this::toManagementResponseDTO);
        }
        else if(user.getRole() == 1) {
            return userRepository.findAllFacilityUserBySearchKeyForManager(searchKey, facilityId, isActive, pageable)
                    .map(this::toManagementResponseDTO);
        }
        return null;
    }

    public Page<UserManagementDTO> searchStudentsNonFacility(Boolean isActive, User user, String searchKey, int page, int size) {
        if(size > 40) {
            throw new IllegalArgumentException("Mỗi trang không được vượt quá 40");
        }
        if(user.getRole() != 0 && user.getRole() != 1) {
            throw new RuntimeException("Không có quyền xem thông tin người dùng");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        return userRepository.searchAllNonFacilityUserWithRole(searchKey, 4, isActive, pageable)
                    .map(this::toManagementResponseDTO);
    }

    public Page<UserManagementDTO> getStudentsNonFacility(Boolean isActive, User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        if(user.getRole() == 0 || user.getRole() == 1) {
            return userRepository.findByFacilityIsNullAndRoleAndIsActive(4, isActive, pageable)
                    .map(this::toManagementResponseDTO);
        }
        throw new RuntimeException("Không có quyền tìm kiếm người dùng");
    }

    public void deleteUserById(String id, User deleteAuthor) {
        if(deleteAuthor.getRole() > 1) {
            throw new RuntimeException("Không có quyền xóa người dùng");
        }
        User deletedUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng " + id));
        User facilityManagerOfDeletedUser = deletedUser.getFacility() != null ? deletedUser.getFacility().getManager() : null;
        if((facilityManagerOfDeletedUser == null || ValidateRole.isResponsibleForFacility(deleteAuthor, facilityManagerOfDeletedUser))
            && deletedUser.getRole() > deleteAuthor.getRole()) {
            if(deletedUser.getRole() == 1) {
                List<Facility> responsibleFacilities = facilityRepository.findAllByManager_Id(id);
                if(!responsibleFacilities.isEmpty()) {
                    StringBuilder facilityNames = new StringBuilder();
                    for(Facility facility : responsibleFacilities) {
                        facilityNames.append(facility.getName()).append(", ");
                    }
                    throw new RuntimeException("Người dùng hiện đang quản lý các cơ sở: " + facilityNames.substring(0, facilityNames.length() - 2) + ". Vui lòng gỡ quyền quản lý cơ sở cho người dùng trước khi xóa");
                }
            }
            userRepository.delete(deletedUser);
        }
    }

    @Transactional
    public ManagerCreateResponse createManager(ManagerCreateDTO manager, User creator) {
        if(creator.getRole() != 0) throw new RuntimeException("Bạn không có quyền thêm quản lý");
        Integer index = userRepository.countByRole(1);
        User createdManager = new User();
        createdManager.setId("quan_ly_" + index);
        createdManager.setRole(1);
        createdManager.setName(manager.getName());
        createdManager.setPassword(passwordEncoder.encode(manager.getPassword()));
        createdManager.setDateOfBirth(manager.getDateOfBirth());
        createdManager.setPhoneNumber(manager.getPhoneNumber());
        createdManager.setEmail(manager.getEmail());
        createdManager.setAvatar(manager.getAvatar());
        return new ManagerCreateResponse(userRepository.save(createdManager));
    }

    @Transactional
    public ManagerCreateResponse updateManager(ManagerManagementDTO manager, User updateAuthor) {
        if(updateAuthor.getRole() != 0) {
            throw new RuntimeException("Không cho phép chỉnh sửa người dùng này");
        }
        User updatedManager = userRepository.findById(manager.getId()).orElseThrow(() -> new RuntimeException("Không tìm thấy quản lý"));
        if(manager.getPhoneNumber().length() == 10 || manager.getPhoneNumber().isEmpty()) {
            updatedManager.setPhoneNumber(manager.getPhoneNumber());
        }
        if(manager.getEmail().contains("@")) {
            updatedManager.setEmail(manager.getEmail());
        }
        if(manager.getPassword().length() >= 8) {
            updatedManager.setPassword(passwordEncoder.encode(manager.getPassword()));
        }
        updatedManager.setDateOfBirth(manager.getDateOfBirth());
        updatedManager.setName(manager.getName());
        updatedManager.setAvatar(manager.getAvatar());
        return new ManagerCreateResponse(userRepository.save(updatedManager));
    }

    @Transactional
    public void updateUserByAdmin(UserManagementDTO updatedUserDTO, User updateAuthor) {
        if(updateAuthor.getRole() > 1) {
            throw new RuntimeException("Không có quyền xóa người dùng");
        }
        User updatedUser = userRepository.findById(updatedUserDTO.getId()).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng " + updatedUserDTO.getId()));
        User facilityManagerOfUpdatedUser = updatedUser.getFacility() != null ? updatedUser.getFacility().getManager() : null;
        if((facilityManagerOfUpdatedUser == null || ValidateRole.isResponsibleForFacility(updateAuthor, facilityManagerOfUpdatedUser))
                && updatedUser.getRole() > updateAuthor.getRole()) {
            if((updatedUser.getRole() == 2 || updatedUser.getRole() == 3)
                    && (updatedUserDTO.getRole() == 2 || updatedUserDTO.getRole() == 3)) {
                facilityRepository.findById(updatedUserDTO.getFacilityId()).ifPresent(updatedUser::setFacility);
                updatedUser.setRole(updatedUserDTO.getRole());
            }
            updatedUser.setName(updatedUserDTO.getName());
            updatedUser.setPassword(passwordEncoder.encode(updatedUserDTO.getPassword()));
            updatedUser.setDateOfBirth(updatedUserDTO.getDateOfBirth());
            updatedUser.setPhoneNumber(updatedUserDTO.getPhoneNumber());
            updatedUser.setEmail(updatedUserDTO.getEmail());
            updatedUser.setAvatar(updatedUserDTO.getAvatar());
            updatedUser.setBeltLevel(updatedUserDTO.getBeltLevel());
        }
    }

    @Transactional
    public void updateActiveStatus(String userId, User author, Boolean active) throws Exception {
        if(author.getRole() > 1) {
            throw new RuntimeException("Không có quyền thực hiện chức năng này");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản người dùng"));
        if(author.getRole() < user.getRole()) {
            user.setIsActive(active);
            if(!active) {
                if(user.getRole() == 1) {
                    List<Facility> responsibleFacilities = facilityRepository.findAllByManager_Id(userId);
                    if(!responsibleFacilities.isEmpty()) {
                        StringBuilder facilityNames = new StringBuilder();
                        for(Facility facility : responsibleFacilities) {
                            facilityNames.append(facility.getName()).append(", ");
                        }
                        System.out.println(facilityNames);
                        throw new RuntimeException("Người dùng hiện đang quản lý các cơ sở: " + facilityNames.substring(0, facilityNames.length() - 2) + ". Vui lòng gỡ quyền quản lý cơ sở cho người dùng trước khi vô hiệu hóa tài khoản");
                    }
                }
                // Log out if user is inactivated
                user.setLoginPin(user.getLoginPin()+1);
            }
        }
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
        List<User> allManagers = userRepository.findAllByRoleAndIsActiveTrue(1);
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
        dto.setFacilityId(user.getFacility() != null ? user.getFacility().getId() : null);
        return dto;
    }

    private UserManagementDTO toManagementResponseDTO(User user) {
        UserManagementDTO dto = new UserManagementDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setIsActive((user.getIsActive()));
        dto.setRole((user.getRole()));
        dto.setBeltLevel(String.valueOf(user.getBeltLevel()));
        dto.setFacilityId(user.getFacility() != null ? user.getFacility().getId() : null);
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
            user.setCreatedAt(Timestamp.valueOf(VietNamTime.nowDateTime()));
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
    public List<User> createMembersForClass(List<UserCreateForClassDTO> userList, User creator, Integer classId) {
        FacilityClass facilityClass = facilityClassRepository.findById(classId).orElse(null);
        if(facilityClass == null) {
            throw new IllegalArgumentException("Lớp không hợp lệ");
        }
        User facilityManager = facilityClass.getFacility().getManager();
        if(!ValidateRole.isResponsibleForFacility(creator, facilityManager)) {
            throw new RuntimeException("Bạn không có quyền tạo người dùng trong lớp");
        }
        if (userList == null || userList.isEmpty()) {
            throw new IllegalArgumentException("Danh sách người dùng không được trống.");
        }
        List<String> userIds = new ArrayList<>(userList.size());
        for (UserCreateForClassDTO user : userList) {
            userIds.add(user.getId());
        }

        List<String> existAdminUser = userRepository.existManagerOrClubHead(userIds);
        if(existAdminUser != null && !existAdminUser.isEmpty()) {
            throw new RuntimeException("Bạn không được tạo những người dùng này");
        }

        List<String> duplicatedUserIds = userRepository.findExistingIds(userIds);
        if(!duplicatedUserIds.isEmpty()) {
            throw new DuplicateUsersException(duplicatedUserIds);
        }

        List<User> usersToSave = new ArrayList<>();
        List<UserInClassDTO> usersInClass = new ArrayList<>();

        for (UserCreateForClassDTO dto : userList) {
            User user = new User();
            user.setId(dto.getId());
            user.setName(dto.getName());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setDateOfBirth(dto.getDateOfBirth());
            user.setEmail(dto.getEmail());
            user.setAvatar(dto.getAvatar());
            user.setCreatedAt(Timestamp.valueOf(VietNamTime.nowDateTime()));
            user.setRole(dto.getRole());
            user.setBeltLevel(dto.getBeltLevel());
            user.setIsActive(true);
            user.setLoginPin(0);
            user.setPassword(passwordEncoder.encode(user.getId() + "12345678@"));


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
            facilityClassUserService.bulkCreate(facilityClassUserBulkCreateDTO, creator);
        }
        catch (Exception e) {
            System.out.println("In Class: " + e.getMessage());
        }

        return savedUsers;
    }


    public Page<UserWithFacilityClass> getActiveStudentsByName(String searchKey, int page, int size, User searcher) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        if(searcher.getRole() == 1) {
            return userRepository.findUserWithRoleForManagerByName(
                            searcher.getId(),4, searchKey, pageable)
                    .map(this::toUserWithFacilityClass);
        }
        if(searcher.getRole() == 0) {
            return userRepository.findByIsActiveTrueAndRoleEqualsAndNameContainingIgnoreCase(4,  searchKey, pageable)
                    .map(this::toUserWithFacilityClass);
        }
        return null;
    }

    public Page<UserWithFacilityClass> getActiveCoachInstructorByName(String searchKey, int page, int size, User searcher) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        // Lọc role = 2 hoặc 3
        if(searcher.getRole() > 1) {
            throw new RuntimeException("Không cho phép tìm kiếm người dùng");
        }
        return userRepository.findByIsActiveTrueAndRoleInAndNameContainingIgnoreCase(
                Arrays.asList(2, 3),
                searchKey,
                pageable).map(this::toUserWithFacilityClass);
    }

    @Transactional
    public void bulkUpdateUsers(List<UserUpdateDTO> userList, Integer classId, User creator) {
        FacilityClass facilityClass = facilityClassRepository.findById(classId).orElseThrow(() -> new RuntimeException("Không tìm thấy cơ sở với id là " + classId));
        User manager = facilityClass.getFacility().getManager();
        if(!ValidateRole.isResponsibleForFacility(creator, manager)) {
            throw new RuntimeException("Bạn không có quyền thay đổi những người dùng này");
        }
        for (UserUpdateDTO dto : userList) {
            User user = userRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng có ID = " + dto.getId()));
            if(user.getRole() < 2
                    || dto.getRole() < 2
                    || dto.getRole() > 4
                    || (user.getRole() == 4 && dto.getRole() != 4)
                    || (user.getRole() != 4 && dto.getRole() == 4)) {
                throw new RuntimeException("Không cho phép thay đổi người dùng");
            }
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

    public UserImportResult importUsers(MultipartFile file, String type, Integer classId, User creator) {
        Integer roleType = 4;
        if(type.equals("instructor")) {
            roleType = 3;
        }
        else if(type.equals("coach")) {
            roleType = 2;
        }
        FacilityClass facilityClass = facilityClassRepository.findById(classId).orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với id là " + classId));
        User facilityManager = facilityClass.getFacility().getManager();
        if(!ValidateRole.isResponsibleForFacility(creator, facilityManager)) {
            throw new RuntimeException("Bạn không có quyền thay đổi người dùng trong lớp");
        }
        List<UserImportRowResult> results = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = wb.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {


                Row row = sheet.getRow(i);
                if (row == null) continue;

                String userId = get(row, COL_MA_VO_SINH);
                System.out.println(i + userId);
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
                    user.setPassword(passwordEncoder.encode(userId+ "12345678@"));
                    userRepository.save(user);
                    // add to class
                    FacilityClassUser facilityClassUser = new FacilityClassUser();
                    facilityClassUser.setUserId(userId);
                    facilityClassUser.setFacilityClass(facilityClass);
                    facilityClassUser.setRoleInClass(type);
                    facilityClassUser.setIsActive(true);
                    facilityClassUser.setCreatedAt(VietNamTime.nowDateTime());
                    facilityClassUserRepository.save(facilityClassUser);
                    // add to result
                    if(dateOfBirth == null) {
                        throw new IllegalArgumentException("Ngày sinh không được để trống");
                    }
                    results.add(new UserImportRowResult(
                            i + 1, userId, fullName, dateOfBirth.format(formatter), beltLevel, address, phoneNumber, null
                    ));

                } catch (Exception ex) {
                    results.add(new UserImportRowResult(
                            i + 1, userId, fullName, dateOfBirth != null ? dateOfBirth.format(formatter) : null, beltLevel, address, phoneNumber, ex.getMessage()
                    ));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Không đọc được file excel. Lỗi: ", e);
        }
        catch (DateTimeException dte) {
            throw new RuntimeException("Không đọc được file excel. Lỗi ngày tháng: ", dte);
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
        userWithFacilityClass.setFacilityId(user.getFacility() != null ? user.getFacility().getId() : null);
        userWithFacilityClass.setBeltLevel(user.getBeltLevel());
        userWithFacilityClass.setPassword(null);
        userWithFacilityClass.setIsActive(user.getIsActive());
        userWithFacilityClass.setCreatedAt(user.getCreatedAt());
        userWithFacilityClass.setClassId(facilityClassUserService.getCurrentClassIdOfUser(user));

        return userWithFacilityClass;
    }
}
