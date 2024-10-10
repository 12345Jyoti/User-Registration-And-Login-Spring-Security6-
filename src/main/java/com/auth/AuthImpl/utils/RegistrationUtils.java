package com.auth.AuthImpl.utils;

import com.auth.AuthImpl.registraion.enums.Medium;

import java.util.List;

public class RegistrationUtils {

    /**
     * Gets the primary active option from the list.
     * If the list is empty or null, throws an IllegalArgumentException.
     *
     * @param activeOptionList the list of active options
     * @return the first option from the list
     */
    public static Medium getPrimaryActiveOption(List<Medium> activeOptionList) {
        if (activeOptionList == null || activeOptionList.isEmpty()) {
            throw new IllegalArgumentException("Active options list cannot be null or empty");
        }
        return activeOptionList.get(0);  // Return the first option as primary
    }
}

