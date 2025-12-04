package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.cloudinary.utils.ObjectUtils;
import com.hokinhtaekwondo.hokinh_taekwondo.service.CloudinaryService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/api/cloudinary")
public class CloudinaryController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @PostMapping("/admin/upload-signature")
    public ResponseEntity<?> getUploadSignature(@RequestBody Map<String, String> body) {
        String publicId = body.get("public_id");
        String folder = body.get("folder");
        long timestamp = System.currentTimeMillis() / 1000;

        Map<String, Object> paramsToSign = new TreeMap<>();
        paramsToSign.put("folder", folder);
        paramsToSign.put("use_filename", true);
        paramsToSign.put("unique_filename", false);
        paramsToSign.put("overwrite", true);
        paramsToSign.put("timestamp", timestamp);

        StringBuilder toSign = new StringBuilder();
        paramsToSign.forEach((key, value) -> {
            if(!toSign.isEmpty()) toSign.append("&");
            toSign.append(key).append("=").append(value);
        });

        String signature = DigestUtils.sha1Hex(toSign + apiSecret);

        return ResponseEntity.ok().body(Map.of(
                "timestamp", timestamp,
                "signature", signature,
                "uniqueFileName", false,
                "useFileName", true,
                "folder", folder,
                "overwrite", true,
                "apiKey", apiKey,
                "cloudName", cloudName
        ));
    }

    @PostMapping("/admin/delete-signature")
    public ResponseEntity<?> getDeleteSignature(@RequestBody Map<String, String> body) {
        String publicId = body.get("public_id");
        long timestamp = System.currentTimeMillis() / 1000;

        Map<String, Object> paramsToSign = new TreeMap<>();
        paramsToSign.put("public_id", publicId);
        paramsToSign.put("timestamp", String.valueOf(timestamp));

        StringBuilder toSign = new StringBuilder();
        paramsToSign.forEach((key, value) -> {
            if(!toSign.isEmpty()) toSign.append("&");
            toSign.append(key).append("=").append(value);
        });

        String signature = DigestUtils.sha1Hex(toSign + apiSecret);

        return ResponseEntity.ok().body(ObjectUtils.asMap(
                "signature", signature,
                "timestamp", timestamp,
                "api_key", apiKey,
                "cloud_name", cloudName,
                "public_id", publicId
        ));
    }

    @PostMapping("/admin/delete-images")
    public ResponseEntity<?> deleteImages(@RequestBody List<String> publicIds) {
        try {
            var result = cloudinaryService.deleteImages(publicIds);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to delete images: " + e.getMessage());
        }
    }
}
