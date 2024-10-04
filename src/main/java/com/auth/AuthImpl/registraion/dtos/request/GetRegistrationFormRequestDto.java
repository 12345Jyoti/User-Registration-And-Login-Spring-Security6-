package com.auth.AuthImpl.registraion.dtos.request;

import com.auth.AuthImpl.registraion.dtos.Email;
import com.auth.AuthImpl.registraion.dtos.Mobile;
import com.auth.AuthImpl.registraion.enums.Medium;

public class GetRegistrationFormRequestDto {
        private Medium medium;  // Sign-up medium (Enum)
        private Mobile mobile;       // Mobile sign-up details
        private Email email;         // Email sign-up details

        // Getters and Setters
        public Medium getMedium() {
            return medium;
        }

        public void setMedium(Medium medium) {
            this.medium = medium;
        }

        public Mobile getMobile() {
            return mobile;
        }

        public void setMobile(Mobile mobile) {
            this.mobile = mobile;
        }

        public Email getEmail() {
            return email;
        }

        public void setEmail(Email email) {
            this.email = email;
        }

    }

