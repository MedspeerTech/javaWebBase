package com.piotics.custom.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.piotics.common.utils.UtilityManager;
import com.piotics.model.SignUpUser;

public class ObjectValidator implements ConstraintValidator<ObjectValidation, SignUpUser> {

	@Autowired
	UtilityManager utilityManager;

	@Override
	public void initialize(ObjectValidation constraintAnnotation) {

//		this.message = constraintAnnotation.message();
//		this.password =constraintAnnotation.password();
//		this.username = constraintAnnotation.username();
	}

	@Override
	public boolean isValid(SignUpUser object, ConstraintValidatorContext context) {

		return(object.isValid());
	}

}

