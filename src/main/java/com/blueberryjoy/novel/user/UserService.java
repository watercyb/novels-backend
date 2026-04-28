package com.blueberryjoy.novel.user;

import com.blueberryjoy.novel.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
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
            throw new ApiException("User with email " + email + " already exists", HttpStatus.BAD_REQUEST);
        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setNovelCount(0);
        user.setChapterCount(0);
        user.setWordCount(0);
        user.setPasswordHash(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public void updatePassword(User user, String password) {
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void updateAvatar(User user, String avatarUrl) {
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
    }
}
