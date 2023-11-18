package dev.alphacentaurii.RETROWARE.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import dev.alphacentaurii.RETROWARE.service.DynamicContentService;
import dev.alphacentaurii.RETROWARE.service.WebsiteContentService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.ServletContext;

@Controller
public class ContentController {
    
    private final WebsiteContentService webContent;
    private final DynamicContentService dynamicContent;

    private final Resource default_profile_pic;


    @Autowired
    public ContentController(ServletContext servletContext, WebsiteContentService webContent, DynamicContentService dynamicContent){
        this.webContent = webContent;
        this.dynamicContent = dynamicContent;

        WebApplicationContext app_context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        default_profile_pic = app_context.getResource("classpath:static/media/website_default/default_profile.jpg");
    }

    @PostMapping("/search_games")
    public String updateCategoryGameList( Model model, @RequestBody Map<String,Object> body){
        
        String search_string;
        String category;
        Integer sort_option;

        //Data might be altered on client side and casting error might occur
        try{
            search_string = (String) body.getOrDefault("s", "");
            category = (String)body.get("c");
            sort_option = (Integer) body.getOrDefault("sort", 0);
        }catch(Exception e){
            return "";
        }

        if(StringUtils.isBlank(search_string))
            model.addAttribute("game_list", webContent.getGamesInCategory(category,  sort_option));
        else
            model.addAttribute("game_list", webContent.searchGames(search_string, category, sort_option));

        return "fragments/game_list.html :: content";
    }

    @GetMapping("/profile_picture")
    public ResponseEntity<?> getCurrentUserProfilePicture(Principal principal){
        try{
            if(principal == null)
            return ResponseEntity.ok()
                    .contentLength(default_profile_pic.contentLength())
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(default_profile_pic);
        }catch(IOException e){
            return ResponseEntity.badRequest()
                .body("Could not load default profile picture\n" + e.getMessage());
        }
        
        return getUserProfilePicture(principal.getName());
    }

    @GetMapping("/profile_picture/{username}")
    @ResponseBody
    public ResponseEntity<?> getUserProfilePicture(@PathVariable String username){
        try{
            Resource file = dynamicContent.getUserProfilePicture(username);

            if(file.exists()){
                return ResponseEntity.ok()
                    .contentLength(file.contentLength())
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(file);
            }
            
            return ResponseEntity.ok()
                    .contentLength(default_profile_pic.contentLength())
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(default_profile_pic);
        }
        catch(IOException e){
            return ResponseEntity.badRequest()
                .body("Could not find profile image for '" + username + "'\n" + e.getMessage());
        } 
    }


}//End of class
