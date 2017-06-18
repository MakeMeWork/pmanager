package com.itransition.pmanager.controller;

import com.itransition.pmanager.model.*;
import com.itransition.pmanager.security.CustomUserDetails;
import com.itransition.pmanager.security.CustomUserDetailsService;
import com.itransition.pmanager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

/**
 * Created by Lenovo on 06.06.2017.
 */
@PropertySource("classpath:application.properties")
@Controller
public class MainController {
    @Value("${hostname}")
    private String host;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private SignupValidator signupValidator;
    @Autowired
    private EmailService emailService;
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private NewsService newsService;
    @Autowired
    private TagService tagService;


    @RequestMapping(value = {"/", "/home"})
    public String HomePage(Principal user, Model model){
        if(Objects.nonNull(user)){
            getAtrributes(user, model);
        }else
            model.addAttribute("projects", projectService.findFiveProjects());
        List<News> news = newsService.findAllsorted();
        int i = news.size() > 5 ? 5 : news.size();
        model.addAttribute("news", news.subList(0, i));
        model.addAttribute("tags", tagService.findSevenTags());
        return "home";
    }

    private void getAtrributes(Principal user, Model model) {
        model.addAttribute("me", userService.findOne(user.getName()));
        if(userService.findOne(user.getName()).getRole().getLabel().equals("Developer")){
            model.addAttribute("projects", projectService.findFiveProjects(userService.findOne(user.getName()).getProjects()));
        }else{
            model.addAttribute("projects", projectService.findFiveProjects());
        }
    }


    @PreAuthorize("!isAuthenticated()")
    @RequestMapping("/login")
    public String getLogin(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        model.addAttribute("error", error != null);
        model.addAttribute("logout", logout != null);
        return "login";
    }
    @PreAuthorize("!isAuthenticated()")
    @RequestMapping(value = "/registration",method = RequestMethod.GET)
    public String registration(Model model){
        model.addAttribute("user",new User());
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public  String registration(User user, BindingResult bindingResult, Model model){
        signupValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()){
            return "registration";
        }
        verificationService.save(new Verification(user.getUsername(), ""+user.getPassword().hashCode()));
        emailService.sendEmail(user.getEmail(),"http://"+host+"/activate/"+user.getPassword().hashCode());
        userService.save(user);
        autorize(user);
        return "redirect:/home";
    }

    @GetMapping("/activate/{link}")
    public String activate(@PathVariable String link, Model model,Principal me) {
        Verification verification = verificationService.findOne(link);
        if(Objects.nonNull(me)){
            model.addAttribute("me", userService.findOne(me.getName()));
        }
        if(verification == null) {
            return "redirect:error";
        }
        User user= userService.findOne(verification.getUser());
        user.setVerify(true);
        userService.save(user);
        return "activate";
    }

    private void autorize(User user) {
        CustomUserDetails authUser = (CustomUserDetails)customUserDetailsService.loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authUser, user.getPassword(), authUser.getAuthorities());
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String Admin(@RequestParam(name = "set",required = false)String set,
                        @RequestParam(name = "newsparam", required = false)String newsparam,
                        @RequestParam(name = "usrs", required = false) String usrs,
                        @RequestParam(name = "prjs", required = false) String prjs,
                        Principal user,
                        Model model){
        if(Objects.nonNull(user))
            model.addAttribute("me", userService.findOne(user.getName()));
        if(Objects.nonNull(set)) {
            model.addAttribute("settings", settingsService.findAll());
            model.addAttribute("newset", new Settings());
        }
        if(Objects.nonNull(newsparam))
            model.addAttribute("news", new News());
        if(Objects.nonNull(usrs))
            model.addAttribute("users", userService.findAll());
        if(Objects.nonNull(prjs))
            model.addAttribute("projects", projectService.findAll());
        return "admin";
    }

    @PostMapping("/news/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addNews(News news){
        newsService.save(news);
        return "redirect:/home";
    }
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addSett(Settings newset){
        settingsService.save(newset);
        return "redirect:admin?set=set";
    }

    @GetMapping("/news")
    public String getNewsPage(Principal user, Model model){
        if(Objects.nonNull(user)){
            getAtrributes(user, model);
        }else
            model.addAttribute("projects", projectService.findFiveProjects());
        return "news";
    }

    @GetMapping("/newsload")
    public
    @ResponseBody
    String getNews(@RequestParam(name = "type", required = false) String type,
                   @RequestParam(name = "count", required = false) Integer count){
        List<News> newsList = newsService.findAllsorted();
        if(Objects.nonNull(type)){
            return TemplateNews(newsList.subList(0,3 > newsList.size()? newsList.size() : 3),3);
        }
        if(Objects.nonNull(count)){
            int start = count > newsList.size() ? newsList.size() : count;
            int last = start + 3 > newsList.size() ? newsList.size() : start + 3;
            return TemplateNews(newsList.subList(start,last),last);
        }
        return "";
    }

    private String TemplateNews(List<News> newsList, int count) {
        String news = "";
        for (News onenews: newsList
                ) {
            news+="<div class=\"panel-body\">\n" +
                    "                    <h3>"+onenews.getName()+"</h3>\n" +
                    "                    <p style=\"font-size: 14px;\">"+onenews.getContent()+"</p>\n" +
                    "                    <a class=\"pull-right\" href=\""+(onenews.getLink()==null ? "" : onenews.getLink())+"\">More</a>\n" +
                    "                    <span>"+onenews.getDate()+"</span>"+
                    "                    <hr/>\n" +
                    "                </div>\n";
        }
        return news+"<div class='next'><a href=\"\\newsload?count="+ count +"\"'>next</a></div>\n";
    }

    @GetMapping(value = "/403")
    public String accessDenied(Model model, Principal user) {

        if (Objects.nonNull(user)) {
            model.addAttribute("msg", "Hi " + user.getName()
                    + ", you do not have permission to access this page!");
        } else {
            model.addAttribute("msg",
                    "You do not have permission to access this page!");
        }
        return "403";
    }
}
