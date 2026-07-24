package com.blueberryjoy.novel.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("""
                SELECT u
                FROM User u
                WHERE u.username LIKE CONCAT(:keyword, '%')
                ORDER BY u.username
            """)
    Page<User> searchByUsernamePrefix(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
                SELECT u
                FROM User u
                ORDER BY u.username
            """)
    Page<User> getAll(Pageable pageable);

    @Query("""
                SELECT u
                FROM User u
                WHERE u.username LIKE CONCAT(:keyword, '%') and u.role=:role
                ORDER BY u.username
            """)
    Page<User> searchByUsernamePrefixRole(@Param("keyword") String keyword, @Param("role") User.Role role, Pageable pageable);

    @Query("""
                SELECT u
                FROM User u
                WHERE u.role=:role
                ORDER BY u.username
            """)
    Page<User> getAllByRole(@Param("role") User.Role role, Pageable pageable);
}