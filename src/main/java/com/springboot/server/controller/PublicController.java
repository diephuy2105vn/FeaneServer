package com.springboot.server.controller;

import com.springboot.server.models.*;
import com.springboot.server.payload.exception.ResourceNotFoundException;
import com.springboot.server.payload.request.OrderRequest;
import com.springboot.server.payload.response.EMessageResponse;
import com.springboot.server.payload.response.ListResponse;
import com.springboot.server.payload.response.MessageResponse;
import com.springboot.server.payload.response.ProductResponse;
import com.springboot.server.repository.*;
import com.springboot.server.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ShopCartRepository shopCartRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    private UserDetailsImpl getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl)authentication.getPrincipal();
    };
    @GetMapping("/product/all")
    public ResponseEntity<?> getProductAll (
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) String sort) {
        AtomicInteger totalProduct = new AtomicInteger();
        List<ProductResponse> productResponses;
        if(q != null && !q.isEmpty()) {
            Pageable pageable = PageRequest.of(page - 1, size);
            totalProduct.set(productRepository.countByNameContaining(q));
            productResponses =  productRepository.findAllByNameContaining(q, pageable).stream()
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
        Pageable pageable = PageRequest.of(page -1 , size, sortSpecification);
        if(types != null && !types.isEmpty() ) {
            productResponses = new ArrayList<>();
            types.forEach(type ->{
                totalProduct.addAndGet(productRepository.countByType(type));
                List<ProductResponse> products =  productRepository.findAllByType(type, pageable).stream()
                        .map(ProductResponse::new)
                        .toList();
                productResponses.addAll(products);
            });
        }
        else {
            totalProduct.set((int)productRepository.count());
            productResponses = productRepository.findAll(pageable).stream()
                    .map(ProductResponse::new)
                    .collect(Collectors.toList());
        };
        ListResponse<ProductResponse> listResponse = new ListResponse<ProductResponse>(totalProduct.get(), productResponses);
        return ResponseEntity.ok().body(listResponse);
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
    @PostMapping("/order")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            UserDetailsImpl userDetails = getUserDetails();

            orderRequest.getDetails().forEach(detail -> {
                Shop shop = shopRepository.findById(detail.getShopId())
                        .orElseThrow(() -> new ResourceNotFoundException("Not found shop"));
                OrderTb order = new OrderTb(orderRequest);
                order.setShop(shop);
                if(userDetails != null) {
                    User user = userRepository.findByUsername(userDetails.getUsername())
                            .orElseThrow(() -> new ResourceNotFoundException("Not found user"));

                    order.setUser(user);
                }
                orderRepository.save(order);
                AtomicLong totalPrice = new AtomicLong();
                detail.getCartDetails().forEach(item -> {
                    Product product= productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Not found product"));
                    if(orderRequest.getOrderByCart() && userDetails!=null) {
                        Cart cart = cartRepository.findByUsername(userDetails.getUsername())
                                .orElseThrow(() -> new ResourceNotFoundException("Not found cart"));
                        CartDetail cartDetail = cartDetailRepository.findByCartIdAndProductId(cart.getId(), product.getId());
                        cartDetailRepository.delete(cartDetail);
                    };
                    OrderDetail orderDetail = new OrderDetail(order, product, item.getQuantity());
                    orderDetailRepository.save(orderDetail);
                    product.setSold(product.getSold() + item.getQuantity());
                    productRepository.save(product);
                    totalPrice.addAndGet(orderDetail.getTotalPrice());
                });
                order.setTotalPrice(totalPrice.get());
                orderRepository.save(order);
            });
            return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS, "Create order successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }
}
