package com.springboot.server.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.springboot.server.models.Image;
import com.springboot.server.payload.exception.ResourceNotFoundException;
import com.springboot.server.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Autowired
    private ImageRepository imageRepository;

    public Map upload(MultipartFile file)  {
        try{
            Map<String, Object> uploadOptions = new HashMap<>();
            uploadOptions.put("folder", "Feane");
            Map data = this.cloudinary.uploader().upload(file.getBytes(), uploadOptions);

            return data;
        }catch (IOException io){
            throw new RuntimeException("Image upload fail");
        }
    }
    public Map delete(String publicId) {
        try {
            Map<String, Object> deleteOptions = new HashMap<>();
            deleteOptions.put("folder", "Feane");
            Map result = cloudinary.uploader().destroy(publicId, deleteOptions);
            Image image = imageRepository.findByPublicId(publicId)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found image"));
            imageRepository.delete(image);
            return result;
        } catch (IOException e) {
            throw new ResourceNotFoundException("Not found image");
        }
    }
}