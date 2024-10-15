package ru.dikun.prototype.responses;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;

    private long expiresIn; 

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }

}
