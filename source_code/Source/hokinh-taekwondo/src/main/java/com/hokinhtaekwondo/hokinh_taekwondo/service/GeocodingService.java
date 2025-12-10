package com.hokinhtaekwondo.hokinh_taekwondo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeocodingService {

    @Value("${google.geocoding.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAddressFromCoordinates(double lat, double lng) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                + lat + "," + lng + "&key=" + apiKey;

        Map<?, ?> response = restTemplate.getForObject(url, Map.class);

        if (response == null) {
            throw new RuntimeException("Failed to fetch address");
        }
        else if (!"OK".equals(response.get("status"))) {
            Object errorMsg = response.get("error_message");
            throw new RuntimeException(errorMsg != null ? errorMsg.toString() : "Unknown error from Google API");
        }


        List<?> results = (List<?>) response.get("results");
        if (results.isEmpty()) {
            return null;
        }

        Map<?, ?> first = (Map<?, ?>) results.getFirst();
        return (String) first.get("formatted_address");
    }
}

