package com.jhjang.photoapp.model;

public class UserReq {
    private String email;
    private String passwd;

    public UserReq() {
    }

    public UserReq(String email, String passwd) {
        this.email = email;
        this.passwd = passwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
