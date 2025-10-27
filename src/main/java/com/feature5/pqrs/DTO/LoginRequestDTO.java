package com.feature5.pqrs.DTO;

public class LoginRequestDTO {

    private String nickname;
    private String password;

    // Constructor vacío (necesario para Spring y Jackson)
    public LoginRequestDTO() {
    }

    // Constructor con parámetros (usado en tests y llamadas manuales)
    public LoginRequestDTO(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

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
