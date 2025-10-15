package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.model.Award;
import com.hokinhtaekwondo.hokinh_taekwondo.service.AwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/award")
public class AwardController {
    @Autowired
    private AwardService awardService;

    @GetMapping("/homepage")
    public ResponseEntity<?> getAwardHomepage() {
        return ResponseEntity.ok(awardService.getAllAwards());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addAward(@RequestBody Award award) {
        try {
            Award savedAward = awardService.createAward(award);
            return ResponseEntity.ok(savedAward.getName());
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
