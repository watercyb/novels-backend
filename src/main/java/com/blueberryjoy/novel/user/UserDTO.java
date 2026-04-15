package com.blueberryjoy.novel.user;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
public class UserDTO {
    private final Long id;
    private final String username;
    private final String email;
    private final String avatarUrl;
    private final LocalDateTime createdAt;

    public UserDTO(User user) {
        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        avatarUrl = user.getAvatarUrl();
        createdAt = user.getCreatedAt();
    }
}
