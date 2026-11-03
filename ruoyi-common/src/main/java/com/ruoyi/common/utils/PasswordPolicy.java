package com.ruoyi.common.utils;

import java.util.regex.Pattern;

/**
 * 密码复杂度策略
 *
 * 统一 H5 注册、H5 修改密码、后台重置密码等场景的密码强度校验口径，避免各处规则漂移。
 *
 * 规则：长度不少于 8 位，且同时包含小写字母、大写字母和数字。
 *
 * @author ruoyi
 */
public class PasswordPolicy
{
    /** 密码最小长度 */
    public static final int MIN_LENGTH = 8;

    private static final Pattern LOWER_PATTERN = Pattern.compile("[a-z]");

    private static final Pattern UPPER_PATTERN = Pattern.compile("[A-Z]");

    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");

    private PasswordPolicy()
    {
    }

    /**
     * 校验密码是否满足复杂度要求
     *
     * @param password 明文密码
     * @return 校验通过返回 null，否则返回失败原因
     */
    public static String check(String password)
    {
        if (StringUtils.isEmpty(password))
        {
            return "密码不能为空";
        }
        if (password.length() < MIN_LENGTH)
        {
            return "密码长度至少" + MIN_LENGTH + "位";
        }
        if (!LOWER_PATTERN.matcher(password).find()
                || !UPPER_PATTERN.matcher(password).find()
                || !DIGIT_PATTERN.matcher(password).find())
        {
            return "密码需包含大写字母、小写字母和数字";
        }
        return null;
    }

    /**
     * 判断密码是否满足复杂度要求
     *
     * @param password 明文密码
     * @return 满足返回 true
     */
    public static boolean isComplex(String password)
    {
        return check(password) == null;
    }
}
