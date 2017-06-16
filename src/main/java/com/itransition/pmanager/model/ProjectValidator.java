package com.itransition.pmanager.model;

import com.itransition.pmanager.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by Lenovo on 13.06.2017.
 */
@Component
public class ProjectValidator implements Validator {
    @Autowired
    private ProjectService projectService;


    public boolean supports(Class<?> clazz) {
        return Project.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
        Project project = (Project) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.empty", "Name must not be empty.");
        String name = project.getName();
        if (projectService.findOne(name)!=null) {
            errors.rejectValue("name", "name.unique", "This name already exist.");
        }
    }
}

