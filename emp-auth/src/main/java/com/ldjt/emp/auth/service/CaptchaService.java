package com.ldjt.emp.auth.service;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.ldjt.emp.framework.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 *
 * @author emp
 */
@Service
@Slf4j
public class CaptchaService {

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @Autowired
    private RedisCache redisCache;

    private static final String CAPTCHA_CODE_KEY = "captcha:code:";
    private static final long CAPTCHA_EXPIRATION = 2; // 2分钟过期

    /**
     * 生成验证码
     */
    public CaptchaVO generateCaptcha() {
        // 生成验证码文本
        String code = defaultKaptcha.createText();

        // 生成UUID作为key
        String uuid = UUID.randomUUID().toString();

        // 存储到Redis
        redisCache.set(CAPTCHA_CODE_KEY + uuid, code, CAPTCHA_EXPIRATION, TimeUnit.MINUTES);

        // 生成图片
        BufferedImage image = defaultKaptcha.createImage(code);

        // 转换为Base64
        String base64Image = convertToBase64(image);

        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setUuid(uuid);
        captchaVO.setImg(base64Image);

        return captchaVO;
    }

    /**
     * 验证验证码
     */
    public boolean validateCaptcha(String uuid, String code) {
        if (uuid == null || code == null) {
            return false;
        }

        String cacheCode = redisCache.get(CAPTCHA_CODE_KEY + uuid);

        // 验证后删除
        redisCache.delete(CAPTCHA_CODE_KEY + uuid);

        return code.equalsIgnoreCase(cacheCode);
    }

    /**
     * 将图片转换为Base64
     */
    private String convertToBase64(BufferedImage image) {
        try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", os);
            byte[] imageBytes = os.toByteArray();
            // 拼接 MIME 前缀 + Base64 编码
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("验证码图片转换失败", e);
            return "";
        }
    }

    /**
     * 验证码VO
     */
    public static class CaptchaVO {
        private String uuid;
        private String img;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }
}
