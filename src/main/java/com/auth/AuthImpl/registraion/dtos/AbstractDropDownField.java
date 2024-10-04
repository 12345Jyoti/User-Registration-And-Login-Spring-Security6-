package com.auth.AuthImpl.registraion.dtos;


import java.util.List;

public abstract class AbstractDropDownField<X> extends AbstractField<X> {
    private List<X> options;

    // Getters and Setters
    public List<X> getOptions() {
        return options;
    }

    public void setOptions(List<X> options) {
        this.options = options;
    }
}
