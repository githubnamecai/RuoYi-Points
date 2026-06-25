package com.ruoyi.qrcode.domain;

import java.nio.file.Path;

public class UtilDownloadZip {
    private Path path;

    private String name;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UtilDownloadZip{" +
                "path=" + path +
                ", name='" + name + '\'' +
                '}';
    }
}