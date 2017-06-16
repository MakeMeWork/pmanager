package com.itransition.pmanager.controller;

import com.itransition.pmanager.model.SignupValidator;
import com.itransition.pmanager.model.User;
import com.itransition.pmanager.model.Verification;
import com.itransition.pmanager.security.CustomUserDetails;
import com.itransition.pmanager.security.CustomUserDetailsService;
import com.itransition.pmanager.service.EmailService;
import com.itransition.pmanager.service.SettingsService;
import com.itransition.pmanager.service.UserService;
import com.itransition.pmanager.service.VerificationService;
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

    @RequestMapping(value = {"/", "/home"})
    public String HomePage(Principal user, Model model){
        if(Objects.nonNull(user)){
            model.addAttribute("me", userService.findOne(user.getName()));
        }
        return "home";
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
    @GetMapping("/error")
    public
    @ResponseBody
    String getError(){
        return "You make something mistake, turn back and never do that!";
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
    public
    @ResponseBody
    String Admin(){

        return "succes";
    }
}
