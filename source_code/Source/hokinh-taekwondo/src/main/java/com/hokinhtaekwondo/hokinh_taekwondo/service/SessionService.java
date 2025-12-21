package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.facilityClassUser.ClassOfStudent;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.session.*;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics.*;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics.instructor.FacilityClassInfo;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics.instructor.FacilityInfo;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics.instructor.SessionAttendanceDTOForInstructor;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.statistics.instructor.SessionStatisticsForInstructor;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.FullSessionUserDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.user.SessionUserDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.model.*;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.*;
import com.hokinhtaekwondo.hokinh_taekwondo.utils.exception.ConflictException;
import com.hokinhtaekwondo.hokinh_taekwondo.utils.time.VietNamTime;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final FacilityClassRepository facilityClassRepository;
    private final SessionUserRepository sessionUserRepository;
    private final UserRepository userRepository;
    private final FacilityClassUserRepository facilityClassUserRepository;
    private final SessionUserService sessionUserService;

    // ================= BULK CREATE ==================
    @Transactional
    public void bulkCreateSessions(List<SessionCreateDTO> sessionList) {
        for (SessionCreateDTO dto : sessionList) {
            FacilityClass facilityClass = facilityClassRepository.findById(dto.getFacilityClassId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp có ID: " + dto.getFacilityClassId()));

            Session newSession = new Session();
            newSession.setFacilityClass(facilityClass);
            newSession.setDate(dto.getDate());
            newSession.setStartTime(dto.getStartTime());
            newSession.setEndTime(dto.getEndTime());
            newSession.setTopic(dto.getTopic());
            newSession.setVideoLink(dto.getVideoLink());
            newSession.setReport(dto.getReport());
            newSession.setStatus(dto.getStatus());

            sessionRepository.save(newSession);
        }
    }

    @Transactional
    public int bulkCreateSessionsAndUsers(Integer facilityClassId,
                                          LocalDate startDate,
                                          LocalDate endDate,
                                          List<SessionAndSessionUserBulkCreateDTO> dtoList) {

        FacilityClass facilityClass = facilityClassRepository.findById(facilityClassId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp học."));

        int createdCount = 0;

        // Find all old sessions
        List<Integer> oldSessions = sessionRepository.findIdsByFacilityClassIdAndDateBetween(facilityClass.getId(), startDate, endDate);
        // Delete users in old session and old sessions
        sessionUserRepository.deleteAllBySessionIdIn(oldSessions);
        sessionRepository.deleteAllByIdIn(oldSessions);
        for (SessionAndSessionUserBulkCreateDTO dto : dtoList) {

            LocalDate current = startDate;

            // 1 ─ Move forward until current's dayOfWeek matches dto.dayOfWeek

            while (current.getDayOfWeek().getValue() != dto.getDayOfWeek()) {
                current = current.plusDays(1);
                if (current.isAfter(endDate)) break;
            }

            // 2 ─ From now on, jump weekly (+7 days)
            while (!current.isAfter(endDate)) {
                Session s = new Session();
                s.setFacilityClass(facilityClass);
                s.setDate(current);
                s.setStartTime(dto.getStartTime());
                s.setEndTime(dto.getEndTime());
                sessionRepository.save(s);

                for (SessionUserDTO u : dto.getUsers()) {
                    SessionUser su = new SessionUser();
                    su.setSessionId(s.getId());
                    su.setUserId(u.getId());
                    su.setRoleInSession(u.getRoleInSession());
                    sessionUserRepository.save(su);
                }
                createdCount++;
                current = current.plusWeeks(1); // +7 days
            }
        }

        facilityClass.setSessionsUpdatedAt(VietNamTime.nowDateTime());
        facilityClass.setLatestSession(sessionRepository.findTopByFacilityClassOrderByDateDesc(facilityClass).getDate());
        facilityClassRepository.save(facilityClass);

        return createdCount;
    }


    // ================= BULK UPDATE ==================
    @Transactional
    public void bulkUpdateSessions(SessionBulkUpdateDTO sessionBulkUpdateDTO) {
        if (sessionBulkUpdateDTO.getFacilityClassId() == null || facilityClassRepository.findById(sessionBulkUpdateDTO.getFacilityClassId()).isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy lớp học");
        }
        for (SessionUpdateDTO dto : sessionBulkUpdateDTO.getSessions()) {
            Session existingSession = sessionRepository.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy buổi học có ID: " + dto.getId()));

            // Chỉ cập nhật các trường cho phép
            if (dto.getDate() != null) existingSession.setDate(dto.getDate());
            if (dto.getStartTime() != null) existingSession.setStartTime(dto.getStartTime());
            if (dto.getEndTime() != null) existingSession.setEndTime(dto.getEndTime());
            if (dto.getTopic() != null) existingSession.setTopic(dto.getTopic());
            if (dto.getVideoLink() != null) existingSession.setVideoLink(dto.getVideoLink());
            if (dto.getReport() != null) existingSession.setReport(dto.getReport());
            if (dto.getStatus() != null) existingSession.setStatus(dto.getStatus());

            sessionRepository.save(existingSession);
        }
    }

    public List<Session> getSessionsByFacilityClassAndDateRange(Integer facilityClassId, LocalDate startDate, LocalDate endDate) {
        FacilityClass facilityClass = facilityClassRepository.findById(facilityClassId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp học có ID: " + facilityClassId));

        return sessionRepository.findByFacilityClass_IdAndDateBetween(facilityClass.getId(), startDate, endDate);
    }

    // ------------------ 1. Lấy danh sách session ----------------------
    public List<SessionAndUserResponseDTO> getSessions(LocalDate start, LocalDate end, Integer classId) {

        List<Session> sessions = sessionRepository.findSessionsInRange(start, end, classId);

        return sessions.stream().map(s -> {
            SessionAndUserResponseDTO dto = new SessionAndUserResponseDTO();
            dto.setId(s.getId());
            dto.setDate(s.getDate().toString());
            dto.setStartTime(s.getStartTime().toString());
            dto.setEndTime(s.getEndTime().toString());
            dto.setStatus(s.getStatus());
            dto.setTopic(s.getTopic());
            dto.setVideoLink(s.getVideoLink());
            dto.setReport(s.getReport());

            // fetch instructors only
            List<SessionUser> list = sessionUserRepository.findBySessionId(s.getId());
            dto.setMainInstructors(list.stream()
                    .filter(u -> !u.getRoleInSession().equals("student"))
                    .map(this::toDTO)
                    .toList());

            dto.setStudents(null);
            return dto;
        }).toList();
    }

    // ------------------ 2. Lấy tất cả students trong session ----------
    public List<FullSessionUserDTO> getStudentsOfSession(Integer sessionId) {

        List<SessionUser> list = sessionUserRepository.findBySessionId(sessionId);

        return list.stream()
                .filter(u -> u.getRoleInSession().equals("student"))
                .map(u -> {
                    FullSessionUserDTO dto = toDTO(u);

                    Integer classId = facilityClassUserRepository.findActiveClassForUser(u.getUserId());
                    dto.setClassId(classId);

                    return dto;
                }).toList();
    }

    @Transactional
    public SessionAndUserResponseDTO createSession(SessionAndUserCreateDTO req) {
        Session session = new Session();
        session.setDate(LocalDate.parse(req.getDate()));
        session.setStartTime(LocalTime.parse(req.getStartTime()));
        session.setEndTime(LocalTime.parse(req.getEndTime()));
        session.setStatus(req.getStatus());
        session.setTopic(req.getTopic());
        session.setVideoLink(req.getVideoLink());
        session.setReport(req.getReport());
        FacilityClass facilityClass = facilityClassRepository.findById(req.getClassId())
                .orElseThrow(
                        () -> new RuntimeException("Không tìm thấy lớp học với ID là "  + req.getClassId())
                );
        session.setFacilityClass(facilityClass);
        // Get session
        SessionAndUserResponseDTO responseDTO = new SessionAndUserResponseDTO();
        Session savedSession = sessionRepository.save(session);
        responseDTO.setId(savedSession.getId());
        responseDTO.setDate(savedSession.getDate().toString());
        responseDTO.setStartTime(savedSession.getStartTime().toString());
        responseDTO.setEndTime(savedSession.getEndTime().toString());
        responseDTO.setStatus(savedSession.getStatus());
        responseDTO.setTopic(savedSession.getTopic());
        responseDTO.setVideoLink(savedSession.getVideoLink());
        responseDTO.setReport(savedSession.getReport());

        // Update users inside session
        if (req.getMainInstructors() != null) {
            responseDTO.setMainInstructors(createSessionUsers(req.getMainInstructors(), responseDTO.getId()));
        }
        if (req.getStudents() != null) {
            responseDTO.setStudents(createSessionUsers(req.getStudents(),  responseDTO.getId()));
        }
        return responseDTO;
    }

    private List<FullSessionUserDTO> createSessionUsers(List<FullSessionUserDTO> users, Integer sessionId) {
        List<FullSessionUserDTO> list = new ArrayList<>();

        for (FullSessionUserDTO dto : users) {
            SessionUser su = new SessionUser();
            su.setSessionId(sessionId);
            su.setUserId(dto.getUserId());

            // Common updates
            list.add(saveInfoUserSession(dto, su));
        }

        return list;
    }

    private FullSessionUserDTO saveInfoUserSession( FullSessionUserDTO dto, SessionUser su) {
        if (dto.getAttended() != null) {
            if (dto.getRoleInSession() != null ) {
                if ("off".equals(dto.getRoleInSession()) && dto.getAttended()) {
                    throw new ConflictException("Người có trạng thái nghỉ dạy không được phép có mặt trong lớp");
                }
                su.setRoleInSession(dto.getRoleInSession());
            }
            su.setAttended(dto.getAttended());
        }
        if (dto.getReview() != null) su.setReview(dto.getReview());
        if (dto.getCheckinTime() != null ) {
            if(!dto.getCheckinTime().isBlank()) {
                su.setCheckinTime(LocalDateTime.parse(dto.getCheckinTime()));
            }
            else {
                su.setCheckinTime(null);
            }
        }

        return toDTO(sessionUserRepository.save(su));
    }

    // ------------------ 3. Cập nhật session ----------------------------
    @Transactional
    public SessionAndUserResponseDTO updateSession(SessionAndUserUpdateDTO req, Integer sessionId) {

        SessionAndUserResponseDTO responseDTO = new SessionAndUserResponseDTO();
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        responseDTO.setId(session.getId());
        // Chỉ đổi những field != null
        if (req.getDate() != null) {
            session.setDate(LocalDate.parse(req.getDate()));
            responseDTO.setDate(req.getDate());
        }
        if (req.getStartTime() != null) {
            session.setStartTime(LocalTime.parse(req.getStartTime()));
            responseDTO.setStartTime(req.getStartTime());
        }
        if (req.getEndTime() != null) {
            session.setEndTime(LocalTime.parse(req.getEndTime()));
            responseDTO.setEndTime(req.getEndTime());
        }
        if (req.getStatus() != null) {
            session.setStatus(req.getStatus());
            responseDTO.setStatus(req.getStatus());
        }
        if (req.getTopic() != null) {
            session.setTopic(req.getTopic());
            responseDTO.setTopic(req.getTopic());
        }
        if (req.getVideoLink() != null) {
            session.setVideoLink(req.getVideoLink());
            responseDTO.setVideoLink(req.getVideoLink());
        }
        if (req.getReport() != null) {
            session.setReport(req.getReport());
            responseDTO.setReport(req.getReport());
        }
        sessionUserRepository.deleteAllById(req.getSessionUserIds());

        // Update users inside session
        if (req.getMainInstructors() != null) {
            responseDTO.setMainInstructors(updateSessionUsers(req.getMainInstructors(), sessionId));
            System.out.println(req.getMainInstructors().getFirst().getUserId() +  " " + req.getMainInstructors().getFirst().getName() + req.getMainInstructors().getFirst().getRoleInSession());
        }
        if (req.getStudents() != null) {
            responseDTO.setStudents(updateSessionUsers(req.getStudents(),  sessionId));
        }
        return responseDTO;
    }

    private List<FullSessionUserDTO> updateSessionUsers(List<FullSessionUserDTO> users, Integer sessionId) {
        List<FullSessionUserDTO> list = new ArrayList<>();

        for (FullSessionUserDTO dto : users) {
            SessionUser su;

            // Update
            if (dto.getId() != null) {
                su = sessionUserRepository.findById(dto.getId())
                        .orElseThrow(() -> new RuntimeException("User " + dto.getId() + " not found"));
            }
            // Create
            else {
                su = new SessionUser();
                su.setSessionId(sessionId);
                su.setUserId(dto.getUserId());
            }

            // Common updates
            list.add(saveInfoUserSession(dto, su));
        }

        return list;
    }

    @Transactional
    public Session deleteSession(Integer sessionId) {
        Session deletedSession = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy buổi học có id " + sessionId));
        sessionUserRepository.deleteAllBySessionId(sessionId);
        sessionRepository.deleteById(sessionId);
        return deletedSession;
    }

    public List<InstructorSessionDTO> getInstructorSessions(LocalDate start, LocalDate end, String instructorId) {
        List<InstructorSessionInfo> instructorSessions = sessionRepository.findAllSessionsForInstructorByUserIdAndDateRange(instructorId, start, end);
        List<InstructorSessionDTO> result = new ArrayList<>();
        HashMap<Integer, Facility> facilities = new HashMap<>();
        for (InstructorSessionInfo session : instructorSessions) {
            Facility facility = session.session().getFacilityClass().getFacility();
            facilities.putIfAbsent(facility.getId(), facility);
            result.add(toInstructorSessionDTO(session.session(),
                                    session.sessionUser(),
                                    session.session().getFacilityClass(),
                                    facilities.get(facility.getId())
            ));
        }
        return result;
    }

    @Transactional
    public void reportSession(InstructorSessionUpdateDTO session, String instructorId) throws Exception {
        // 1. Check existence of instructor in session
        SessionUser currentRole = sessionUserRepository
                .findBySessionIdAndUserId(session.getId(), instructorId);
        if(currentRole == null) {
            throw new Exception("Bạn không thuộc buổi học này!");
        }

        // 2. Check the role
        if (!currentRole.getRoleInSession().equals("leader") &&
                !currentRole.getRoleInSession().equals("assistant")) {
            throw new Exception("Bạn không có quyền điểm danh!");
        }
        if(!currentRole.getAttended()) {
            throw new Exception("Bạn cần check in thành công để có thể báo cáo thông tin buổi học!");
        }

        Session updatedSession = sessionRepository.findById(session.getId())
                .orElseThrow(
                        () -> new RuntimeException("Không tìm thấy buổi học có id " +  session.getId())
                );
        // 3. Update session report
        updatedSession.setReport(session.getReport());
        updatedSession.setTopic(session.getTopic());
        updatedSession.setVideoLink(session.getVideoLink());
        // 4. Update status of students in session
        sessionUserService.reportStudents(session.getStudents(), session.getId());
    }

    public SessionStatistics getInstructorSessionAttendancesStatistics(Integer facilityId, LocalDate start, LocalDate end) {

        if(end.isBefore(VietNamTime.nowDate())) {
            throw new RuntimeException("Thời điểm thống kê không phù hợp");
        }

        if(Math.abs(ChronoUnit.DAYS.between(start, end)) > 365) {
            throw new RuntimeException("Khoảng thời gian phải dưới 365 ngày");
        }

        List<InstructorSessionStatistics> instructorSessionStatistics = new ArrayList<>();
        List<StudentAttendanceStatistics> studentAttendanceList = new ArrayList<>();
        HashMap<String, Integer> mapInstructorId = new HashMap<>();
        HashMap<String, Integer> mapStudentId = new HashMap<>();
        List<SessionAttendanceDTO> sessionData = sessionRepository.findAllSessionsForInstructorsByFacilityId(facilityId,  start, end);
        for(SessionAttendanceDTO dto : sessionData) {
            String userId = dto.getUserId();
            if(dto.getRoleInSession().equals("student")) {
                if(!mapStudentId.containsKey(userId)) {
                    mapStudentId.put(userId, studentAttendanceList.size());
                    StudentAttendanceStatistics sessionStatistics = new StudentAttendanceStatistics();
                    sessionStatistics.setUserId(userId);
                    sessionStatistics.setStudentName(dto.getUserName());
                    studentAttendanceList.add(sessionStatistics);
                }
                if(dto.getAttended()) {
                    Integer currentNumAttended = studentAttendanceList.get(mapStudentId.get(dto.getUserId())).getNumAttendedSession();
                    studentAttendanceList.get(mapStudentId.get(dto.getUserId())).setNumAttendedSession(currentNumAttended + 1);
                }
                else {
                    Integer currentNumAbsent = studentAttendanceList.get(mapStudentId.get(dto.getUserId())).getNumAbsentSession();
                    studentAttendanceList.get(mapStudentId.get(dto.getUserId())).setNumAbsentSession(currentNumAbsent + 1);
                }
            }
            else {
                if(!mapInstructorId.containsKey(userId)) {
                    mapInstructorId.put(userId, instructorSessionStatistics.size());
                    InstructorSessionStatistics sessionStatistics = new InstructorSessionStatistics();
                    sessionStatistics.setUserId(userId);
                    sessionStatistics.setUserName(dto.getUserName());
                    instructorSessionStatistics.add(sessionStatistics);
                }
                if(dto.getRoleInSession().equals("off") || !dto.getAttended()) {
                    AbsentSession absentSession = new AbsentSession();
                    absentSession.setRoleInSession(dto.getRoleInSession());
                    absentSession.setDate(dto.getDate());
                    absentSession.setClassId(dto.getFacilityClassId());
                    absentSession.setFacilityId(dto.getFacilityId());
                    instructorSessionStatistics.get(mapInstructorId.get(userId)).getAbsentSessions().add(absentSession);
                }
                else {
                    PresentSession presentSession = new PresentSession();
                    presentSession.setClassId(dto.getFacilityClassId());
                    presentSession.setDate(dto.getDate());
                    long lateMinutes = dto.getCheckinDelay() == null
                            ? 0
                            : dto.getCheckinDelay().toMinutes();
                    presentSession.setLateMinutes(lateMinutes);
                    presentSession.setRoleInSession(dto.getRoleInSession());
                    presentSession.setFacilityId(dto.getFacilityId());
                    instructorSessionStatistics.get(mapInstructorId.get(userId)).getPresentSessions().add(presentSession);
                }
            }
        }
        List<String> classIdsOfStudents = mapStudentId.keySet().stream().toList();
        List<ClassOfStudent>  classOfStudents = facilityClassUserRepository.findClassesOfStudents(classIdsOfStudents);
        for(ClassOfStudent classOfStudent : classOfStudents) {
            Integer index = mapStudentId.get(classOfStudent.getStudentId());
            studentAttendanceList.get(index).setClassId(classOfStudent.getClassId());
            studentAttendanceList.get(index).setIsActive(classOfStudent.getIsActive());
        }
        SessionStatistics result = new SessionStatistics();
        result.setInstructorSessionStatistics(instructorSessionStatistics);
        result.setStudentAttendanceList(studentAttendanceList);
        return result;
    }

    public SessionStatisticsForInstructor getSessionStatisticsForInstructor(String userId, LocalDate start, LocalDate end) {
        List<SessionAttendanceDTOForInstructor> sessionData = sessionRepository.findAllSessionsForInstructorsByUserId(userId, start, end);
        SessionStatisticsForInstructor result = new SessionStatisticsForInstructor();
        HashMap<Integer, FacilityInfo> facilityMap = new HashMap<>();
        HashMap<Integer, FacilityClassInfo> classMap = new HashMap<>();

        for(SessionAttendanceDTOForInstructor dto : sessionData) {
            facilityMap.putIfAbsent(dto.getFacilityId(), new FacilityInfo(dto.getFacilityName()));
            classMap.putIfAbsent(dto.getFacilityClassId(), new FacilityClassInfo(dto.getFacilityClassName(), dto.getFacilityId()));
            if(dto.getRoleInSession().equals("off") || !dto.getAttended()) {
                AbsentSession absentSession = new AbsentSession();
                absentSession.setDate(dto.getDate());
                absentSession.setClassId(dto.getFacilityClassId());
                absentSession.setFacilityId(dto.getFacilityId());
                absentSession.setRoleInSession(dto.getRoleInSession());
                result.getAbsentSessions().add(absentSession);
            }
            else {
                PresentSession presentSession = new PresentSession();
                presentSession.setDate(dto.getDate());
                presentSession.setClassId(dto.getFacilityClassId());
                presentSession.setFacilityId(dto.getFacilityId());
                presentSession.setRoleInSession(dto.getRoleInSession());
                presentSession.setLateMinutes(dto.getCheckinDelay().toMinutes());
                result.getPresentSessions().add(presentSession);
            }
        }
        result.setFacilityMap(facilityMap);
        result.setClassMap(classMap);
        return result;
    }

    public List<StudentScheduleInfo> getStudentSchedule(String userId, LocalDate start, LocalDate end) {
        return sessionRepository.findAllSessionsForStudentByUserIdAndDateRange(
                userId,
                start,
                end);
    }

    private InstructorSessionDTO toInstructorSessionDTO(Session session, SessionUser su, FacilityClass facilityClass, Facility facility) {
        InstructorSessionDTO dto = new InstructorSessionDTO();
        // Attendance status of instructor
        dto.setAttended(su.getAttended());
        dto.setRole(su.getRoleInSession());
        dto.setCheckinTime(su.getCheckinTime());

        // Info of session
        dto.setId(session.getId());
        dto.setDate(session.getDate());
        dto.setStatus(session.getStatus());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setReport(session.getReport());
        dto.setTopic(session.getTopic());
        dto.setVideoLink(session.getVideoLink());

        // Class name
        dto.setClassName(facilityClass.getName());

        // Facility info
        dto.setFacilityName(facility.getName());
        dto.setFacilityAddress(facility.getAddress());
        dto.setFacilityMapsLink(facility.getMapsLink());
        return dto;
    }

    private FullSessionUserDTO toDTO(SessionUser su) {
        FullSessionUserDTO dto = new FullSessionUserDTO();

        User user = userRepository.findById(su.getUserId()).orElse(null);

        dto.setId(su.getId());
        dto.setUserId(su.getUserId());
        dto.setName(user != null ? user.getName() : "");
        dto.setRoleInSession(su.getRoleInSession());
        dto.setReview(su.getReview());
        dto.setAttended(su.getAttended());
        dto.setCheckinTime(
                su.getCheckinTime() != null ? su.getCheckinTime().toString() : null
        );
        return dto;
    }

}
