package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByFacility(Facility facility);

    Optional<User> findByEmail(String email);

    @Query("SELECT u.id FROM User u WHERE u.id IN :ids")
    List<String> findExistingIds(@Param("ids") List<String> ids);

    List<User> findAllByRoleAndIsActiveTrue(Integer role);

    @Query("""
    SELECT count(u.id)
    FROM User u
    WHERE u.id LIKE CONCAT(:id, '%')
        """)
    Long countByUserId(String id);

    @Query("""
    SELECT u FROM User u
    WHERE (u.facility IS NULL OR u.facility.manager.id = :managerId) 
    AND u.role = :role 
    AND LOWER(u.name) LIKE LOWER(CONCAT('%', :searchKey, '%'))
""")
    Page<User> findUserWithRoleForManagerByName(@Param("managerId") String managerId,
                                            @Param("role") int role,
                                            @Param("searchKey") String searchKey,
                                            Pageable pageable);

    Page<User> findByIsActiveTrueAndRoleInAndNameContainingIgnoreCase(Collection<Integer> role, String name, Pageable pageable);
    Page<User> findByIsActiveTrueAndRoleEqualsAndNameContainingIgnoreCase(int role, String searchKey,Pageable pageable);
    @Query("SELECT u.id FROM User u WHERE (u.role = 0 OR u.role = 1) AND u.id IN :ids")
    List<String> existManagerOrClubHead(@Param("ids") List<String> ids);
    @Query("""
    SELECT u 
    FROM User u
    JOIN FETCH u.facility fac
    WHERE (fac.id = :facilityId
    AND u.role > 1) AND u.isActive = :isActive
    """)
    Page<User> findUsersByFacilityIdForManager(@Param("facilityId") Integer facilityId,
                                               @Param("isActive") Boolean isActive,
                                               Pageable pageable);

    @Query("""
    SELECT u 
    FROM User u
    JOIN FETCH u.facility fac
    WHERE (fac.id = :facilityId
    AND u.role > 1) AND u.isActive = :isActive
    """)
    Page<User> findAllUsersInFacilities(@Param("facilityIds") List<Integer> facilityIds,
                                        @Param("isActive") Boolean isActive,
                                        Pageable pageable);

    @Query("""
    SELECT u 
    FROM User u
    WHERE u.role > 1 
        AND (
            LOWER(u.name) LIKE LOWER(CONCAT('%', :searchKey, '%'))
            OR LOWER(u.id) LIKE LOWER(CONCAT('%', :searchKey, '%'))
            ) AND u.isActive = :isActive
    """)
    Page<User> findAllUserBySearchKeyForClubHead(@Param("searchKey") String searchKey,
                                                 @Param("isActive") Boolean isActive,
                                                 Pageable pageable);

    @Query("""
    SELECT u 
    FROM User u
    JOIN FETCH u.facility fac       
    WHERE u.role > 1 
        AND (
            LOWER(u.name) LIKE LOWER(CONCAT('%', :searchKey, '%'))
            OR LOWER(u.id) LIKE LOWER(CONCAT('%', :searchKey, '%'))
            ) 
        AND u.isActive = :isActive
        AND fac.id = :facilityId
    """)
    Page<User> findAllFacilityUserBySearchKeyForClubHead(@Param("searchKey") String searchKey,
                                                         @Param("facilityId") Integer facilityId,
                                                         @Param("isActive") Boolean isActive,
                                                         Pageable pageable);

    @Query("""
    SELECT u 
    FROM User u
    JOIN FETCH u.facility fac   
    WHERE u.role > 1 
        AND (
            LOWER(u.name) LIKE LOWER(CONCAT('%', :searchKey, '%'))
             OR LOWER(u.id) LIKE LOWER(CONCAT('%', :searchKey, '%'))
            )
        AND u.isActive = :isActive
        AND fac.id IN :facilityIds
    """)
    Page<User> findAllUserBySearchKeyForManager(@Param("searchKey") String searchKey,
                                                @Param("facilityIds") List<Integer> facilityIds,
                                                @Param("isActive") Boolean isActive,
                                                Pageable pageable);

    @Query("""
    SELECT u 
    FROM User u
    JOIN FETCH u.facility fac       
    WHERE u.role = :role
        AND (
            LOWER(u.name) LIKE LOWER(CONCAT('%', :searchKey, '%'))
            OR LOWER(u.id) LIKE LOWER(CONCAT('%', :searchKey, '%'))
            ) 
        AND u.isActive = :isActive
        AND fac IS NULL
    """)
    Page<User> searchAllNonFacilityUserWithRole(@Param("searchKey") String searchKey,
                                                         @Param("role") Integer role,
                                                         @Param("isActive") Boolean isActive,
                                                         Pageable pageable);
    @Query("""
    SELECT u 
    FROM User u
    JOIN FETCH u.facility fac   
    WHERE u.role > 1 
        AND (
            LOWER(u.name) LIKE LOWER(CONCAT('%', :searchKey, '%'))
             OR LOWER(u.id) LIKE LOWER(CONCAT('%', :searchKey, '%'))
            )
        AND u.isActive = :isActive
        AND fac.id = :facilityId
    """)
    Page<User> findAllFacilityUserBySearchKeyForManager(@Param("searchKey") String searchKey,
                                                        @Param("facilityId") Integer facilityId,
                                                        @Param("isActive") Boolean isActive,
                                                        Pageable pageable);
    List<User> findAllByRoleAndIsActive(Integer role, Boolean isActive);

    Page<User> findByFacilityIsNullAndRoleAndIsActive(
            Integer role,
            Boolean isActive,
            Pageable pageable
    );

    Page<User> findAllByIsActiveAndRoleGreaterThan(Boolean isActive, Integer role, Pageable pageable);

    Integer countByRole(Integer role);
}
