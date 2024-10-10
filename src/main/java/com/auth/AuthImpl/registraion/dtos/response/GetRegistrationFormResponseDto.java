//package com.auth.AuthImpl.registraion.dtos.response;
//
//import com.auth.AuthImpl.registraion.dtos.Email;
//import com.auth.AuthImpl.registraion.dtos.Mobile;
//
//import java.util.List;
//
//public class GetRegistrationFormResponseDto {
//
//    private List<String> options; // e.g., ["mobile", "email", "google", "truecaller"]
//    private Mobile mobile;    // Mobile-specific form structure
//    private Email email;      // Email-specific form structure
//
//    // Getters and Setters
//    public List<String> getOptions() {
//        return options;
//    }
//
//    public void setOptions(List<String> options) {
//        this.options = options;
//    }
//
//    public Mobile getMobile() {
//        return mobile;
//    }
//
//    public void setMobile(Mobile mobile) {
//        this.mobile = mobile;
//    }
//
//    public Email getEmail() {
//        return email;
//    }
//
//    public void setEmail(Email email) {
//        this.email = email;
//    }
//
//    public GetRegistrationFormResponseDto(List<String> options, Mobile mobile, Email email) {
//        this.options = options;
//        this.mobile = mobile;
//        this.email = email;
//    }
//
//    public GetRegistrationFormResponseDto(){}
//    // Nested class for mobile registration form fields
//////    public static class MobileForm {
//////        private Field<String> isdCode;
//////        private Field<String> phoneNumber;
//////
//////        // Getters and Setters
//////        public Field<String> getIsdCode() {
//////            return isdCode;
//////        }
////
////        public void setIsdCode(Field<String> isdCode) {
////            this.isdCode = isdCode;
////        }
////
////        public Field<String> getPhoneNumber() {
////            return phoneNumber;
////        }
////
////        public void setPhoneNumber(Field<String> phoneNumber) {
////            this.phoneNumber = phoneNumber;
////        }
////    }
////
////    // Nested class for email registration form fields
////    public static class EmailForm {
////        private Field<String> email;
////
////        // Getters and Setters
////        public Field<String> getEmail() {
////            return email;
////        }
////
////        public void setEmail(Field<String> email) {
////            this.email = email;
////        }
////    }
////
////    // Generic Field class to represent form fields
////    public static class Field<T> {
////        private String inputType;
////        private List<T> values; // Only used for dropdowns
////        private String validationType;
////        private boolean mandatory;
////        private String placeholder;
////
////        // Getters and Setters
////        public String getInputType() {
////            return inputType;
////        }
////
////        public void setInputType(String inputType) {
////            this.inputType = inputType;
////        }
////
////        public List<T> getValues() {
////            return values;
////        }
////
////        public void setValues(List<T> values) {
////            this.values = values;
////        }
////
////        public String getValidationType() {
////            return validationType;
////        }
////
////        public void setValidationType(String validationType) {
////            this.validationType = validationType;
////        }
////
////        public boolean isMandatory() {
////            return mandatory;
////        }
////
////        public void setMandatory(boolean mandatory) {
////            this.mandatory = mandatory;
////        }
////
////        public String getPlaceholder() {
////            return placeholder;
////        }
////
////        public void setPlaceholder(String placeholder) {
////            this.placeholder = placeholder;
////        }
////    }
//}
//
