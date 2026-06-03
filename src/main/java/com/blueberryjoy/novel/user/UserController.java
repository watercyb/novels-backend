/*
Post /signup JSON: {username, email, password} -> UserDTO
Post /login JSON: {email, password} -> UserDTO
Post /refresh
Post /logout
Post /update (authenticated) form-data: avatarImage(file), username(String) -> UserDTO
Post /update_password (authenticated) form-data: {oldPassword, newPassword}
Post /forgot_password form-data: {email} -> UserDTO
Post /reset_password form-data: {email, code, password}
Get (authenticated) -> UserDTO
*/

package com.blueberryjoy.novel.user;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.file.FileService;
import com.blueberryjoy.novel.mail.MailService;
import com.blueberryjoy.novel.security.JwtUtils;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FileService repository;
    @Autowired
    private MailService mailService;
    @Autowired
    private ResetPasswordCodeStore resetPasswordCodeStore;
    @Value("${BASE_URL}")
    private String url;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, Object> body) {
        try {
            String username = (String) body.get("username");
            String email = (String) body.get("email");
            String password = (String) body.get("password");
            if (username == null || email == null || password == null)
                throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            User user = userService.createUser(username, email, password, "USER");
            ResponseCookie accessCookie = jwtUtils.getAccessCookie(user);
            ResponseCookie refreshCookie = jwtUtils.getRefreshCookie(user);
            ResponseCookie userCookie = jwtUtils.getUserCookie();
            mailService.sendWelcomeMail(email, username);
            return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.SET_COOKIE, accessCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).header(HttpHeaders.SET_COOKIE, userCookie.toString()).body(new UserResponse(true, null, new UserDTO(user)));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        try {
            String email = (String) body.get("email");
            String password = (String) body.get("password");
            if (email == null || password == null)
                throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            User user = userService.findUserByEmail(email);
            if (user == null || !passwordEncoder.matches(password, user.getPasswordHash()))
                throw new ApiException("UNABLE_LOGIN", HttpStatus.UNAUTHORIZED);
            ResponseCookie accessCookie = jwtUtils.getAccessCookie(user);
            ResponseCookie refreshCookie = jwtUtils.getRefreshCookie(user);
            ResponseCookie userCookie = jwtUtils.getUserCookie();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).header(HttpHeaders.SET_COOKIE, userCookie.toString()).body(new UserResponse(true, null, new UserDTO(user)));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
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
            if (token == null) throw new ApiException("NEED_LOGIN", HttpStatus.UNAUTHORIZED);
            User user = jwtUtils.verifyRefreshToken(token);
            if (user == null) throw new ApiException("NEED_LOGIN", HttpStatus.UNAUTHORIZED);
            ResponseCookie accessCookie = jwtUtils.getAccessCookie(user);
            ResponseCookie refreshCookie = jwtUtils.getRefreshCookie(user);
            ResponseCookie userCookie = jwtUtils.getUserCookie();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).header(HttpHeaders.SET_COOKIE, userCookie.toString()).body(new UserResponse(true, null, new UserDTO(user)));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            ResponseCookie accessCookie = jwtUtils.getEmptyAccessCookie();
            ResponseCookie refreshCookie;
            ResponseCookie userCookie = jwtUtils.getEmptyUserCookie();
            if (auth != null && !Objects.equals(auth.getPrincipal(), "anonymousUser")) {
                User user = (User) auth.getPrincipal();
                refreshCookie = jwtUtils.getEmptyRefreshCookie(user);
            } else {
                refreshCookie = jwtUtils.getEmptyRefreshCookie(null);
            }
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).header(HttpHeaders.SET_COOKIE, accessCookie.toString()).header(HttpHeaders.SET_COOKIE, userCookie.toString()).body(new UserResponse(true, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> update(@RequestParam(value = "avatarImage", required = false) MultipartFile avatarImage, @RequestParam(value = "username", required = false) String username) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to update user info right now.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();

            if (avatarImage != null && !avatarImage.isEmpty()) {
                String contentType = avatarImage.getContentType();
                if (contentType == null || !contentType.startsWith("image/"))
                    throw new ApiException("Unsupported file format", HttpStatus.BAD_REQUEST);
                if (avatarImage.getSize() > 5 * 1024 * 1024)
                    throw new ApiException("This file is larger than 5MB. Please upload a smaller version.", HttpStatus.BAD_REQUEST);

                String fileName = repository.uploadAvatar(avatarImage, user.getId(), user.getAvatarUrl());
                if (fileName == null || fileName.isEmpty())
                    throw new ApiException("Unable to update user avatar right now.", HttpStatus.BAD_REQUEST);
                user.setAvatarUrl(fileName);
            }
            if (username != null && !username.isEmpty()) user.setUsername(username);
            userService.updateUser(user);
            return ResponseEntity.ok().body(new UserResponse(true, "Uploaded successfully", new UserDTO(user)));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update_password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> update(@RequestBody Map<String, Object> body) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_UPDATE_PASSWORD", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            String oldPassword = (String) body.get("oldPassword");
            String newPassword = (String) body.get("newPassword");
            if (oldPassword == null || oldPassword.isEmpty() || newPassword == null || newPassword.isEmpty())
                throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            if (!passwordEncoder.matches(oldPassword, user.getPasswordHash()))
                throw new ApiException("UNABLE_UPDATE_PASSWORD", HttpStatus.BAD_REQUEST);
            if (newPassword.equals(oldPassword))
                throw new ApiException("SAME_PASSWORD", HttpStatus.BAD_REQUEST);
            userService.updatePassword(user, newPassword);
            ResponseCookie accessCookie = jwtUtils.getAccessCookie(user);
            ResponseCookie refreshCookie = jwtUtils.getRefreshCookie(user);
            ResponseCookie userCookie = jwtUtils.getUserCookie();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).header(HttpHeaders.SET_COOKIE, userCookie.toString()).body(new UserResponse(true, "Uploaded successfully", null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<?> sendResetCode(@RequestBody Map<String, Object> body) {
        try {
            String email = (String) body.get("email");
            if (email == null || email.isEmpty()) throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            User user = userService.findUserByEmail(email);
            if (user != null) {
                String code = UUID.randomUUID().toString();
                mailService.sendResetPasswordMail(email, user.getUsername(), url + "/reset-password?email=" + email + "&code=" + code);
                resetPasswordCodeStore.set(user.getId(), code);
            }
            return ResponseEntity.ok().body(new UserResponse(true, "If an account exists for this email, you will receive a password reset email shortly.", null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reset_password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, Object> body) {
        try {
            String email = (String) body.get("email");
            String code = (String) body.get("code");
            String password = (String) body.get("password");
            if (email == null || email.isEmpty() || code == null || code.isEmpty() || password == null || password.isEmpty())
                throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            User user = userService.findUserByEmail(email);
            if (user == null || !resetPasswordCodeStore.check(user.getId(), code))
                throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            userService.updatePassword(user, password);
            resetPasswordCodeStore.remove(user.getId());
            ResponseCookie accessCookie = jwtUtils.getAccessCookie(user);
            ResponseCookie refreshCookie = jwtUtils.getRefreshCookie(user);
            ResponseCookie userCookie = jwtUtils.getUserCookie();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).header(HttpHeaders.SET_COOKIE, userCookie.toString()).body(new UserResponse(true, "Password reset successfully", null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to delete chapter right now.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            return ResponseEntity.ok().body(new UserResponse(true, null, new UserDTO(user)));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{authorId}")
    public ResponseEntity<?> getAuthor(@PathVariable Long authorId) {
        try {
            if (authorId == null) throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            UserDTO user = userService.getAuthor(authorId);
            return ResponseEntity.ok().body(new UserResponse(true, null, user));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record UserResponse(boolean success, String message, UserDTO user) {
    }
}
