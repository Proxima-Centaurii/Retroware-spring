package dev.alphacentaurii.RETROWARE.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import dev.alphacentaurii.RETROWARE.enums.SearchSortCriteria;
import dev.alphacentaurii.RETROWARE.model.Category;
import dev.alphacentaurii.RETROWARE.model.Game;
import dev.alphacentaurii.RETROWARE.service.WebsiteContentService;

/**
 * This class contains model attributes that is be available throughout the WebApp.
*/

@ControllerAdvice
public class GlobalControllerAdvice {
    
    private final WebsiteContentService webContent;


    @Autowired
    public GlobalControllerAdvice(WebsiteContentService webContent){
        this.webContent = webContent;
    }

    @ModelAttribute("all_games")
    public List<Game> getAllGames(){
        return webContent.getAllGames();
    }

    @ModelAttribute("featured_games")
    public List<Game> getFeaturedGames(){
        return webContent.getFeaturedGames();
    }

    @ModelAttribute("popular_games")
    public List<Game> getPopularGames(){
        return webContent.getPopularGames();
    }

    @ModelAttribute("category_map")
    public HashMap<String, Category> getAllCategories(){
        return webContent.getAllCategories();
    }

    @ModelAttribute("sorting_options")
    public SearchSortCriteria[] getSortingOptions(){
        return webContent.getSortingOptions();
    }

    @ModelAttribute("principal_name")
    public String getPrincipalName(Principal principal){
        if(principal == null)
            return null;

        return principal.getName();
    }

}//End class
