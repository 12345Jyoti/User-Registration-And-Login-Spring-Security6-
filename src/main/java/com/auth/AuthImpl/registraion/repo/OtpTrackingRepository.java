package com.auth.AuthImpl.registraion.repo;

import com.auth.AuthImpl.registraion.entity.OtpTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpTrackingRepository extends JpaRepository<OtpTracking, Long> {

    // You can define custom queries here if needed in the future
}
