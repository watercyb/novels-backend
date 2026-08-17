package com.blueberryjoy.novel.mail;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MailCount {

    private final AtomicInteger count = new AtomicInteger(0);

    public int getCount() {
        return count.get();
    }

    public void increment() {
        count.incrementAndGet();
    }
}
