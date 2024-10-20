package com.jabiseo.api.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValueOfEnumValidator implements ConstraintValidator<EnumValid, String> {

    private EnumValid enumValid;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null){
            return false;
        }
        Enum<?>[] enumValues = this.enumValid.enumClass().getEnumConstants();
        if (enumValues == null) {
            return false;
        }
        for (Object enumValue : enumValues) {
            if (value.equals(enumValue.toString())
                    || this.enumValid.ignoreCase() && value.equalsIgnoreCase(enumValue.toString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initialize(EnumValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.enumValid = constraintAnnotation;
    }
}
