package com.itransition.pmanager.model;

import com.itransition.pmanager.service.UserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by Lenovo on 13.06.2017.
 */
@Component
public class SignupValidator implements Validator {
    @Autowired
    private UserService userService;


    public boolean supports(Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
        User user = (User) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.empty", "Username must not be empty.");
        String username = user.getUsername();
        if (userService.findOne(username)!=null) {
            errors.rejectValue("username", "username.unique", "This username already exist.");
        }

        if( !EmailValidator.getInstance().isValid( user.getEmail() ) ){
            errors.rejectValue("email", "email.notValid", "Email address is not valid.");
        }


    }
}
