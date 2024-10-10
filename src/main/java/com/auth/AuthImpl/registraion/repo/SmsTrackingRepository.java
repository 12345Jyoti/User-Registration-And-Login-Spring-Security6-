package com.auth.AuthImpl.registraion.repo;

import com.auth.AuthImpl.registraion.entity.SmsTracking;
import com.auth.AuthImpl.registraion.entity.OtpVerification;
import com.auth.AuthImpl.registraion.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SmsTrackingRepository extends JpaRepository<SmsTracking, Long> {

    Optional<SmsTracking>findByOtpVerification(OtpVerification otpVerification);

//    Optional<SmsTracking> findByUserAndOtpVerification(Users user, OtpVerification otpVerification);

    // You can define custom queries here if needed in the future
}
