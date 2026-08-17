/*
Get / RequestParam: token(String) -> users(int), novels(int), messages(int), mails(int)
*/

package com.blueberryjoy.novel.analytics;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.mail.MailCount;
import com.blueberryjoy.novel.message.MessageRepository;
import com.blueberryjoy.novel.novel.NovelRepository;
import com.blueberryjoy.novel.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MailCount mailCount;
    @Value("${ANALYTICS_TOKEN}")
    private String token;

    @GetMapping("")
    public ResponseEntity<?> getPublishedChapterById(@RequestParam(required = false) String token) {
        try {
            if (token == null || !token.equals(this.token)) return ResponseEntity.status(HttpStatus.OK).build();
            int users = (int) userRepository.count();
            int novels = (int) novelRepository.count();
            int messages = (int) messageRepository.count();
            int mails = mailCount.getCount();
            return ResponseEntity.status(HttpStatus.OK).body(new AnalyticsResponse(true, users, novels, messages, mails));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record AnalyticsResponse(boolean success, int users, int novels, int messages, int mails) {
    }
}
