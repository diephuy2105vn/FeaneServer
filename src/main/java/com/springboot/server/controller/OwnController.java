package com.springboot.server.controller;

import com.springboot.server.models.*;
import com.springboot.server.payload.exception.ResourceNotFoundException;
import com.springboot.server.payload.request.OrderRequest;
import com.springboot.server.payload.request.ProductRequest;
import com.springboot.server.payload.response.*;
import com.springboot.server.repository.*;
import com.springboot.server.service.CloudinaryService;
import com.springboot.server.service.UserDetailsImpl;
import com.springboot.server.service.UserDetailsServiceImpl;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/own")
@PreAuthorize("hasRole('OWNER')")
public class OwnController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private ShopRepository shopRespository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;


    private UserDetailsImpl getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return (UserDetailsImpl) authentication.getPrincipal();
        } else {
            return null;
        }
    };

    @GetMapping("/shop/all")
    public ResponseEntity<?> getAllShop () {
        UserDetailsImpl userDetails = getUserDetails();
        System.out.println(userDetails.getUsername());
        List<ShopResponse> shopResponses = shopRespository.findAllByUsername(userDetails.getUsername()).stream()
                .map(ShopResponse::new)
                .sorted(Comparator.comparing(ShopResponse::getCreatedAt))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(shopResponses);
    }
    @PostMapping("/{shopName}/product")
    public ResponseEntity<?> createProduct(@PathVariable String shopName, @ModelAttribute ProductRequest productRequest) {
        try {
            UserDetailsImpl userDetails = getUserDetails();
            Shop shop = shopRespository.findByNameAndUsername(shopName, userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found shop"));
            Product product = new Product(productRequest);
            product.setShop(shop);
            productRepository.save(product);
            List<Image> images = Arrays.stream(productRequest.getImages().toArray(new MultipartFile[0])).map(image -> {
                Map data = cloudinaryService.upload(image);
                Image imageSaved = imageRepository.save(new Image(product, (String)data.get("url"), (String)data.get("public_id")));
                return imageSaved;
            }).collect(Collectors.toList());
            product.setImages(images);
            return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS, "Create product successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }
    @GetMapping("/{shopName}/product/all")
    public ResponseEntity<?> getProductAll (@PathVariable String shopName,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "12") int size,
                                            @RequestParam(required = false) List<String> types,
                                            @RequestParam(required = false) String sort,
                                            @RequestParam(required = false) String q) {
        try {
            UserDetailsImpl userDetails = getUserDetails();
            Shop shop = shopRespository.findByNameAndUsername(shopName, userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found shop"));
            AtomicInteger totalProduct = new AtomicInteger();
            List<ProductResponse> productResponses;
            if(q != null && !q.isEmpty()) {
                Pageable pageable = PageRequest.of(page - 1, size);
                totalProduct.set(productRepository.countByShopIdAndNameContaining(shop.getId(), q));
                productResponses =  productRepository.findAllByShopIdAndNameContaining(shop.getId(), q, pageable).stream()
                        .map(ProductResponse::new)
                        .toList();

                return ResponseEntity.ok().body(productResponses);
            }
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
            Pageable pageable = PageRequest.of(page - 1, size, sortSpecification);
            if(types != null && !types.isEmpty() ) {
                productResponses = new ArrayList<>();
                types.forEach(type ->{
                    totalProduct.addAndGet(productRepository.countByShopIdAndType(shop.getId(), type));
                    List<ProductResponse> products =  productRepository.findAllByShopIdAndType(shop.getId(), type, pageable).stream()
                            .map(ProductResponse::new)
                            .toList();
                    productResponses.addAll(products);
                });
            }
            else {
                totalProduct.set(productRepository.countByShopId(shop.getId()));
                productResponses = productRepository.findAllByShopId(shop.getId(), pageable).stream()
                        .map(ProductResponse::new)
                        .collect(Collectors.toList());
            };
            ListResponse<ProductResponse> listResponse = new ListResponse<ProductResponse>(totalProduct.get(), productResponses);
            return ResponseEntity.ok().body(listResponse);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }
    @GetMapping("/{shopName}/product/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable String shopName, @PathVariable Long productId) {
        try {
            UserDetailsImpl userDetails = getUserDetails();
            Shop shop = shopRespository.findByNameAndUsername(shopName, userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found shop"));
            Product product = productRepository.findByShopIdAndProductId(shop.getId(),productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found product"));
            ProductResponse productResponse= new ProductResponse(product);

            return ResponseEntity.ok().body(productResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }
    @PutMapping("/{shopName}/product/{productId}")
    public ResponseEntity<?> editProduct(@PathVariable String shopName, @PathVariable long productId, @ModelAttribute ProductRequest productRequest) {
        try {
            UserDetailsImpl userDetails = getUserDetails();

            Shop shop = shopRespository.findByNameAndUsername(shopName, userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found shop"));
            Product product = productRepository.findByShopIdAndProductId(shop.getId(),productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found product"));
            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setQuantity(productRequest.getQuantity());
            product.setPrice(productRequest.getPrice());
            if(productRequest.getImages()!=null && !productRequest.getImages().isEmpty()) {
                product.getImages().forEach(image -> {
                    cloudinaryService.delete(image.getPublicId());
                });
                List<Image> images = Arrays.stream(productRequest.getImages().toArray(new MultipartFile[0])).map(image -> {
                    Map data = cloudinaryService.upload(image);
                    Image imageSaved = imageRepository.save(new Image(product, (String) data.get("url"), (String) data.get("public_id")));
                    return imageSaved;
                }).collect(Collectors.toList());
                product.setImages(images);
            }
            productRepository.save(product);
            return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS, "Edit product successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }
    @DeleteMapping("/{shopName}/product/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable String shopName, @PathVariable Long productId) {
        UserDetailsImpl userDetails = getUserDetails();

        Shop shop = shopRespository.findByNameAndUsername(shopName, userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Not found shop"));

        Product product = productRepository.findByShopIdAndProductId(shop.getId(),productId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found product"));
        product.getImages().forEach(image -> {
            cloudinaryService.delete(image.getPublicId());
        });

        productRepository.delete(product);
        return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS, "Delete product successfully"));
    }

    @GetMapping("/{shopName}/order/all")
    public ResponseEntity<?> getAllOrder (@PathVariable String shopName,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "12") int size,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(required = false, defaultValue = "ALL") String timeFrame) {
        try {
            UserDetailsImpl userDetails = getUserDetails();
            Sort sortSpecification = Sort.unsorted();

            if(sort != null) {
                switch (sort) {
                    case "NAME_ASC":
                        sortSpecification = Sort.by("name").ascending();
                        break;
                    case "NAME_DESC":
                        sortSpecification = Sort.by("name").descending();
                        break;
                    case "DATE_ASC":
                        sortSpecification = Sort.by("createdAt").ascending();
                        break;
                    case "DATE_DESC":
                        sortSpecification = Sort.by("createdAt").descending();
                        break;
                    default: break;
                }
            }
            Pageable pageable = PageRequest.of(page - 1, size, sortSpecification);
            Shop shop = shopRespository.findByNameAndUsername(shopName, userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found shop"));
            List<OrderResponse> orderResponses;
            Date now = new Date();

            Date start = shop.getCreatedAt();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date end = calendar.getTime();

            switch (timeFrame) {
                case "WEEK":
                    // Đặt start và end là ngày đầu tiên và cuối cùng của tuần hiện tại
                    calendar.setTime(now);
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    start = calendar.getTime();
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    end = calendar.getTime();
                    break;
                case "MONTH":
                    // Đặt start và end là ngày đầu tiên và cuối cùng của tháng hiện tại
                    calendar.setTime(now);
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    start = calendar.getTime();
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    end = calendar.getTime();
                    break;
                case "YEAR":
                    // Đặt start và end là ngày đầu tiên và cuối cùng của năm hiện tại
                    calendar.setTime(now);
                    calendar.set(Calendar.DAY_OF_YEAR, 1);
                    start = calendar.getTime();
                    calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
                    end = calendar.getTime();
                    break;
                default:
                    break;
            }
            if(status != null && !status.isEmpty()) {
                orderResponses = orderRepository
                        .findAllByShopIdAndStatusAndCreatedAtBetween(shop.getId(), status, start, end, pageable)
                        .stream()
                        .map(OrderResponse::new).toList();
            }
            else {
                orderResponses = orderRepository.findAllByShopIdAndCreatedAtBetween(shop.getId(), start, end, pageable).stream()
                        .map(OrderResponse::new).toList();
            }

            return ResponseEntity.ok().body(orderResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @GetMapping("/{shopName}/order/{orderId}")
    public ResponseEntity<?> getOrder (@PathVariable String shopName, @PathVariable long orderId) {
        try {
            UserDetailsImpl userDetails = getUserDetails();

            Shop shop = shopRespository.findByNameAndUsername(shopName, userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found shop"));
            OrderTb orderTb = orderRepository.findByShopIdAndOrderId(shop.getId(), orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found order"));
            OrderResponse orderResponse = new OrderResponse(orderTb);
            return ResponseEntity.ok().body(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @PostMapping("/{shopName}/order/{orderId}")
    public ResponseEntity<?> editOrder (@PathVariable String shopName, @PathVariable long orderId, @RequestBody OrderRequest orderRequest) {
        try {
            UserDetailsImpl userDetails = getUserDetails();

            Shop shop = shopRespository.findByNameAndUsername(shopName, userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found shop"));
            OrderTb orderTb = orderRepository.findByShopIdAndOrderId(shop.getId(), orderId).orElseThrow();
            orderTb.setName(orderRequest.getName());
            orderTb.setAddress(orderRequest.getAddress());
            orderTb.setPhoneNumber(orderRequest.getPhoneNumber());
            orderRepository.save(orderTb);
            return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, "Edit successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @PostMapping("/{shopName}/order/{orderId}/confirm")
    public ResponseEntity<?> confirmOrder (@PathVariable String shopName, @PathVariable long orderId) {
        try {
            UserDetailsImpl userDetails = getUserDetails();

            Shop shop = shopRespository.findByNameAndUsername(shopName, userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found shop"));
            OrderTb orderTb = orderRepository.findByShopIdAndOrderId(shop.getId(), orderId).orElseThrow();
            orderTb.setStatus("CONFIRMED");
            orderRepository.save(orderTb);
            return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, "Confirmed order successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @DeleteMapping("/{shopName}/order/{orderId}")
    public ResponseEntity<?> editOrder (@PathVariable String shopName, @PathVariable long orderId) {
        try {
            UserDetailsImpl userDetails = getUserDetails();

            Shop shop = shopRespository.findByNameAndUsername(shopName, userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found shop"));
            OrderTb orderTb = orderRepository.findByShopIdAndOrderId(shop.getId(), orderId).orElseThrow();
            orderTb.getOrderDetails().forEach(orderDetail -> {
                orderDetailRepository.delete(orderDetail);
            });
            orderRepository.delete(orderTb);
            return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, "Delete successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }


}
