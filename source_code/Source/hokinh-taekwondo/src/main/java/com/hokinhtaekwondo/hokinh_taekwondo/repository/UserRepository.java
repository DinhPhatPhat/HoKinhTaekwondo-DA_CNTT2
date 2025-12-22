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

    @Query("SELECT u.id FROM User u WHERE u.id IN :ids")
    List<String> findExistingIds(@Param("ids") List<String> ids);

    List<User> findAllByRole(Integer role);
    Long countById(String id);

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
    List<User> findByFacility_Id(Integer id);
    @Query("""
    SELECT u 
    FROM User u
    JOIN FETCH u.facility fac
    WHERE fac.id = :facilityId
    AND u.role > 1
    """)
    List<User> findUsersByFacilityIdForManager(@Param("facilityId") Integer facilityId);

    List<User> findByFacilityIsNullAndRoleEquals(Integer role);

    Integer countByRole(Integer role);
}
