package com.springboot.server.controller;

import com.springboot.server.models.*;
import com.springboot.server.payload.exception.ResourceNotFoundException;
import com.springboot.server.payload.request.CartDetailRequest;
import com.springboot.server.payload.response.*;
import com.springboot.server.repository.*;
import com.springboot.server.service.UserDetailsImpl;
import com.springboot.server.service.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    private UserDetailsImpl getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl)authentication.getPrincipal();
    };

    @GetMapping("/search")
    public ResponseEntity<?> findUsers (@RequestParam String q) {
        try {
            UserDetailsImpl userDetails = getUserDetails();
            Set<UserResponse> users = userRepository.findByUsernameOrNameContaining(q).stream()
                    .filter(item -> !item.getUsername().equals(userDetails.getUsername()))
                    .map(item -> {
                        return new UserResponse(item.getId(), item.getUsername(), item.getName(), item.getAvatar());
                    })
                    .collect(Collectors.toSet());

            return ResponseEntity.ok().body(users);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<?> getFriends()  {
        try {
            UserDetailsImpl userDetails = getUserDetails();
            Set<User> userfriends = userRepository.findByUsername(userDetails.getUsername())
                    .map(item->item.getFriends())
                    .orElseThrow(()-> new ResourceNotFoundException("User not found"));
            List<UserResponse> friendInfoResponseList = userfriends.stream()
                    .map(item -> new UserResponse(item.getId(), item.getUsername(), item.getName(), item.getAvatar()))
                    .toList();
            return ResponseEntity.ok().body(friendInfoResponseList);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @PostMapping("/friend")
    public ResponseEntity<MessageResponse> addFriend(@RequestParam String username) {
        try {
            UserDetailsImpl userDetails = getUserDetails();
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found user current"));
            User friendUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found friend current"));;

            currentUser.getFriends().add(friendUser);
            friendUser.getFriends().add(currentUser);
            userRepository.save(currentUser);
            userRepository.save(friendUser);

            return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS, "Okay"));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @DeleteMapping("/friend")
    public ResponseEntity<MessageResponse> deleteFriend(@RequestParam String username) {
        try {
            System.out.println(username);
            UserDetailsImpl userDetails = getUserDetails();
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found user current"));
            User friendUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found friend current"));;
            if(currentUser.getFriends().contains(friendUser)) {
                currentUser.getFriends().remove(friendUser);
                friendUser.getFriends().remove(currentUser);
                userRepository.save(currentUser);
                userRepository.save(friendUser);
                return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS, "Okay"));
            }
            else {
                throw new Exception("Username is not in friends");
            }
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @GetMapping("/delAddresses")
    public ResponseEntity<?> getAddresses() {
        UserDetailsImpl userDetails = getUserDetails();
        List<DeliveryAddressResponse> deliveryAddressesResponse = deliveryAddressRepository.findByUserId(userDetails.getId()).stream()
                .map(item ->
                        new DeliveryAddressResponse(item.getId(), item.getName(), item.getPhoneNumber(), item.getAddress(), item.getIsDefault()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(deliveryAddressesResponse);
    }
    @PostMapping("/delAddress/createOrUpdate")
    public ResponseEntity<?> createOrUpdateDelAddress(@RequestBody DeliveryAddress newDelAddress) {
        try {
            UserDetailsImpl userDetails = getUserDetails();

            if( newDelAddress.getId() != null) {
                deliveryAddressRepository.findById(newDelAddress.getId())
                        .map(item -> {
                            item.setPhoneNumber(newDelAddress.getPhoneNumber());
                            item.setName(newDelAddress.getName());
                            item.setAddress(newDelAddress.getAddress());
                            return deliveryAddressRepository.save(item);
                        })
                        .orElseGet(() -> {
                            return deliveryAddressRepository.save(newDelAddress);
                        });
            }
            else {
                User user = userRepository.findByUsername(userDetails.getUsername())
                        .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));;
                newDelAddress.setUser(user);
                newDelAddress.setIsDefault(false);
                Long newId = deliveryAddressRepository.save(newDelAddress).getId();
                newDelAddress.setId(newId);
            }
            return ResponseEntity.ok().body(new DeliveryAddressResponse(newDelAddress.getId(), newDelAddress.getName(),
                    newDelAddress.getPhoneNumber(),newDelAddress.getAddress(), newDelAddress.getIsDefault()));
        } catch(Exception e) {
             return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @PostMapping("/delAddress/setDefault")
    public ResponseEntity<?> setDefaultDelAddress(@RequestBody DeliveryAddress delAddress) {
         try {
             UserDetailsImpl userDetails = getUserDetails();

             // Đặt địa chỉ mặc định củ thành false
             deliveryAddressRepository.findByUserIdAndIsDefaultTrue(userDetails.getId()).map(item -> {
                        item.setIsDefault(false);
                        return deliveryAddressRepository.save(item);
                    })
                    .orElseThrow(() -> (new ResourceNotFoundException("Delivery address default not found")));
             // Đặt địa chỉ mặc định mới thành true
             deliveryAddressRepository.findById(delAddress.getId())
                    .map(item -> {
                        item.setIsDefault(true);
                        return deliveryAddressRepository.save(item);
                    })
                    .orElseThrow(() -> (new ResourceNotFoundException("Delivery address not found")));;
                return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS, "Set default sussessfully"));
        } catch (Exception e) {
             return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
         }
    }
    @DeleteMapping("/delAddress/delete/{id}")
    public ResponseEntity<?> deleteDelAddress (@PathVariable Long id) {
        try {
            deliveryAddressRepository.findByIdAndIsDefaultFalse(id)
                    .map(item -> {
                        deliveryAddressRepository.deleteById(item.getId());
                        return true;
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Del Address Not Found"));
            return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS, "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @GetMapping("/cart")
    public ResponseEntity<?> getCart() {
        try {
            UserDetailsImpl userDetails = getUserDetails();
            Cart cart  = cartRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found cart"));
            CartResponse cartResponse = new CartResponse(cart);
            return ResponseEntity.ok().body(cartResponse);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @PostMapping("/cart")
    public ResponseEntity<?> addCartDetail(@RequestBody CartDetailRequest cartDetailRequest) {
        try {
            UserDetailsImpl userDetails = getUserDetails();
            Cart cart  = cartRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found cart"));
            Product product = productRepository.findById(cartDetailRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found product"));
            CartDetail cartDetail = cartDetailRepository.findByCartIdAndProductId(cart.getId(), cartDetailRequest.getProductId());
            if(cartDetail != null) {
                cartDetail.setQuantity(cartDetailRequest.getQuantity());
                cartDetailRepository.save(cartDetail);
            }
            else {
                CartDetail cartDetailNew = new CartDetail(cart, product, cartDetailRequest.getQuantity());
                cartDetailRepository.save(cartDetailNew);
                cart.getCartDetails().add(cartDetailNew);
            }
            cartRepository.save(cart);
            return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS, "Add successfully"));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }
    @PutMapping("/cart")
    public ResponseEntity<?> changeQuantityCartDetail(@RequestBody CartDetailRequest cartDetailRequest) {
        try {
            UserDetailsImpl userDetails = getUserDetails();
            Cart cart = cartRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found cart"));
            CartDetail cartDetail = cartDetailRepository.findByCartIdAndProductId(cart.getId(), cartDetailRequest.getProductId());
            if(cartDetail != null) {
                cartDetail.setQuantity(cartDetailRequest.getQuantity());
                cartDetailRepository.save(cartDetail);
                return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS, "Change quantity successfully"));
            }
            else {
                throw new ResourceNotFoundException("Not found cart detail");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }

    @DeleteMapping("/cart/{productId}")
    public ResponseEntity<?> deleteCartDetail(@PathVariable Long productId) {
        try {
            UserDetailsImpl userDetails = getUserDetails();
            Cart cart = cartRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Not found cart"));
            CartDetail cartDetail = cartDetailRepository.findByCartIdAndProductId(cart.getId(), productId);
            cartDetailRepository.delete(cartDetail);
            return ResponseEntity.ok().body(new MessageResponse(EMessageResponse.MESSAGE_SUCCESS, "Delete successfully"));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMessageResponse.MESSAGE_ERROR, e.getMessage()));
        }
    }
}
