package com.auth.AuthImpl.registraion.dtos.fields;

import java.util.List;
        public abstract class AbstractDropDownField<X> extends AbstractField<X> {
    private List<X> options;


            public List<X> getOptions() {
        return options;
    }

    public void setOptions(List<X> options) {
        this.options = options;  // Corrected this line
    }
}
