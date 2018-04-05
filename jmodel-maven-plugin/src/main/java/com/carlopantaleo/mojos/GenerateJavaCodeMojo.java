package com.carlopantaleo.mojos;

import com.carlopantaleo.exceptions.ValidationException;

public abstract class GenerateJavaCodeMojo extends JModelMojo {
    protected void validatePackage(String thePackage, String propertyName) throws ValidationException {
        if (thePackage == null) {
            throw new ValidationException(String.format("'%s' is mandatory.", propertyName));
        }
        String pattern = "^(?!\\.)[a-z.]*[a-z]$";
        if (!thePackage.matches(pattern)) {
            throw new ValidationException(propertyName, pattern);
        }
    }
}
