package com.itransition.pmanager.controller;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.itransition.pmanager.model.Project;
import com.itransition.pmanager.model.ProjectValidator;
import com.itransition.pmanager.model.Source;
import com.itransition.pmanager.model.User;
import com.itransition.pmanager.service.ProjectService;
import com.itransition.pmanager.service.SourceService;
import com.itransition.pmanager.service.TagService;
import com.itransition.pmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.Principal;
import java.util.Objects;
import java.util.UUID;

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

    @Autowired
    private SourceService sourceService;

    @Autowired
    private TagService tagService;

    @GetMapping("/{name}")
    public String getProject(@PathVariable String name, Model model, Principal user) {
        if(Objects.nonNull(user)){
            model.addAttribute("me", userService.findOne(user.getName()));
            model.addAttribute("permit", permit(userService.findOne(user.getName()), projectService.findOne(name)));
        }
        else{
            model.addAttribute("permit", false);
        }
        Project project = projectService.findOne(name);
        if(project == null)
            return "redirect:/create";
        model.addAttribute("project", project);
        return "project";
    }


    @PostMapping("/{name}")
    public String fileUpload(@PathVariable("name")String name,
                             @RequestParam("file")MultipartFile file){
        AmazonS3Client amazonS3Client = new AmazonS3Client(new ProfileCredentialsProvider());

        String bucketName = "source" + UUID.randomUUID();
        amazonS3Client.createBucket(bucketName);

        try{
            InputStream is = file.getInputStream();

            amazonS3Client.putObject(new PutObjectRequest(bucketName, file.getName(), is, new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead));

            S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucketName, file.getName()));
            Source source = saveSorce(name, file, s3Object);

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return "redirect:/project/"+name;
    }

    private Source saveSorce(String name, MultipartFile file, S3Object s3Object) {
        Source source = new Source(file.getName(),s3Object.getObjectContent().getHttpRequest().getURI().toString() ,projectService.findOne(name));
        sourceService.Save(source);
        return source;
    }



    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public String createProject(Model model,Principal user){
        if(Objects.nonNull(user)){
            model.addAttribute("me", userService.findOne(user.getName()));
        }
        model.addAttribute("project", new Project());
        model.addAttribute("tages", "");
        return "addproject";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public String addProject(Project project,String tages, BindingResult bindingResult, Model model,Principal user){
        projectValidator.validate(project, bindingResult);
        if (bindingResult.hasErrors()){
            model.addAttribute("me", userService.findOne(user.getName()));
            model.addAttribute("project", new Project());
            return "addproject";
        }
        projectService.Save(project);
        project.setTags(tagService.Save(tages.split(" "),project));
        projectService.Save(project);
        return "redirect:/project/"+project.getName();
    }

    @GetMapping("/{name}/delete")
    public String delete(@PathVariable("name") String name,
                         @RequestParam(required = false, name = "src") String src,
                         @RequestParam(required = false, name = "dev") String dev,
                         @RequestParam(required = false, name = "tag") String tag){
        Project project = projectService.findOne(name);
        if(Objects.nonNull(src)){
            project.getSources().remove(sourceService.findOne(src));
        }
        if(Objects.nonNull(dev)){
            project.getUsers().remove(userService.findOne(dev));
        }
        if(Objects.nonNull(tag)){
            project.getTags().remove(tagService.findOne(tag));
        }
        projectService.Save(project);
        return "redirect:/project/"+name;
    }

    @PostMapping("/{name}/add")
    public String addDev(@PathVariable("name") String name,
                         @RequestParam(name = "username") String username,
                         Model model){
        User user = userService.findOne(username);
        if(Objects.nonNull(user)){
            return addToProject(name, user);
        }
        model.addAttribute("msg", "User not found!");
        return "project";
    }
    @PostMapping("/change")
    public
    @ResponseBody
    String changeDescription(@RequestParam(name = "content") String content,
                             @RequestParam(name = "name")String name){
        Project project = projectService.findOne(name);
        project.setDescription(content);
        projectService.Save(project);
        return "1";
    }

    private String addToProject(@PathVariable("name") String name, User user) {
        if(user.getRole().getLabel().equals("Manager")){return "project";}
        Project project = projectService.findOne(name);
        project.getUsers().add(user);
        user.getProjects().add(project);
        userService.save(user);
        projectService.Save(project);
        return "redirect:/project/"+name;
    }

    private boolean permit(User user, Project project){
        try {
            return (user.getProjects().contains(project));
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
