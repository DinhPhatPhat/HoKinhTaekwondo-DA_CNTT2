package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.model.Award;
import com.hokinhtaekwondo.hokinh_taekwondo.service.AwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/award")
public class AwardController {
    @Autowired
    private AwardService awardService;

    @GetMapping("/homepage")
    public ResponseEntity<?> getAwardHomepage() {
        return ResponseEntity.ok(awardService.getAllAwards());
    }

    @GetMapping("/admin/all_deleted_awards")
    public ResponseEntity<?> getAllDeletedAwards() {
        return ResponseEntity.ok(awardService.getAllDeletedAwards());
    }

    @PostMapping("/admin/add")
    public ResponseEntity<?> addAward(@RequestBody Award award) {
        try {
            Award savedAward = awardService.createAward(award);
            return ResponseEntity.ok(savedAward);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<?> updateAward(   @PathVariable Long id,
                                            @RequestBody Award award) {
        try {
            Award savedAward = awardService.updateAward(id, award);
            return ResponseEntity.ok(savedAward);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/admin/patch/{id}")
    public ResponseEntity<?> patchAward(   @PathVariable Long id,
                                           @RequestBody Map<String, Object> updatedFields) {
        try {
            return ResponseEntity.ok(awardService.patchAward(id, updatedFields));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> deleteAward(@PathVariable Long id) {
        try {
            Award deletedAward = awardService.delete(id);
            return ResponseEntity.ok(deletedAward);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/admin/delete_all")
    public ResponseEntity<?> deletedAllAward() {
        try {
            awardService.deleteAll();
            return ResponseEntity.ok("Đã xóa thành công tất cả giải thưởng");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
