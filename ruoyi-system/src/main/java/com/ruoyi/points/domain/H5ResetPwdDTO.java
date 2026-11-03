package com.ruoyi.points.domain;

/**
 * H5 用户自助修改密码参数
 *
 * @author ruoyi
 */
public class H5ResetPwdDTO
{
    /** 原密码（明文，仅用于校验） */
    private String oldPassword;

    /** 新密码（明文，服务端加密后落库） */
    private String newPassword;

    public String getOldPassword()
    {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword)
    {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }
}
