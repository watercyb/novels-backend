package com.blueberryjoy.novel.user;

import com.blueberryjoy.novel.security.JwtResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("name", body.get("username"));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, Object> body) {
        JwtResponse response = userService.signup(body);
        return ResponseEntity.ok(response);
    }

}
