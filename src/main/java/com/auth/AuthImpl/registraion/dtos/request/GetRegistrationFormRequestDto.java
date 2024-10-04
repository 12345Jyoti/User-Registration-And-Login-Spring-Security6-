package com.auth.AuthImpl.registraion.dtos.request;

import com.auth.AuthImpl.registraion.enums.Medium;

public class GetRegistrationFormRequestDto {
        private Medium medium;  // Sign-up medium (Enum)
        private MobileRequest mobile;       // Mobile sign-up details
        private EmailRequest email;         // Email sign-up details

        // Getters and Setters
        public Medium getMedium() {
            return medium;
        }

        public void setMedium(Medium medium) {
            this.medium = medium;
        }

        public MobileRequest getMobile() {
            return mobile;
        }

        public void setMobile(MobileRequest mobile) {
            this.mobile = mobile;
        }

        public EmailRequest getEmail() {
            return email;
        }

        public void setEmail(EmailRequest email) {
            this.email = email;
        }

    }

