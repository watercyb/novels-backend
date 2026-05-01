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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class FileService {
    @Value("${REPO_API_URL}")
    private String repoApiUrl;
    @Value("${REPO_URL}")
    private String repoUrl;
    @Value("${REPO_TOKEN}")
    private String token;
    private final int avatarSize = 256;

    public String uploadChapter(Novel novel, long userId, String contentUrl, String content, boolean isRemove) {
        try {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(repoApiUrl + "chapter.php");
                MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                        .addTextBody("token", token)
                        .addTextBody("user_id", String.valueOf(userId))
                        .addTextBody("novel_id", String.valueOf(novel.getId()))
                        .addTextBody("content", content)
                        .addTextBody("remove", String.valueOf(isRemove));

                if (contentUrl != null) {
                    String[] strs = contentUrl.split("/");
                    if (strs.length > 0)
                        builder.addTextBody("filename", strs[strs.length - 1]);
                }

                org.apache.http.HttpEntity entity = builder.build();

                post.setEntity(entity);

                try (CloseableHttpResponse response = client.execute(post)) {
                    String json = EntityUtils.toString(response.getEntity());
                    ObjectMapper mapper = new ObjectMapper();
                    UploadResponse res = mapper.readValue(json, UploadResponse.class);
                    if (!res.success) throw new ApiException(res.message, HttpStatus.INTERNAL_SERVER_ERROR);
                    return repoUrl + res.filename;
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
                HttpPost post = new HttpPost(repoApiUrl + "avatar.php");

                byte[] bytes = formatImage(file, true);

                org.apache.http.HttpEntity entity = MultipartEntityBuilder.create()
                        .addBinaryBody("image", bytes, ContentType.DEFAULT_BINARY, "avatar.jpg")
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

    public String uploadNovelCover(MultipartFile file, long userId, long novelId) {
        try {
            if (file == null || file.isEmpty())
                throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(repoApiUrl + "cover.php");

                byte[] bytes = formatImage(file, false);

                org.apache.http.HttpEntity entity = MultipartEntityBuilder.create()
                        .addBinaryBody("image", bytes, ContentType.DEFAULT_BINARY, "cover.jpg")
                        .addTextBody("token", token)
                        .addTextBody("user_id", String.valueOf(userId))
                        .addTextBody("novel_id", String.valueOf(novelId))
                        .build();

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

    public void deleteNovelCover(Novel novel, long userId) {
        try {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(repoApiUrl + "cover.php");

                String url = novel.getCoverUrl();
                if (url == null) return;

                org.apache.http.HttpEntity entity = MultipartEntityBuilder.create()
                        .addTextBody("token", token)
                        .addTextBody("user_id", String.valueOf(userId))
                        .addTextBody("novel_id", String.valueOf(novel.getId()))
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

    private byte[] formatImage(MultipartFile file, boolean resize) {
        try {
            BufferedImage original = ImageIO.read(file.getInputStream());
            if (original == null)
                throw new ApiException("Invalid image file", HttpStatus.BAD_REQUEST);

            BufferedImage image = cropToSquare(original);
            if (resize)
                image = resize(image, avatarSize, avatarSize);

            BufferedImage rgbImage = new BufferedImage(
                    original.getWidth(),
                    original.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            rgbImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

            ByteArrayOutputStream arr = new ByteArrayOutputStream();
            boolean success = ImageIO.write(rgbImage, "jpeg", arr);
            return arr.toByteArray();
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private BufferedImage cropToSquare(BufferedImage img) {
        int size = Math.min(img.getWidth(), img.getHeight());
        int x = (img.getWidth() - size) / 2;
        int y = (img.getHeight() - size) / 2;

        return img.getSubimage(x, y, size, size);
    }

    private BufferedImage resize(BufferedImage img, int width, int height) {
        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = output.createGraphics();
        g2d.drawImage(scaled, 0, 0, null);
        g2d.dispose();

        return output;
    }

    public record UploadResponse(boolean success, String message, String filename) {
    }
}