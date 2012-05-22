package org.more.webui.context._;
public class UserInfo {
    private String acc = "default_acc";
    private String pwd = "default_pwd";
    public String getAcc() {
        return acc;
    }
    public void setAcc(String acc) {
        this.acc = acc;
    }
    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public void login(){};
}