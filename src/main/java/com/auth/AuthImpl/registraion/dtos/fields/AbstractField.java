package com.auth.AuthImpl.registraion.dtos.fields;

public abstract class AbstractField<T> {
    private String inputType;
    private String validationType;
    private boolean mandatory;
    private String placeHolder;
    private T userValue;

    // Getters and Setters
    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getValidationType() {
        return validationType;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public T getUserValue() {
        return userValue;
    }

    public void setUserValue(T userValue) {
        this.userValue = userValue;
    }
}

