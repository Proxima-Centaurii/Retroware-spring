package dev.alphacentaurii.RETROWARE.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

import dev.alphacentaurii.RETROWARE.enums.UserRegistrationStatus;
import dev.alphacentaurii.RETROWARE.model.Game;
import dev.alphacentaurii.RETROWARE.model.UserProfile;
import dev.alphacentaurii.RETROWARE.service.UserAccountsService;
import dev.alphacentaurii.RETROWARE.service.WebsiteContentService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserController {

    private final UserAccountsService userAccounts;
    private final WebsiteContentService webContent;

    @Autowired
    public UserController(WebsiteContentService webContent ,UserAccountsService userAccounts){
        this.webContent = webContent;
        this.userAccounts = userAccounts;
    }
    
    @GetMapping("/login")
    public String loginPage(Model model, @RequestParam (value = "failed", required = false) Integer failed, Principal principal){

        if(failed == null)
            return "login";

        if(principal != null)
            return "redirect:/home";

        log.debug("Login failed...");
        model.addAttribute("login_fail",  failed != 0);

        return "login";
    }

    @GetMapping("/login-success")
    public String loginSuccessful(){
        return "redirect:/home";
    }

    @GetMapping("/register")
    public String registerPage(Principal principal){
        if(principal != null)
            return "redirect:/home";

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(   Model model,
                                @RequestParam(value = "username", required = true) String username,
                                @RequestParam(value = "password", required = true) String password,
                                @RequestParam(value = "confirm_password", required = true) String confirm_password){
        
        //System.out.printf("Username: %s\nPassword: %s\n%s", username, password, password.equals(confirm_password)? "Password confirmed!\n" : "Passowrd miss-match!\n");

        UserRegistrationStatus outcome = userAccounts.registerUser(username, password, confirm_password);

        log.debug(String.format("\nUsername: %s\nPassword: %s\nRe-typed password: %s\nOutcome: %s", username, password, confirm_password, outcome.message));

        if(outcome != UserRegistrationStatus.OK){
            model.addAttribute("fail_message", outcome.message);

            return "register";
        }

        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logoutRedirect(){
        return "redirect:/home";
    }

    @GetMapping("/profile")
    public String userPage(Model model, Principal principal,@RequestParam(value="u", required = false) String username){

        // User page can't be accessed if user is not logged in and username param is not specified
        // User name is allowed to be blank in order to load the currently logged in user's page
        if(StringUtils.isBlank(username))
            if(principal == null)
                return "redirect:/home";
            else
                username = principal.getName();

        UserProfile profile = userAccounts.getUserProfile(username);

        if(profile == null)
            return "redirect:/home";

        model.addAttribute("profile", profile);

        boolean profile_owner = false;
        if(principal != null && principal.getName().equals(username))
            profile_owner = true;
        
        model.addAttribute("profile_owner", profile_owner);

        List<Game> liked_games = webContent.getUserLikedGames(username);
        model.addAttribute("game_list", liked_games);

        return "user_profile";
    }


}//End of class
