package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.model.Award;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.AwardRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.utils.time.VietNamTime;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    public Award patchAward(Long id, Map<String, Object> updatedFields) {
        Award award = awardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giải thưởng tương ứng"));

        if(updatedFields.containsKey("isDeleted")) {
            award.setDeleted((Boolean) updatedFields.get("isDeleted"));
            award.setDeletedAt(VietNamTime.nowDateTime());
        }
        // other empty fields
        return awardRepository.save(award);
    }

    public Award updateAward(Long id, Award award) {
        Award awardInfo = awardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giải thưởng tương ứng"));
        if(award.getYear().isEmpty()) {
            throw new ValidationException("Chưa chọn năm đạt giải");
        }
        else {
            awardInfo.setYear(award.getYear());
            awardInfo.setName(award.getName());
            awardInfo.setDescription(award.getDescription());
            awardInfo.setImage(award.getImage());
            awardInfo.setRank(award.getRank());
            return awardRepository.save(awardInfo);
        }
    }

    public List<Award> getAllAwards() {
        return awardRepository.findAllByIsDeletedEquals(false);
    }

    public List<Award> getAllDeletedAwards() {
        return awardRepository.findAllByIsDeletedEquals(true);
    }

    public Award delete(Long id) {
        Award awardInfo = awardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giải thưởng tương ứng"));
        awardRepository.delete(awardInfo);
        return awardInfo;
    }

    @Transactional
    public void deleteAll() {
        // Permanently Delete Awards
        awardRepository.deleteAllByIsDeletedEquals(true);
    }
}
