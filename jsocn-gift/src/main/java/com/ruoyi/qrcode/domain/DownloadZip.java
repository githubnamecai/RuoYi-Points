package com.ruoyi.qrcode.domain;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownloadZip {
    public static ResponseEntity<byte[]> downloadImagesAsZip(List<UtilDownloadZip> filePaths, String zipName, HttpServletResponse response)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (UtilDownloadZip imagePath : filePaths) {
                if (Files.exists(imagePath.getPath())) {
                    // 新的文件名，例如：原文件名为"image1.jpg"，现在可以改为"new_image1.jpg"
                    String newImageNameInZip = imagePath.getName();

                    // 创建ZipEntry时使用新的文件名
                    ZipEntry zipEntry = new ZipEntry(newImageNameInZip);
                    zos.putNextEntry(zipEntry);

                    try (FileInputStream fis = new FileInputStream(imagePath.getPath().toFile())) {
                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = fis.read(bytes)) >= 0) {
                            zos.write(bytes, 0, length);
                        }
                    }

                    zos.closeEntry();
                } else {
                    System.out.println("Warning: Image not found at path: " + imagePath);
                }
            }

        } catch (IOException e) {
            System.err.println("Error creating or writing to the ZIP file: " + e.getMessage());
        }

        // 设置HTTP响应头，准备下载
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", zipName+".zip");
        headers.setConnection("UTF-8"); // 添加这行

        // 将压缩后的字节数据转换为ResponseEntity
        return ResponseEntity.ok()
                .headers(headers)
                .body(baos.toByteArray());

    }


    public static void downloadImagesAsZipNew(List<UtilDownloadZip> filePaths, String zipName, HttpServletResponse response) {
        // 1. 设置响应头，告诉浏览器这是一个下载文件
        response.reset(); // 重置响应缓冲区
        response.setContentType("application/octet-stream");
        // 建议对文件名进行编码处理，防止中文乱码
        try {
            response.setHeader("Content-Disposition", "attachment; filename=" +
                    java.net.URLEncoder.encode(zipName + ".zip", "UTF-8").replaceAll("\\+", "%20"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. 获取输出流，直接流向客户端，不经过内存大数组
        try (OutputStream os = response.getOutputStream();
             ZipOutputStream zos = new ZipOutputStream(os)) {

            byte[] buffer = new byte[1024]; // 创建一个小缓冲区
            int len;

            for (UtilDownloadZip item : filePaths) {
                if (Files.exists(item.getPath())) {
                    try {
                        // 创建 ZipEntry
                        ZipEntry zipEntry = new ZipEntry(item.getName());
                        zos.putNextEntry(zipEntry);

                        // 3. 流式读取文件并写入 ZIP
                        // 关键点：这里不再使用 byte[] 存储整个文件，而是分块读取写入
                        try (java.io.FileInputStream fis = new java.io.FileInputStream(item.getPath().toFile())) {
                            while ((len = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                            }
                        }

                        zos.closeEntry();
                    } catch (IOException e) {
                        System.err.println("Error writing file to zip: " + item.getName() + ", " + e.getMessage());
                    }
                }
            }
            // 4. 刷新流，确保数据全部发送
            zos.flush();

        } catch (IOException e) {
            System.err.println("Error creating zip stream: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
