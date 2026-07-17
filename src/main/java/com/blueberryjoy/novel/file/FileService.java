package com.blueberryjoy.novel.file;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.novel.Novel;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
    private final int coverWidth = 896;
    private final int coverHeight = 1280;

    public String uploadChapter(Novel novel, long userId, String contentUrl, String content, boolean isRemove) {
        try {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(repoApiUrl + "chapter.php");
                MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                        .addTextBody("token", token)
                        .addTextBody("user_id", String.valueOf(userId))
                        .addTextBody("novel_id", String.valueOf(novel.getId()))
                        .addTextBody("content", content, ContentType.create("text/plain", "UTF-8"))
                        .addTextBody("remove", String.valueOf(isRemove));

                String oldFilename = getFilename(contentUrl);
                if (oldFilename != null)
                    builder.addTextBody("old_filename", oldFilename);

                org.apache.http.HttpEntity entity = builder.build();

                post.setEntity(entity);

                try (CloseableHttpResponse response = client.execute(post)) {
                    String json = EntityUtils.toString(response.getEntity());
                    ObjectMapper mapper = new ObjectMapper();
                    UploadResponse res = mapper.readValue(json, UploadResponse.class);
                    if (!res.success) throw new ApiException(res.message, HttpStatus.INTERNAL_SERVER_ERROR);
                    return repoApiUrl + "files/" + res.filename;
                } catch (ParseException e) {
                    throw new ApiException("UNABLE_UPDATE_FILE", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String getChapter(String contentUrl) {
        try {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                if (contentUrl != null && contentUrl.startsWith(repoApiUrl)) {
                    HttpGet get = new HttpGet(contentUrl);
                    try (CloseableHttpResponse response = client.execute(get)) {
                        return EntityUtils.toString(response.getEntity(), "UTF-8");
                    } catch (ParseException e) {
                        throw new ApiException("UNABLE_GET_FILE", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    throw new ApiException("UNABLE_GET_FILE", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String uploadAvatar(MultipartFile file, long userId, String avatarUrl) {
        try {
            if (file == null || file.isEmpty())
                throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(repoApiUrl + "avatar.php");

                byte[] bytes = formatImage(file, 1);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                        .addBinaryBody("image", bytes, ContentType.DEFAULT_BINARY, "avatar.jpg")
                        .addTextBody("token", token).addTextBody("user_id", String.valueOf(userId));
                String oldFilename = getFilename(avatarUrl);
                if (oldFilename != null) builder.addTextBody("old_filename", oldFilename);
                org.apache.http.HttpEntity entity = builder.build();

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
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String uploadNovelCover(MultipartFile file, long userId, long novelId, String coverUrl) {
        try {
            if (file == null || file.isEmpty())
                throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(repoApiUrl + "cover.php");

                byte[] bytes = formatImage(file, 2);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                        .addBinaryBody("image", bytes, ContentType.DEFAULT_BINARY, "cover.jpg")
                        .addTextBody("token", token)
                        .addTextBody("user_id", String.valueOf(userId))
                        .addTextBody("novel_id", String.valueOf(novelId));
                String oldFilename = getFilename(coverUrl);
                if (oldFilename != null) builder.addTextBody("old_filename", oldFilename);
                org.apache.http.HttpEntity entity = builder.build();

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
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteNovelCover(Novel novel, long userId) {
        try {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(repoApiUrl + "cover.php");

                String oldFilename = getFilename(novel.getCoverUrl());
                if (oldFilename == null) return;

                org.apache.http.HttpEntity entity = MultipartEntityBuilder.create()
                        .addTextBody("token", token)
                        .addTextBody("user_id", String.valueOf(userId))
                        .addTextBody("novel_id", String.valueOf(novel.getId()))
                        .addTextBody("old_filename", oldFilename)
                        .addTextBody("remove", "true")
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
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private byte[] formatImage(MultipartFile file, int type) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null)
                throw new ApiException("Invalid image file", HttpStatus.BAD_REQUEST);

            //BufferedImage image = cropToSquare(original);
            if (type == 1) {
                image = resizeAvatar(image);
            } else {
                image = resizeCover(image);
            }

            BufferedImage rgbImage = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            rgbImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

            ByteArrayOutputStream arr = new ByteArrayOutputStream();
            ImageIO.write(rgbImage, "jpeg", arr);
            return arr.toByteArray();
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private BufferedImage cropToSquare(BufferedImage img) {
        int size = Math.min(img.getWidth(), img.getHeight());
        int x = (img.getWidth() - size) / 2;
        int y = (img.getHeight() - size) / 2;

        return img.getSubimage(x, y, size, size);
    }

    private BufferedImage resizeCover(BufferedImage img) {
        int width = coverWidth;
        int height = coverHeight;
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = output.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2d.drawImage(img, 0, 0, width, height, null);

        g2d.dispose();
        return output;
    }

    private BufferedImage resizeAvatar(BufferedImage img) {
        int limit = avatarSize;
        int originalWidth = img.getWidth();
        int originalHeight = img.getHeight();

        // No resize needed
        if (originalWidth <= limit && originalHeight <= limit) {
            return img;
        }

        double scale = Math.min(
                (double) limit / originalWidth,
                (double) limit / originalHeight
        );

        int newWidth = (int) Math.round(originalWidth * scale);
        int newHeight = (int) Math.round(originalHeight * scale);

        BufferedImage output = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = output.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(img, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return output;
    }

    public String getFilename(String url) {
        if (url != null) {
            String[] strs = url.split("/");
            if (strs.length > 1)
                return strs[strs.length - 1];
        }
        return null;
    }

    public record UploadResponse(boolean success, String message, String filename) {
    }
}