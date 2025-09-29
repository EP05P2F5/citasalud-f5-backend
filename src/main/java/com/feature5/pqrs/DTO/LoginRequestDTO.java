package com.feature5.pqrs.DTO;


public class LoginRequestDTO {
    private String nickname;
    private String password;

    // Getters y setters


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

