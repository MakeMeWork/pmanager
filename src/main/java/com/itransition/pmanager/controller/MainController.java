package com.itransition.pmanager.controller;

import com.itransition.pmanager.model.User;
import com.itransition.pmanager.security.CustomUserDetails;
import com.itransition.pmanager.security.CustomUserDetailsService;
import com.itransition.pmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.*;
import java.security.Security;

/**
 * Created by Lenovo on 06.06.2017.
 */
@Controller
public class MainController {

    @Autowired
    private UserService userService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;


    @RequestMapping(value = {"/", "/home"})
    public String HomePage(){
        return "home";
    }

    @RequestMapping("/login")
    public String getLogin(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        model.addAttribute("error", error != null);
        model.addAttribute("logout", logout != null);
        return "login";
    }

    @RequestMapping(value = "/registration",method = RequestMethod.GET)
    public String registration(Model model){
        model.addAttribute("user",new User());
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public  String registration(@ModelAttribute("user") User user, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()){
            return "registration";
        }
        userService.update(user);
//        CustomUserDetails authUser = (CustomUserDetails)customUserDetailsService.loadUserByUsername(user.getUsername());
//        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authUser, user.getPassword(), authUser.getAuthorities());
//        authenticationManager.authenticate(usernamePasswordAuthenticationToken);
//        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
//            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//        }
        return "redirect:/home";
    }
}
