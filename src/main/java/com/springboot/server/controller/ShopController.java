package com.springboot.server.controller;

import com.springboot.server.payload.constants.ERole;
import com.springboot.server.models.Role;
import com.springboot.server.models.Shop;
import com.springboot.server.models.User;
import com.springboot.server.payload.exception.ResourceNotFoundException;
import com.springboot.server.payload.request.ShopRegisterRequest;
import com.springboot.server.payload.response.EMessageResponse;
import com.springboot.server.payload.response.MessageResponse;
import com.springboot.server.payload.response.ShopResponse;
import com.springboot.server.repository.RoleRepository;
import com.springboot.server.repository.ShopRepository;
import com.springboot.server.repository.UserRepository;
import com.springboot.server.service.UserDetailsImpl;
import com.springboot.server.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/shop")
public class ShopController {
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private  RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private UserDetailsImpl getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return (UserDetailsImpl) authentication.getPrincipal();
        } else {
            return null;
        }
    };
    @PostMapping("/register")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> registerOwn(@RequestBody ShopRegisterRequest shopRegisterRequest) {
        System.out.println(shopRegisterRequest.getProductTypes());
        try {
            UserDetailsImpl userDetails = getUserDetails();

            if(userDetails != null) {
                User user = userRepository.findByUsername(userDetails.getUsername())
                        .orElseThrow(() -> new ResourceNotFoundException("Not found user"));
                Shop shops = new Shop(shopRegisterRequest, user);
                Set<Role> roles = new HashSet<>(user.getRoles());
                Role ownRole = roleRepository.findByName(ERole.ROLE_OWNER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

                roles.add(ownRole);
                user.setRoles(roles);
                userRepository.save(user);
                ShopResponse shopResponse = new ShopResponse(shopRepository.save(shops));
                return ResponseEntity.ok().body(shopResponse);
            }
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, "User not found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }
}
