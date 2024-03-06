package com.springboot.server.payload.request;

import com.springboot.server.payload.constants.EProductType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Getter
@Setter
public class ProductRequest
{
    private String name;
    private String description;
    private EProductType type;
    private int quantity;
    private long price;
    private String note;
    private List<MultipartFile> images;
}
