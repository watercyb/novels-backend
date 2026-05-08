package com.blueberryjoy.novel.user;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class ResetPasswordCodeStore {
    private final ConcurrentHashMap<Long, Code> store = new ConcurrentHashMap<>();

    public void set(Long userId, String code) {
        long expireAt = System.currentTimeMillis() + 10 * 60 * 1000;
        store.put(userId, new Code(code, expireAt));
    }

    public boolean check(Long userId, String code) {
        Code entry = store.get(userId);
        if (entry == null) return false;
        if (entry.expireAt < System.currentTimeMillis()) {
            store.remove(userId);
            return false;
        }
        return entry.code.equals(code);
    }

    public void remove(Long userId) {
        store.remove(userId);
    }

    record Code(String code, long expireAt) {
    }
}
