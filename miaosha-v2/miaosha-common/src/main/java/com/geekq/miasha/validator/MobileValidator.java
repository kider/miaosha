package com.geekq.miasha.validator;

import com.geekq.miasha.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MobileValidator implements ConstraintValidator<MobileCheck, String> {

    private boolean require = false;

    @Override
    public void initialize(MobileCheck isMobile) {
        require = isMobile.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (require) {
            return ValidatorUtils.isMobile(value);
        } else {
            if (StringUtils.isEmpty(value)) {
                return true;
            } else {
                return ValidatorUtils.isMobile(value);
            }
        }
    }
}
