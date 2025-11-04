package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.Facility;
import com.hokinhtaekwondo.hokinh_taekwondo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByFacility(Facility facility);

    Page<User> findByIsActiveTrueAndRoleAndNameContainingIgnoreCase(int i, String searchKey, Pageable pageable);

    Page<User> findByIsActiveTrueAndRoleInAndNameContainingIgnoreCase(Collection<Integer> role, String name, Pageable pageable);
}
