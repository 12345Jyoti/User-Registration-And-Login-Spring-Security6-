package com.auth.AuthImpl.registraion.repo;

import com.auth.AuthImpl.registraion.entity.OtpVerification;
import com.auth.AuthImpl.registraion.entity.Users;
import com.auth.AuthImpl.registraion.enums.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    @Query("SELECT ov FROM OtpVerification ov JOIN FETCH ov.user u WHERE u.username = :username AND ov.otp = :otp AND ov.type = :otpType")
    OtpVerification findByUser_UsernameAndOtpAndType(@Param("username") String username, @Param("otp") String otp, @Param("otpType") OtpType otpType);

    Optional<OtpVerification> findByUserAndOtp(Users user, String otp);

    List<OtpVerification> findByUserAndIsUsedFalse(Users user);
}

