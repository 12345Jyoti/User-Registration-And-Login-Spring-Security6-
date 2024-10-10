package com.auth.AuthImpl.registraion.form;
import com.auth.AuthImpl.registraion.dtos.fields.AbstractField;
import com.auth.AuthImpl.registraion.enums.FormMedium;

import java.util.HashMap;
import java.util.Map;

public class RegisterForm {            //rename
    private Map<FormMedium, AbstractField> fields;
    private String message;

    private RegisterForm(Builder builder) {
        this.fields = builder.fields;
        this.message = builder.message;
    }

    public Map<FormMedium, AbstractField> getFields() {
        return fields;
    }

    public String getMessage() {
        return message;
    }

    public static class Builder {
        private Map<FormMedium, AbstractField> fields = new HashMap<>();
        private String message;

        //Field enum(
        public Builder withField(FormMedium key, AbstractField field) {
            fields.put(key, field);
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public RegisterForm build() {
            return new RegisterForm(this);
        }
    }
}
