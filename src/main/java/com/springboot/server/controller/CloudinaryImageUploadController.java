package com.springboot.server.controller;

import com.springboot.server.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cloudinary")
@RequiredArgsConstructor
public class CloudinaryImageUploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<List<Map>> uploadImage(@RequestParam("images") MultipartFile[] files){

        List<Map> responses = Arrays.stream(files).map(file -> {
            Map data = this.cloudinaryService.upload(file);
            return data;
        }).collect(Collectors.toList());
        return ResponseEntity.ok().body(responses);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Map> deleteImage(@RequestParam("publicId") String publicId) {
        Map data = this.cloudinaryService.delete(publicId);
        return ResponseEntity.ok().body(data);
    }

}
