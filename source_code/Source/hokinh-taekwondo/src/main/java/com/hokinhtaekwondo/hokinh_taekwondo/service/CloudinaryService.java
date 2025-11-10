package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public  CloudinaryService(@Value("${cloudinary.cloud-name}") String cloudName,
                              @Value("${cloudinary.api-key}") String apiKey,
                              @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public Map deleteImages(List<String> publicIds) throws Exception {
        return cloudinary.api().deleteResources(publicIds, ObjectUtils.emptyMap());
    }

    public void deleteFolder(String folderPath) throws Exception {
        // Delete all resources in the folder (recursively)
        ApiResponse deleteResources = cloudinary.api().deleteResourcesByPrefix(folderPath,
                ObjectUtils.asMap("invalidate", true));

        System.out.println("Deleted resources: " + deleteResources);

        // Delete the now-empty folder
        ApiResponse deleteFolder = cloudinary.api().deleteFolder(folderPath, ObjectUtils.emptyMap());

        System.out.println("Deleted folder: " + deleteFolder);
    }
}
