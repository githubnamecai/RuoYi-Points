package com.ruoyi.framework.config;

import com.google.code.kaptcha.GimpyEngine;

import java.awt.image.BufferedImage;

/**
 * 无扭曲验证码滤镜：直接返回原图，不做任何拉伸、偏移、阴影变形
 */
public class PlainGimpy implements GimpyEngine {
    @Override
    public BufferedImage getDistortedImage(BufferedImage baseImage) {
        // 原图原样输出，无任何形变
        return baseImage;
    }
}