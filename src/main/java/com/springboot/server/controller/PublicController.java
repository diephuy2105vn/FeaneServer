package com.springboot.server.controller;

import com.springboot.server.models.Product;
import com.springboot.server.payload.exception.ResourceNotFoundException;
import com.springboot.server.payload.response.EMessageResponse;
import com.springboot.server.payload.response.MessageResponse;
import com.springboot.server.payload.response.ProductResponse;
import com.springboot.server.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private ProductRepository productRepository;
    @GetMapping("/product/all")
    public ResponseEntity<?> getProductAll (@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "12") int size,
                                            @RequestParam(required = false) List<String> types,
                                            @RequestParam(required = false) String sort) {
        Sort sortSpecification = Sort.unsorted();
        if(sort != null) {
            switch (sort) {
                case "NAME_ASC":
                    sortSpecification = Sort.by("name").ascending();
                    break;
                case "NAME_DESC":
                    sortSpecification = Sort.by("name").descending();
                    break;
                case "PRICE_ASC":
                    sortSpecification = Sort.by("price").ascending();
                    break;
                case "PRICE_DESC":
                    sortSpecification = Sort.by("price").descending();
                    break;
                default: break;
            }
        }
        Pageable pageable = PageRequest.of(page, size, sortSpecification);
        List<ProductResponse> productResponses;
        if(types != null && !types.isEmpty() ) {
            productResponses = new ArrayList<>();
            types.forEach(type ->{
                List<ProductResponse> products =  productRepository.findAllByType(type, pageable).stream()
                        .map(ProductResponse::new)
                        .toList();
                productResponses.addAll(products);
            });
        }
        else {
            productResponses = productRepository.findAll(pageable).stream()
                    .map(ProductResponse::new)
                    .collect(Collectors.toList());
        };
        return ResponseEntity.ok().body(productResponses);
    }
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProduct (@PathVariable String productId) {
        try {
            long productIdParsed = Long.parseLong(productId);
            Product product = productRepository.findById(productIdParsed)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found product"));
            ProductResponse productResponse = new ProductResponse(product);
            return ResponseEntity.ok().body(productResponse);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }
}
