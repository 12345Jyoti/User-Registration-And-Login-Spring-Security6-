////package com.auth.AuthImpl.registraion.dtos.request;
////
////
////import jakarta.validation.constraints.NotBlank;
////
////public class OtpVerificationRequestDto {
////
////    @NotBlank(message = "User ID is mandatory")
////    private String userName;
////
////    @NotBlank(message = "OTP is mandatory")
////    private String otp;
////
////    // Getters and Setters
////    public String getUserName() {
////        return userName;
////    }
////
////    public void setUserName(String userName) {
////        this.userName = userName;
////    }
////
////    public String getOtp() {
////        return otp;
////    }
////
////    public void setOtp(String otp) {
////        this.otp = otp;
////    }
////}
////
//package com.auth.AuthImpl.registraion.dtos.request;
//
//import com.auth.AuthImpl.registraion.enums.OtpType;
//import jakarta.validation.constraints.NotBlank;
//
//public class OtpVerificationRequestDto {
//    @NotBlank(message = "User ID is mandatory")
//    private String userName;
//
//    @NotBlank(message = "OTP is mandatory")
//    private String otp;
//
//    @NotBlank(message = "OTP Type is mandatory")
//    private OtpType otpType; // This should be of type OtpType
//
//    // Getters and Setters
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getOtp() {
//        return otp;
//    }
//
//    public void setOtp(String otp) {
//        this.otp = otp;
//    }
//
//    public OtpType getOtpType() {
//        return otpType;
//    }
//
//    public void setOtpType(OtpType otpType) {
//        this.otpType = otpType;
//    }
//
//    @Override
//    public String toString() {
//        return "OtpVerificationRequestDto{" +
//                "userName='" + userName + '\'' +
//                ", otp='" + otp + '\'' +
//                ", otpType=" + otpType +
//                '}';
//    }
//}
