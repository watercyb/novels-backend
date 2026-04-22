package com.blueberryjoy.novel.file;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.novel.Novel;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

@Service
public class FileService {
    @Value("${REPO_URL}")
    private String repoUrl;
    @Value("${REPO_TOKEN}")
    private String token;

    public String uploadChapter(long novelId, String content) {
        try {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(repoUrl + "chapter.php");
                org.apache.http.HttpEntity entity = MultipartEntityBuilder.create()
                        .addTextBody("token", token)
                        .addTextBody("novel_id", String.valueOf(novelId))
                        .addTextBody("content", content)
                        .build();

                post.setEntity(entity);

                try (CloseableHttpResponse response = client.execute(post)) {
                    String json = EntityUtils.toString(response.getEntity());
                    ObjectMapper mapper = new ObjectMapper();
                    UploadResponse res = mapper.readValue(json, UploadResponse.class);
                    if (!res.success) throw new ApiException(res.message, HttpStatus.INTERNAL_SERVER_ERROR);
                    return res.filename;
                } catch (ParseException e) {
                    throw new ApiException("Cannot upload file", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String uploadChapter(long novelId, String filename, String content) {
        try {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(repoUrl + "chapter.php");
                org.apache.http.HttpEntity entity = MultipartEntityBuilder.create()
                        .addTextBody("token", token)
                        .addTextBody("novel_id", String.valueOf(novelId))
                        .addTextBody("filename", filename)
                        .addTextBody("content", content)
                        .build();

                post.setEntity(entity);

                try (CloseableHttpResponse response = client.execute(post)) {
                    String json = EntityUtils.toString(response.getEntity());
                    ObjectMapper mapper = new ObjectMapper();
                    UploadResponse res = mapper.readValue(json, UploadResponse.class);
                    if (!res.success) throw new ApiException(res.message, HttpStatus.INTERNAL_SERVER_ERROR);
                    return res.filename;
                } catch (ParseException e) {
                    throw new ApiException("Cannot update file", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String uploadAvatar(MultipartFile file, long userId) {
        try {
            if (file == null || file.isEmpty())
                throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(repoUrl + "avatar.php");

                byte[] bytes = file.getBytes();

                org.apache.http.HttpEntity entity = MultipartEntityBuilder.create()
                        .addBinaryBody("image", bytes, ContentType.DEFAULT_BINARY, file.getOriginalFilename())
                        .addTextBody("token", token).addTextBody("user_id", String.valueOf(userId)).build();

                post.setEntity(entity);

                try (CloseableHttpResponse response = client.execute(post)) {
                    String json = EntityUtils.toString(response.getEntity());
                    ObjectMapper mapper = new ObjectMapper();
                    UploadResponse res = mapper.readValue(json, UploadResponse.class);
                    if (!res.success) throw new ApiException(res.message, HttpStatus.INTERNAL_SERVER_ERROR);
                    return repoUrl + res.filename;
                } catch (ParseException e) {
                    throw new ApiException("Cannot upload image", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String uploadNovelCover(MultipartFile file, long novelId) {
        try {
            if (file == null || file.isEmpty())
                throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(repoUrl + "image.php");

                byte[] bytes = file.getBytes();

                org.apache.http.HttpEntity entity = MultipartEntityBuilder.create()
                        .addBinaryBody("image", bytes, ContentType.DEFAULT_BINARY, file.getOriginalFilename())
                        .addTextBody("token", token).addTextBody("novel_id", String.valueOf(novelId)).build();

                post.setEntity(entity);

                try (CloseableHttpResponse response = client.execute(post)) {
                    String json = EntityUtils.toString(response.getEntity());
                    ObjectMapper mapper = new ObjectMapper();
                    UploadResponse res = mapper.readValue(json, UploadResponse.class);
                    if (!res.success) throw new ApiException(res.message, HttpStatus.INTERNAL_SERVER_ERROR);
                    return repoUrl + res.filename;
                } catch (ParseException e) {
                    throw new ApiException("Cannot upload image", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteNovelCover(Novel novel) {
        try {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(repoUrl + "image.php");

                String url = novel.getCoverUrl();
                if (url == null) return;
                String[] strs = url.split("\\.");
                if (strs.length <= 1) return;
                String ext = "." + strs[strs.length - 1];

                org.apache.http.HttpEntity entity = MultipartEntityBuilder.create()
                        .addTextBody("token", token)
                        .addTextBody("novel_id", String.valueOf(novel.getId()))
                        .addTextBody("ext", ext)
                        .build();

                post.setEntity(entity);

                try (CloseableHttpResponse response = client.execute(post)) {
                    String json = EntityUtils.toString(response.getEntity());
                    ObjectMapper mapper = new ObjectMapper();
                    UploadResponse res = mapper.readValue(json, UploadResponse.class);
                    if (!res.success) throw new ApiException(res.message, HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (ParseException e) {
                    throw new ApiException("Cannot delete image", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public record UploadResponse(boolean success, String message, String filename) {
    }
}