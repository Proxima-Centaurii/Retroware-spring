package dev.alphacentaurii.RETROWARE.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dev.alphacentaurii.RETROWARE.dao.PlayCountDAO;
import dev.alphacentaurii.RETROWARE.enums.SearchSortCriteria;
import dev.alphacentaurii.RETROWARE.model.Category;
import dev.alphacentaurii.RETROWARE.model.Game;
import dev.alphacentaurii.RETROWARE.service.UserAccountsService;
import dev.alphacentaurii.RETROWARE.service.WebsiteContentService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class RetrowareController {

    private final WebsiteContentService webContent;
    private final UserAccountsService userAccounts;
    private final PlayCountDAO playCount;

    @Autowired
    public RetrowareController(WebsiteContentService webContent, UserAccountsService userAccounts, PlayCountDAO playCount){
        this.webContent = webContent;
        this.userAccounts = userAccounts;
        this.playCount = playCount;
    }

    @GetMapping("/")
    public String indexPage(){
        return "index";
    }

    
    @GetMapping("/home")
    public String homePage(Model model){
        return "home";
    }


    @GetMapping("/license")
    public String licensePage(){
        return "license";
    }

    @GetMapping("/game")
    public String gamePage(HttpServletRequest request, Model model,Principal principal, @RequestParam(value="id", required = true) Integer id){
        if(id == null)
            return "redirect:/home";
        
        Game game = webContent.getGame(id);

        if(game == null){
            return "redirect:/home";
        }

        model.addAttribute("game", game);
        model.addAttribute("rating", webContent.getRatingsForGame(id));

        // Get comments and user rating if the user is authenticated
        if(principal != null){
            model.addAttribute("user_rating", userAccounts.getUserRatingForGame(principal.getName(), id));
            model.addAttribute("comments_list", webContent.getGameComments(id, principal.getName()));
        }
        // Get comments and user rating if the user is not authenticated
        else{
            model.addAttribute("user_rating", 0);
            model.addAttribute("comments_list", webContent.getGameComments(id, null));
        }

        String client = (principal != null) ? principal.getName() : request.getRemoteAddr();
        log.trace(client + " viewed game " + id);

        playCount.insertUniqueEntry(client, id);
   
        return "game_page";
    }

    @GetMapping("/category")
    public String categoryPage(Model model,  @RequestParam (value="c", required = true) String category_name){

        // Get the details of requested category and also check if it's valid        
        Category current_category = webContent.getCategoryByName(category_name);
        if(current_category == null){
            return "redirect:/home";
        }

        model.addAttribute("current_category", current_category);
        model.addAttribute("sorting_options", SearchSortCriteria.values());

        // Fetch all games in the category and add them to the model
        model.addAttribute("game_list", webContent.getGamesInCategory(category_name));

        return "category";
    }
    
    @GetMapping("/search")
    public String searchPage(Model model, @RequestParam (value="s", required = true) String search_string){

        /* The search string will be checked on client side before making the request.
        * If the user removes client side validation then check on server side if the submitted 
        * string is not blank (not empty, null, or only white spaces).*/
        if(StringUtils.isBlank(search_string)){
            log.warn("Received empty search string! (navbar search box)");
            return "redirect:/home";
        }

        String category = "ALL_GAMES";
        Integer sorting_option = SearchSortCriteria.HIGHEST_RATING.ordinal();

        if(search_string.length() > WebsiteContentService.MAX_SEARCH_STRING_LENGTH)
            return "redirect:/home";

        model.addAttribute("game_list", webContent.searchGames(search_string, category, sorting_option));
        model.addAttribute("search_string", search_string);
        model.addAttribute("selected_category", category);
        model.addAttribute("selected_sorting_option", sorting_option);

        return "search";
    }
    

}//end of class
