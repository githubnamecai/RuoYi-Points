package com.ruoyi.common.utils.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import org.springframework.core.env.Environment;

/**
 * 敏感字段加密/解密与检索哈希工具。
 *
 * 说明：
 * - 加密：AES-GCM（随机 IV），结果以 ENC: 前缀 + Base64(IV + CipherText) 形式存储；
 * - 解密：仅对 ENC: 前缀的数据解密，兼容历史明文（不带前缀直接原样返回）；
 * - 检索：通过 SHA-256(规范化明文) 生成 hex，用于等值查询与唯一约束（不依赖确定性加密）。
 */
public class SensitiveCryptoUtils
{
    private static final String ENC_PREFIX = "ENC:";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    /**
     * 判断字符串是否为加密格式（ENC: 前缀）。
     */
    public static boolean isEncrypted(String value)
    {
        return StringUtils.isNotEmpty(value) && value.startsWith(ENC_PREFIX);
    }

    /**
     * 加密敏感字段（AES-GCM）。
     */
    public static String encrypt(String plainText)
    {
        if (StringUtils.isEmpty(plainText)) return plainText;
        if (isEncrypted(plainText)) return plainText;
        try
        {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey key = new SecretKeySpec(getAesKeyBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] payload = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(encrypted, 0, payload, iv.length, encrypted.length);
            return ENC_PREFIX + Base64.getEncoder().encodeToString(payload);
        }
        catch (Exception e)
        {
            throw new IllegalStateException("敏感字段加密失败", e);
        }
    }

    /**
     * 解密敏感字段（AES-GCM）。
     */
    public static String decrypt(String cipherText)
    {
        if (StringUtils.isEmpty(cipherText)) return cipherText;
        if (!isEncrypted(cipherText)) return cipherText;
        try
        {
            byte[] payload = Base64.getDecoder().decode(cipherText.substring(ENC_PREFIX.length()));
            if (payload.length <= GCM_IV_LENGTH) return cipherText;

            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[payload.length - GCM_IV_LENGTH];
            System.arraycopy(payload, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(payload, GCM_IV_LENGTH, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey key = new SecretKeySpec(getAesKeyBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] plain = cipher.doFinal(encrypted);
            return new String(plain, StandardCharsets.UTF_8);
        }
        catch (Exception e)
        {
            throw new IllegalStateException("敏感字段解密失败", e);
        }
    }

    /**
     * 生成用于等值检索/唯一约束的 SHA-256 Hex。
     */
    public static String sha256Hex(String input)
    {
        if (StringUtils.isEmpty(input)) return null;
        String normalized = input.trim();
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(normalized.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest)
            {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        catch (Exception e)
        {
            throw new IllegalStateException("生成 SHA-256 失败", e);
        }
    }

    /**
     * 获取 AES Key（32字节）。
     *
     * 优先读取配置 ruoyi.crypto.aesKeyBase64；
     * 若未配置，则使用 token.secret 派生 SHA-256 作为 Key（可用但不推荐，改动 secret 会导致历史数据不可解密）。
     */
    private static byte[] getAesKeyBytes()
    {
        String aesKeyBase64 = getProperty("ruoyi.crypto.aesKeyBase64");
        if (StringUtils.isNotEmpty(aesKeyBase64))
        {
            byte[] decoded = Base64.getDecoder().decode(aesKeyBase64);
            if (decoded.length == 16 || decoded.length == 24 || decoded.length == 32)
            {
                if (decoded.length == 32) return decoded;
                byte[] padded = new byte[32];
                System.arraycopy(decoded, 0, padded, 0, decoded.length);
                return padded;
            }
        }

        String tokenSecret = getProperty("token.secret");
        if (StringUtils.isEmpty(tokenSecret))
        {
            throw new IllegalStateException("缺少 ruoyi.crypto.aesKeyBase64 或 token.secret，无法初始化加密 Key");
        }

        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(tokenSecret.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e)
        {
            throw new IllegalStateException("派生 AES Key 失败", e);
        }
    }

    private static String getProperty(String key)
    {
        try
        {
            Environment env = SpringUtils.getBean(Environment.class);
            return env.getProperty(key);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}

