package com.blueberryjoy.novel.mail;

import com.blueberryjoy.novel.exception.ApiException;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class Mailtrap {
    private final OkHttpClient client;
    @Value("${MAILTRAP_TOKEN}")
    private String token;

    public Mailtrap() {
        this.client = new OkHttpClient().newBuilder().build();
    }

    public void sendEmail(String from, String to, String subject, String text, String category) {
        MediaType mediaType = MediaType.parse("application/json");
        EmailRequest emailRequest = new EmailRequest(new From(from, "Novels"), List.of(new To(to)), subject, text, category);
        RequestBody body = RequestBody.create(new ObjectMapper().writeValueAsString(emailRequest), mediaType);
        Request request = new Request.Builder().url("https://send.api.mailtrap.io/api/send").method("POST", body).addHeader("Authorization", "Bearer " + token).addHeader("Content-Type", "application/json").build();
        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            MailResponse mailResponse = mapper.readValue(json, MailResponse.class);
            if (!mailResponse.success)
                throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record EmailRequest(From from, List<To> to, String subject, String text, String category) {
    }

    record From(String email, String name) {
    }

    record To(String email) {
    }

    record MailResponse(boolean success, String[] message_ids, String[] errors) {
    }
}
