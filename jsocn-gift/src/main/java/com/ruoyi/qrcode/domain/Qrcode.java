package com.ruoyi.qrcode.domain;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ruoyi.common.config.RuoYiConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Qrcode {

    /**
     * 生成二维码
     * @param utilQrcode
     * @return
     * @throws IOException
     */
    public static String createQrcode(UtilQrcode utilQrcode) {
        // 创建编码内容
        String qrData = utilQrcode.getUrl();

        // 设置二维码参数
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M); // 设置纠错级别

        // 创建二维码写入器
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // 指定保存路径和文件名
        UUID uuid = UUID.randomUUID();
        String fileName = "qrcode_"+ uuid;

        File file =new File(RuoYiConfig.getUploadPath() + "/qrcode/");
        //如果文件夹不存在则创建
        if  (!file.exists() && !file.isDirectory())
        {
            file.mkdir();
        }
        Path filePath = Paths.get(RuoYiConfig.getUploadPath() + "/qrcode/"+fileName+".png");
        String returnPath = "/profile/upload/qrcode/"+fileName+".png";

        try {
            // 生成二维码位矩阵
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 600, 600, hints);

            // 将位矩阵写入到PNG文件中
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        // 二维码添加文字信息
        if (utilQrcode.getMsg() != null) {
            try {
                // 加载已生成的二维码图片
                BufferedImage image = ImageIO.read(new File(RuoYiConfig.getUploadPath() + "/qrcode/"+fileName+".png"));
                // 预估文字高度，以便给文字预留空间
                int textHeightFirst = 0;
                if (utilQrcode.getMsg() != null ) {
                    textHeightFirst = getTextHeight(utilQrcode.getMsg());
                }
                // 确定新画布的高度
                int newCanvasHeight = image.getHeight() + textHeightFirst;
                // 创建新的空白图像（画布）
                BufferedImage newImage = new BufferedImage(image.getWidth(), newCanvasHeight, BufferedImage.TYPE_INT_ARGB);

                // 创建Graphics2D对象
                Graphics2D graphics = newImage.createGraphics();
                // 设置新画布背景为白色
                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
                // 将原二维码图片绘制到新画布的顶部
                graphics.drawImage(image, 0, 0, null);

                // 设置字体、颜色等
                Font font = new Font("simhei", Font.BOLD, 28);
                graphics.setColor(Color.BLACK);
                graphics.setFont(font);

                // 计算并设置文字的位置
                int startX = 20; // 文字起始点的横向偏移
                int startY = newImage.getHeight(); // 文字起始点的纵向偏移
                int lineHeight = font.getSize(); // 每行文字的高度

                String[] tempLine = utilQrcode.getMsg().split("\n"); // 将字符串按换行符分割成数组
                String[] lines = new String[tempLine.length];

                // 调整行的顺序
                for (int i = 0; i < tempLine.length; i++) {
                    lines[i] = tempLine[tempLine.length - 1 - i];
                }

                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    FontMetrics metrics = graphics.getFontMetrics(font);
                    int textWidth = metrics.stringWidth(line); // 当前行文字的宽度
                    int textHeight = metrics.getHeight(); // 当前行文字的高度

                    // 计算当前行的中心位置
                    int centerX = newImage.getWidth() / 2;
                    int centerY = startY - i * lineHeight - textHeight / 2; // 从底部减去每行的高度

                    // 绘制文字
                    graphics.drawString(line, centerX - textWidth / 2, centerY);
                }

                // 清理资源
                graphics.dispose();
                // 写回图像
                ImageIO.write(newImage, "PNG", new File(RuoYiConfig.getUploadPath() + "/qrcode/"+fileName+".png"));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return returnPath;
    }


    private static int getTextHeight(String text) {
        // 设置字体
        Font font = new Font("simhei", Font.BOLD, 28);

        // 分割字符串为多行
        String[] lines = text.split("\n");

        // 初始化总高度
        int totalHeight = 0;

        // 遍历每一行文字
        for (String line : lines) {
            // 获取FontMetrics以测量单行文字的高度
            FontRenderContext frc = new FontRenderContext(null, true, true);
            TextLayout tl = new TextLayout(line, font, frc);
            Rectangle2D bounds = tl.getBounds();

            // 更新总高度
            totalHeight += (int) Math.ceil(bounds.getHeight());
        }

        // 返回所有行文字的总高度
        return totalHeight;
    }
}
