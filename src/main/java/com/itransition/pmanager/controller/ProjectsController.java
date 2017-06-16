package com.itransition.pmanager.controller;

import com.itransition.pmanager.model.Project;
import com.itransition.pmanager.model.ProjectValidator;
import com.itransition.pmanager.service.ProjectService;
import com.itransition.pmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Objects;

/**
 * Created by Lenovo on 12.06.2017.
 */
@Controller
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectsController {
    private final ProjectService projectService;

    @Autowired
    private ProjectValidator projectValidator;

    @Autowired
    private UserService userService;

    @GetMapping("/{projectId}")
    public String getProject(@PathVariable Long projectId, Model model, Principal user) {
        if(Objects.nonNull(user)){
            model.addAttribute("me", userService.findOne(user.getName()));
        }
        Project project = projectService.findOne(projectId);
        if(project == null)
            return "redirect:/create";
        model.addAttribute("project", project);
        return "project";
    }


    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public String createProject(Model model,Principal user){
        if(Objects.nonNull(user)){
            model.addAttribute("me", userService.findOne(user.getName()));
        }
        model.addAttribute("project", new Project());
        return "addproject";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public String addProject(Project project, BindingResult bindingResult, Model model){
        projectValidator.validate(project, bindingResult);
        if (bindingResult.hasErrors()){
            return "addproject";
        }
        projectService.Save(project);
        return "redirect:/project/"+project.getId();
    }

}
