package com.auth.AuthImpl.registraion.dtos.response;

public class OtpVerificationResponseDto {

    private boolean success; // Indicates if the OTP verification was successful
    private String message; // Message regarding the verification status
    private String userName; // The ID of the user who attempted the verification
    private String jwtToken; // The JWT token if the verification is successful

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}

