package com.piotics.custom.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ObjectValidator.class)
@Documented

public @interface ObjectValidation {

	String message() ;//default "sdfsfsdf";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
