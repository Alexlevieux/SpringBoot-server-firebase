package com.progresee.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class WelcomeController{

   @GetMapping("/welcome")
   public String welcome() {
   
   return "welcome";
   }
   @PostMapping("/welcome")
   public String welcomeLoggedIn() {
   
   return "welcome";
   }

}
