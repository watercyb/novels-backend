package com.blueberryjoy.novel.user;

import com.blueberryjoy.novel.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User createUser(String name, String email, String password, String role) {
        User existingUser = userRepository.findByEmail(email).orElse(null);
        if (existingUser != null)
            throw new ApiException("EMAIL_EXISTS", HttpStatus.BAD_REQUEST);
        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setNovelCount(0);
        user.setChapterCount(0);
        user.setWordCount(0);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(User.Role.valueOf(role));
        return userRepository.save(user);
    }

    @Transactional
    public void updatePassword(User user, String password) {
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
    }
}
