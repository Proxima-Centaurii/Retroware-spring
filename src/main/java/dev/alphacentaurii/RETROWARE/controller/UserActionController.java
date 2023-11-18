package dev.alphacentaurii.RETROWARE.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import dev.alphacentaurii.RETROWARE.service.DynamicContentService;
import dev.alphacentaurii.RETROWARE.service.UserAccountsService;
import dev.alphacentaurii.RETROWARE.service.WebsiteContentService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/user")
@RestController
@Slf4j
public class UserActionController {
    
    private final UserAccountsService userAccounts;
    private final WebsiteContentService webContent;
    private final DynamicContentService dynamicContent;

    @Autowired
    public UserActionController(UserAccountsService userAccounts, WebsiteContentService webContent, DynamicContentService dynamicContent){
        this.userAccounts = userAccounts;
        this.webContent = webContent;
        this.dynamicContent = dynamicContent;
    }

    @PostMapping("/update_description")
    public void updateDescription(Principal principal, @RequestBody String new_description){

        if(new_description == null || new_description.length() > UserAccountsService.MAX_USER_DESCRIPTION_LENGTH)
            return;

        userAccounts.updateUserDescription(new_description, principal.getName());
    }

    @PostMapping("/upload_profile_picture")
    public void uploadProfilePicture(Principal principal, HttpServletResponse response, HttpServletRequest request,
                                     @RequestParam(name= "file", required = true) MultipartFile file){
        if(principal == null){
            log.warn("Unautorised request for profile picture upload. Remote address: " + request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try{
            dynamicContent.saveUserProfilePicture(file, principal.getName());
        }catch(IOException e){
            log.error("Failed to upload profile picture for user: " + principal.getName(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

    @GetMapping("/delete_user")
    public void deleteUser(HttpServletResponse response, Principal principal) throws IOException{
        String username = principal.getName();

        userAccounts.deleteUser(username);
        dynamicContent.deleteProfilePicture(username);

        response.sendRedirect("/logout");
    }

    @PostMapping("/rate_game")
    public void addGameRating(Principal principal, @RequestBody Map<String,Object> body){   
        if(principal == null)
            return;

        Integer id = (Integer) body.get("id");
        
        //If the rating can't be found then set it to an invalid value
        Short r = ((Integer) body.getOrDefault("r", -2)).shortValue();

        // Rating can only be between [-1,1] (inclusive)
        if(r < -1 || r > 1)
            return;

        userAccounts.setUserRatingForGame(principal.getName(), id, r);
    }

    @PostMapping("/add_comment")
    public ModelAndView addComment(Principal principal, @RequestBody Map<String, Object> body){
        if(principal == null)
            return null;

        Integer id = (Integer) body.get("id");
        String comment = (String) body.get("comment");

        if(StringUtils.isBlank(comment) || comment.length() > WebsiteContentService.MAX_USER_COMMENT_LENGTH)
            return null;

        Timestamp current_time = Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        webContent.addComment(principal.getName(), id, current_time, comment);

        ModelAndView mav = new ModelAndView("fragments/comment_entry.html :: entry");
        mav.addObject("username", principal.getName());
        mav.addObject("comment", comment);
        mav.addObject("time_stamp", current_time);
        
        return mav;
    }

    @DeleteMapping("/delete_comment")
    public void deleteComment(Principal principal, @RequestBody Map<String, Object> body){
        if(principal == null)
            return;

        Integer id = (Integer) body.get("id");
        String timestamp = (String) body.get("timestamp");

        webContent.deleteComment(principal.getName(), id, timestamp);
    }


}//End of class
