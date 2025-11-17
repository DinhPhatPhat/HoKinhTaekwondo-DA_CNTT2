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

    Page<User> findByIsActiveTrueAndRoleAndNameContainingIgnoreCase(int i, String searchKey, Pageable pageable);

    Page<User> findByIsActiveTrueAndRoleInAndNameContainingIgnoreCase(Collection<Integer> role, String name, Pageable pageable);
}
