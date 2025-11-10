package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.Award;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AwardRepository extends JpaRepository<Award, Long> {
    public List<Award> findAllByIsDeletedEquals(boolean isDeleted);
    public void deleteAllByIsDeletedEquals(boolean isDeleted);
}
