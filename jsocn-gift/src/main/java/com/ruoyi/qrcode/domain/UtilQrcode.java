package com.ruoyi.qrcode.domain;

public class UtilQrcode {
    private String url;

    private String msg;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "UtilQrcode{" +
                "url='" + url + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
