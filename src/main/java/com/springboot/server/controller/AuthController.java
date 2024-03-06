package com.springboot.server.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.springboot.server.config.jwt.JwtUtils;
import com.springboot.server.models.Cart;
import com.springboot.server.models.DeliveryAddress;
import com.springboot.server.payload.constants.ERole;
import com.springboot.server.models.Role;
import com.springboot.server.models.User;
import com.springboot.server.payload.request.LoginRequest;
import com.springboot.server.payload.request.SignupRequest;
import com.springboot.server.payload.response.EMessageResponse;
import com.springboot.server.payload.response.MessageResponse;
import com.springboot.server.payload.response.ShopResponse;
import com.springboot.server.payload.response.UserInfoResponse;
import com.springboot.server.repository.*;
import com.springboot.server.service.UserDetailsImpl;
import com.springboot.server.service.UserDetailsServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ShopRepository shopRespository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;



    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));


            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            ResponseCookie refreshToken = jwtUtils.generateRefreshToken(userDetails);
            ResponseCookie accessToken = jwtUtils.generateAccessToken(userDetails);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            List<ShopResponse> shopResponses = shopRespository.findAllByUsername(userDetails.getUsername()).stream()
                    .map(ShopResponse::new).collect(Collectors.toList());
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshToken.toString())
                    .body(new UserInfoResponse(userDetails.getId(),
                            userDetails.getUsername(),
                            userDetails.getName(),
                            userDetails.getAddress(),
                            userDetails.getPhoneNumber(),
                            userDetails.getAvatar(),
                            shopResponses,
                            roles,
                            accessToken.toString()
                            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR,"The account is not correct"));
        }
    }


    @PostMapping("/checkuser")
    public ResponseEntity<?> checkUsername(@RequestBody String username) {
        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR,"The username is already in use"));
        }
        else {
            return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS,"OKAY"));
        }
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR,"Username is already taken!"));
        }


        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()),signUpRequest.getName(),
                signUpRequest.getPhoneNumber(), signUpRequest.getAddress());





        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "own":
                        Role ownRole = roleRepository.findByName(ERole.ROLE_OWNER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(ownRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        //Tạo giỏ hàng cho người dùng
        Cart cart = new Cart(user);
        cartRepository.save(cart);

        // Tạo địa chỉ nhận hàng mặc định
        DeliveryAddress deliveryAddress = new DeliveryAddress(signUpRequest.getName(), signUpRequest.getPhoneNumber(), signUpRequest.getAddress(), true);
        deliveryAddress.setUser(user);
        deliveryAddressRepository.save(deliveryAddress);

        return ResponseEntity.ok(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS,"User registered successfully!"));
    }



    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS, "You've been signed out!"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshUser(HttpServletRequest request) {
        try {
            String jwt = jwtUtils.getJwtFromCookies(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
                List<String> roles = userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.toList());
                List<ShopResponse> shopResponses = shopRespository.findAllByUsername(userDetails.getUsername()).stream()
                        .map(ShopResponse::new).collect(Collectors.toList());
                ResponseCookie accessToken = jwtUtils.generateAccessToken(userDetails);

                return ResponseEntity.ok()
                        .body(new UserInfoResponse(userDetails.getId(),
                                userDetails.getUsername(),
                                userDetails.getName(),
                                userDetails.getAddress(),
                                userDetails.getPhoneNumber(),
                                userDetails.getAvatar(),
                                shopResponses,
                                roles,
                                accessToken.toString()));
            }
            throw new Exception("Token is not valid");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR,e.getMessage()));
        }
    }


}
