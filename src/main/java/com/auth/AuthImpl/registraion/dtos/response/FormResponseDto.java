package com.auth.AuthImpl.registraion.dtos.response;
import com.auth.AuthImpl.registraion.dtos.fields.AbstractField;
import com.auth.AuthImpl.registraion.enums.Medium;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FormResponseDto {

    private List<Medium> options;
    @JsonProperty("fields")
    private List<AbstractField<?>> fields; // List of abstract fields (email, mobile, etc.)
    private String message;

    public FormResponseDto(){}

    public FormResponseDto(List<Medium> options,List<AbstractField<?>> fields, String message) {
        this.options=options;
        this.fields = fields;
        this.message = message;
    }

    public  List<Medium> getOptions(){
        return options;
    }
    public List<AbstractField<?>> getFields() {
        return fields;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
    }

    public void setOptions(List<Medium> activeOptionList) {
    }

    public void setFields(List<AbstractField<String>> isdField) {
    }


//
//    private List<String> options;
//    private Mobile mobile;
//    private Email email;
////    private List<AbstractField> fields;          //jackson annotation
//    private String message;
//
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
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public FormResponseDto(List<String> options, Mobile mobile, Email email, String message) {
//        this.options = options;
//        this.mobile = mobile;
//        this.email = email;
//        this.message = message;
//    }
//
//    public FormResponseDto() {}
}
