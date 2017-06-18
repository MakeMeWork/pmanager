package com.itransition.pmanager.controller;

import com.itransition.pmanager.model.SignupValidator;
import com.itransition.pmanager.model.User;
import com.itransition.pmanager.service.ProjectService;
import com.itransition.pmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

/**
 * Created by Lenovo on 14.06.2017.
 */
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private SignupValidator signupValidator;
    @Autowired
    private ServletContext servletContext;

    private final UserService userService;

    @Autowired
    private ProjectService projectService;

    @RequestMapping(value = "/img/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> downloadUserAvatarImage(@PathVariable String userId) throws IOException {
        File file = new File(servletContext.getResourceAsStream("/null")+"/"+userId+".jpg");
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
    }


    @GetMapping("/{username}")
    public String getProfile(Model model, @PathVariable String username,Principal user){
        if(Objects.nonNull(user)){
            getAtrributes(user, model);
        }else
            model.addAttribute("projects", projectService.findFiveProjects());
        User fUser = userService.findOne(username);
        if(Objects.nonNull(fUser)){
        model.addAttribute("user", userService.findOne(username));
            return "profile";
        }
        return "redirect:../profile";
    }

    private void getAtrributes(Principal user, Model model) {
        model.addAttribute("me", userService.findOne(user.getName()));
        if(userService.findOne(user.getName()).getRole().getLabel().equals("Developer")){
            model.addAttribute("projects", projectService.findFiveProjects(userService.findOne(user.getName()).getProjects()));
        }else{
            model.addAttribute("projects", projectService.findFiveProjects());
        }
    }
    @GetMapping("/{username}/settings")
    @PreAuthorize("(#username == authentication.name) or hasRole('ROLE_ADMIN')")
    public String setProfile(Model model, @PathVariable("username") String username,Principal me){
        if(Objects.nonNull(me)){
            getAtrributes(me, model);
        }else
            model.addAttribute("projects", projectService.findFiveProjects());
        model.addAttribute("user", userService.findOne(username));
        return "setProfile";
    }

    @PostMapping("/{username}/settings")
    @PreAuthorize("(#username == authentication.name) or hasRole('ROLE_ADMIN')")
    public String acceptSettings(User user,String newpass , BindingResult bindingResult, Model model, @PathVariable String username){
        signupValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()&(user.getPassword().equals(userService.findOne(username).getPassword()))){
            model.addAttribute("me", userService.findOne(username));
            return "setProfile";
        }
        userService.update(userService.findOne(username),user);
        return "redirect:/profile/"+username;
    }

    @PostMapping("/{username}/newimg")
    @PreAuthorize("(#username == authentication.name) or hasRole('ROLE_ADMIN')")
    public String addImage(@PathVariable("username") String username,
                                         User user,
                                         BindingResult bindingResult,
                                         MultipartFile file,
                                         HttpSession session,
                                         Model model) {
        User newuser = userService.findOne(username);
        newuser.setAvatar(newuser.getId() + ".jpg");
        userService.save(newuser);

        try {
            if(!file.isEmpty()) {
                validateImage(file); // Проверить изображение
                saveImage(newuser.getId() + ".jpg", file); // Сохранить файл
            }
            return "redirect:/profile/"+username;
        } catch (Exception e) {
            bindingResult.reject(e.getMessage());
            model.addAttribute("me", user);
            return "setProfile";
        }
    }

    private void validateImage(MultipartFile image) throws Exception {
        if(!image.getContentType().equals("image/jpeg")) {
            throw new Exception("Only JPG images accepted");
        }
    }
    private void saveImage(String filename, MultipartFile image)
            throws Exception {
        try {
            File file = new File(servletContext.getResourceAsStream("/null")+"/"+filename);
            FileUtils.writeByteArrayToFile(file, image.getBytes());
        } catch(IOException e) {
            throw new Exception(e);
        }
    }

    @GetMapping
    public String getUsers(Principal user, Model model){
        if(Objects.nonNull(user)){
            model.addAttribute("me", userService.findOne(user.getName()));
        }
        return "usersList";
    }
    @GetMapping("/load")
    public
    @ResponseBody
    String getNews(@RequestParam(name = "type", required = false) String type,
                   @RequestParam(name = "count", required = false) Integer count){
        List<User> users = userService.findAll();
        if(Objects.nonNull(type)){
            return TemplateUsers(users.subList(0,4 > users.size()? users.size() : 4),4);
        }
        if(Objects.nonNull(count)){
            int start = count > users.size() ? users.size() : count;
            int last = start + 3 > users.size() ? users.size() : start + 3;
            return TemplateUsers(users.subList(start,last),last);
        }
        return "";
    }

    private String TemplateUsers(List<User> users, int count) {
        String news = "";
        for (User user: users
                ) {
            news+="<div class=\"panel-body\">\n" +
                    "                <div class=\"col-md-4  col-xs-4\">" +
                    "                <img src=\"/profile/img/"+user.getAvatar()+"\" alt=\"avatar\" class=\"img-rounded img-responsive\"/>\n" +
                    "                <p class=\"text-muted\" style=\"font-size: 14px; text-align: center;\">" +user.getRole().getLabel()+ "</p>" +
                    "                </div>"+
                    "                <div class=\"col-md-8 col-xs-8\">\n" +
                    "                <h3> <span>"+user.getFirstName()+"</span> <span>"+user.getLastName()+"</span></h3>\n" +
                    "                <p>"+user.getUsername()+"</p>\n" +
                    "                <p>"+user.getEmail()+"</p>\n" +
                    "                <a href=\"/profile/"+user.getUsername()+"\" class=\"pull-right\">Profile</a>" +
                    "                </div>"+"<hr/>"+
                    "</div>\n";
        }
        return news+"<div class='next'><a href=\"\\profile\\load?count="+ count +"\"'>next</a></div>\n";
    }

}
