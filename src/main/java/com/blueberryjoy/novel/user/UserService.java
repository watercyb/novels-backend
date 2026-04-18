package com.blueberryjoy.novel.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    public User createUser(String name, String email, String password) {
        User existingUser = userRepository.findByEmail(email).orElse(null);
        if (existingUser != null)
            throw new DuplicateKeyException("User with email " + email + " already exists");
        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setNovels(0);
        user.setChapters(0);
        user.setWords(0);
        user.setPasswordHash(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }
}
