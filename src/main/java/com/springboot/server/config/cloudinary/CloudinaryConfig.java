package com.springboot.server.config.cloudinary;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary getCloudinary() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("cloud_name", "df6gmw5it");
        config.put("api_key", "245551815923335");
        config.put("api_secret", "sFKfGt9o_VW9CRxgFWoksfXMm2M");
        config.put("secure", true);
        return new Cloudinary(config);
    }
}