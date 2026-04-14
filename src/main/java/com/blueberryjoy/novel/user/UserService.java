package com.blueberryjoy.novel.user;

import com.blueberryjoy.novel.exception.models.BadRequestException;
import com.blueberryjoy.novel.security.JwtResponse;
import com.blueberryjoy.novel.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;


    public UserService(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User createUser(String name, String email, String password) {
        User existingUser = userRepository.findByEmail(email).orElse(null);
        if (existingUser != null)
            throw new DuplicateKeyException("User with email " + email + " already exists");
        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public JwtResponse signup(Map<String, Object> body) {
        String username = (String) body.get("username");
        String email = (String) body.get("email");
        String password = (String) body.get("password");
        if (username == null || email == null || password == null) {
            throw new BadRequestException("Please complete all required fields to proceed.");
        }
        User user = createUser(username, email, password);
        String token = jwtUtils.generateToken(user.getEmail());
        return new JwtResponse(token);
    }
}
