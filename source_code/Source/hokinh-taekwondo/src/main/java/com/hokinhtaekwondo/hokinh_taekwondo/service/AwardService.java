package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.model.Award;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.AwardRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.FacilityRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AwardService {
    private final AwardRepository awardRepository;

    public Award createAward(Award award) {
        if(award.getName().isEmpty()) {
            throw new ValidationException("Tên giải thưởng không được để trống");
        }
        else if(award.getRank().isEmpty()) {
            throw new ValidationException("Chưa chọn hạng");
        }
        else if(award.getYear().isEmpty()) {
            throw new ValidationException("Chưa chọn năm đạt giải");
        }
        else {
            return awardRepository.save(award);
        }
    }

    public List<Award> getAllAwards() {
        return awardRepository.findAll();
    }
}
