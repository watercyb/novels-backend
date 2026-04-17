package com.blueberryjoy.novel.user;

import com.blueberryjoy.novel.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping()
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("name", body.get("username"));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, Object> body) {
        User user = userService.signup(body);
        ResponseCookie accessCookie = jwtUtils.setAccessCookie(user);
        ResponseCookie refreshCookie = jwtUtils.setRefreshCookie(user);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).body(new UserResponse(true, null, new UserDTO(user)));
    }

    record UserResponse(boolean success, String message, UserDTO user) {
    }
}
