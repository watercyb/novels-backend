package com.blueberryjoy.novel.user;

import com.blueberryjoy.novel.exception.models.ApiException;
import com.blueberryjoy.novel.security.JwtUtils;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //@PostMapping()
    //public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> body) {
    //Map<String, Object> response = new HashMap<>();
    //response.put("name", body.get("username"));
    //return ResponseEntity.status(HttpStatus.CREATED).body(response);
    //}

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, Object> body) {
        try {
            String username = (String) body.get("username");
            String email = (String) body.get("email");
            String password = (String) body.get("password");
            if (username == null || email == null || password == null)
                throw new ApiException("Please complete all required fields to proceed.", HttpStatus.BAD_REQUEST);
            User user = userService.createUser(username, email, password);
            ResponseCookie accessCookie = jwtUtils.getAccessCookie(user);
            ResponseCookie refreshCookie = jwtUtils.getRefreshCookie(user);
            return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.SET_COOKIE, accessCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).body(new UserResponse(true, null, new UserDTO(user)));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        try {
            String email = (String) body.get("email");
            String password = (String) body.get("password");
            if (email == null || password == null)
                throw new ApiException("Please complete all required fields to proceed.", HttpStatus.BAD_REQUEST);
            User user = userService.findUserByEmail(email);
            if (user == null || !passwordEncoder.matches(password, user.getPasswordHash()))
                throw new ApiException("Incorrect email or password. Please try again.", HttpStatus.UNAUTHORIZED);
            ResponseCookie accessCookie = jwtUtils.getAccessCookie(user);
            ResponseCookie refreshCookie = jwtUtils.getRefreshCookie(user);
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).body(new UserResponse(true, null, new UserDTO(user)));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        try {
            String token = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("refreshToken".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
            if (token == null) throw new ApiException("Please login.", HttpStatus.UNAUTHORIZED);
            User user = jwtUtils.verifyRefreshToken(token);
            if (user == null) throw new ApiException("Please login.", HttpStatus.UNAUTHORIZED);
            ResponseCookie accessCookie = jwtUtils.getAccessCookie(user);
            ResponseCookie refreshCookie = jwtUtils.getRefreshCookie(user);
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).body(new UserResponse(true, null, new UserDTO(user)));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            ResponseCookie accessCookie = jwtUtils.getEmptyAccessCookie();
            ResponseCookie refreshCookie;
            if (auth != null) {
                User user = (User) auth.getPrincipal();
                refreshCookie = jwtUtils.getEmptyRefreshCookie(user);
            } else {
                refreshCookie = jwtUtils.getEmptyRefreshCookie(null);
            }
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).header(HttpHeaders.SET_COOKIE, accessCookie.toString()).body(new UserResponse(true, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestParam("avatar") MultipartFile image, @RequestParam("username") Long username) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to update user info at this time.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();

            if (!image.isEmpty()) {
                String contentType = image.getContentType();
                if (contentType != null && contentType.startsWith("image/")) {
                    if (image.getSize() > 5 * 1024 * 1024)
                        throw new ApiException("This file is larger than 5MB. Please upload a smaller version.", HttpStatus.BAD_REQUEST);
                }
            }

            // Example checks


            return ResponseEntity.ok("Uploaded successfully");
        } catch (
                ApiException e) {
            throw e;
        } catch (
                Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record UserResponse(boolean success, String message, UserDTO user) {
    }
}
