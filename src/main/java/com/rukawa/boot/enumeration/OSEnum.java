package com.rukawa.boot.enumeration;

import java.util.Arrays;
import java.util.Optional;

public enum OSEnum {

    WINDOWS("windows"),
    LINUX("linux");

    private String osName;

    OSEnum(String osName) {
        this.osName = osName;
    }

    public static OSEnum getOSEnum(String osName) {
        Optional<OSEnum> osEnumOptional = Arrays.stream(values())
                .filter(value -> osName.toLowerCase()
                        .contains(value.osName.toLowerCase()))
                .findFirst();
        if (osEnumOptional.isPresent()) {
            return osEnumOptional.get();
        }
        return WINDOWS;
    }
}
