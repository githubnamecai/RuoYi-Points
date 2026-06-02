package com.ruoyi.points.domain;

public class ResetPwdDTO {
    private Long userId;
    private String newPassword;
    // getter / setter

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
