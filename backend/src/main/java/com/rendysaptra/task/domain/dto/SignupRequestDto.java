package com.rendysaptra.task.domain.dto;

public class SignupRequestDto {
    private String email;
    private String password;

    public SignupRequestDto() {
    }

    public SignupRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
