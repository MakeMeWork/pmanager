package com.itransition.pmanager.controller;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.itransition.pmanager.model.*;
import com.itransition.pmanager.service.*;
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
import java.util.List;
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

    @Autowired
    private NewsService newsService;

    @GetMapping("/{name}")
    public String getProject(@PathVariable String name, Model model, Principal user) {
        if(Objects.nonNull(user)){
            getAtrributes(user, model);
            model.addAttribute("permit", permit(userService.findOne(user.getName()), projectService.findOne(name)));
        }else {
            model.addAttribute("projects", projectService.findFiveProjects());
            model.addAttribute("permit", false);
        }

        Project project = projectService.findOne(name);
        if(project == null)
            return "redirect:/create";
        model.addAttribute("project", project);
        return "project";
    }

    private void getAtrributes(Principal user, Model model) {
        model.addAttribute("me", userService.findOne(user.getName()));
        if(userService.findOne(user.getName()).getRole().getLabel().equals("Developer")){
            model.addAttribute("projects", projectService.findFiveProjects(userService.findOne(user.getName()).getProjects()));
        }else{
            model.addAttribute("projects", projectService.findFiveProjects());
        }
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
        createNews(project, user);
        return "redirect:/project/"+project.getName();
    }

    private void createNews(Project project, Principal user) {
        News news = new News();
        news.setName("Created new project! "+project.getName());
        news.setContent(user.getName()+"created a new project. Developers get ready for the new job. for more information click on the link.");
        news.setLink("/project/"+project.getName());
        newsService.save(news);
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
    @PostMapping("/{name}/newtag")
    public String addTag(@PathVariable("name") String name,
                         @RequestParam(name = "tag") String tag,
                         Model model){
        Project project = projectService.findOne(name);
        project.getTags().addAll(tagService.Save(tag.split(" "),project));
        projectService.Save(project);
        return "redirect:/project/"+name;
    }

    @GetMapping("/{name}/open")
    public String openProject(@PathVariable("name") String name,
                               Model model){
        Project project = projectService.findOne(name);
        if(Objects.nonNull(project)){
            project.setState(State.Open);
            projectService.Save(project);
            openProjectNews(project);
        }
        return "redirect:/project/"+name;
    }

    private void openProjectNews(Project project) {
        News news = new News();
        news.setName("Project "+project.getName()+" is open again!");
        news.setContent("Project " + project.getName() + " is open again. Everyone back to work. More information at the link." );
        news.setLink("/project/"+project.getName());
    }

    @PostMapping("/{name}/archive")
    public String arhiveProject(@PathVariable("name") String name,
                         Model model){
        Project project = projectService.findOne(name);
        if(Objects.nonNull(project)){
            project.setState(State.Archive);
            projectService.Save(project);
            moveToArchiveNews(project);
        }
        return "redirect:/project/"+name;
    }

    private void moveToArchiveNews(Project project) {
        News news = new News();
        news.setName("Project "+project.getName()+" moved to archive!");
        news.setContent("Project " + project.getName() + " was moved to archive. Thank you all for your work! View the project at the link.");
        news.setLink("/project/"+project.getName());
    }
    @GetMapping
    public String getProjects(Principal user, Model model){
        if(Objects.nonNull(user)){
            model.addAttribute("me", userService.findOne(user.getName()));
        }
        return "projectList";
    }
    @GetMapping("/load")
    public
    @ResponseBody
    String getNews(@RequestParam(name = "type", required = false) String type,
                   @RequestParam(name = "count", required = false) Integer count){
        List<Project> projects = projectService.findAll();
        if(Objects.nonNull(type)){
            return TemplateNews(projects.subList(0,10 > projects.size()? projects.size() : 10),10);
        }
        if(Objects.nonNull(count)){
            int start = count > projects.size() ? projects.size() : count;
            int last = start + 3 > projects.size() ? projects.size() : start + 3;
            return TemplateNews(projects.subList(start,last),last);
        }
        return "";
    }

    private String TemplateNews(List<Project> projects, int count) {
        String news = "";
        for (Project project: projects
                ) {
            news+="<div class=\"panel-body\">\n" +
                    "                    <h3>"+project.getName()+"</h3>\n" +
                    "                    <p style=\"font-size: 14px;height: 150px;text-overflow: ellipsis;\">"+project.getDescription()+"</p>\n" +
                    "                    <a class=\"pull-right\" href=\"\\project\\"+project.getName()+"\">More</a>\n" +
                    "                    <hr/>\n" +
                    "                </div>\n";
        }
        return news+"<div class='next'><a href=\"\\project\\load?count="+ count +"\"'>next</a></div>\n";
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

    @PostMapping("/wiki")
    public
    @ResponseBody
    String getWiki(@RequestParam(name = "name")String name){
        return projectService.findOne(name).getWiki();
    }
    @PostMapping("/swiki")
    public
    @ResponseBody
    String saveWiki(@RequestParam(name = "wiki") String wiki,
                             @RequestParam(name = "name")String name){
        Project project = projectService.findOne(name);
        project.setWiki(wiki);
        projectService.Save(project);
        return project.wikiCompile();
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
