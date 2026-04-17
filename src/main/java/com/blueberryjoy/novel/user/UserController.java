package com.blueberryjoy.novel.user;

import com.blueberryjoy.novel.exception.models.ApiException;
import com.blueberryjoy.novel.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping()
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("name", body.get("username"));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, Object> body) {
        try {
            String username = (String) body.get("username");
            String email = (String) body.get("email");
            String password = (String) body.get("password");
            if (username == null || email == null || password == null)
                throw new ApiException("Please complete all required fields to proceed.", HttpStatus.BAD_REQUEST);
            User user = userService.createUser(username, email, password);
            ResponseCookie accessCookie = jwtUtils.setAccessCookie(user);
            ResponseCookie refreshCookie = jwtUtils.setRefreshCookie(user);
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).body(new UserResponse(true, null, new UserDTO(user)));
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
                throw new ApiException("Incorrect email or password. Please try again.", HttpStatus.BAD_REQUEST);
            ResponseCookie accessCookie = jwtUtils.setAccessCookie(user);
            ResponseCookie refreshCookie = jwtUtils.setRefreshCookie(user);
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).body(new UserResponse(true, null, new UserDTO(user)));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record UserResponse(boolean success, String message, UserDTO user) {
    }
}
