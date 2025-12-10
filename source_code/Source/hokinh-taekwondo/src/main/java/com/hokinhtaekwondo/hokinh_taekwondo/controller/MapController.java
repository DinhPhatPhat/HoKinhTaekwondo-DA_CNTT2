package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.service.GeocodingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/maps")
public class MapController {

    private final GeocodingService geocodingService;

    public MapController(
            GeocodingService geocodingService
    ) {
        this.geocodingService = geocodingService;
    }

    @GetMapping("/resolve-address")
    public ResponseEntity<?> resolveAddress(@RequestParam Double lat,
                                            @RequestParam Double lng) {
        try {
            String address = geocodingService.getAddressFromCoordinates(lat, lng);

            return ResponseEntity.ok(address);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

