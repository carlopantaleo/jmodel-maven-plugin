package com.carlopantaleo.mojos;

import com.carlopantaleo.exceptions.ValidationException;

public abstract class GenerateJavaCodeMojo extends JModelMojo {
    protected void validateDestinationPackage(String destinationPackage) throws ValidationException {
        if (destinationPackage == null) {
            throw new ValidationException("'destination-package' is mandatory.");
        }
        String pattern = "^(?!\\.)[a-z.]*[a-z]$";
        if (!destinationPackage.matches(pattern)) {
            throw new ValidationException("destination-package", pattern);
        }
    }
}
