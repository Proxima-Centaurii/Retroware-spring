package dev.alphacentaurii.RETROWARE.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.alphacentaurii.RETROWARE.dao.CategoryDAO;
import dev.alphacentaurii.RETROWARE.dao.CommentDAO;
import dev.alphacentaurii.RETROWARE.dao.GameDAO;
import dev.alphacentaurii.RETROWARE.dao.RatingCountDAO;
import dev.alphacentaurii.RETROWARE.enums.SearchSortCriteria;
import dev.alphacentaurii.RETROWARE.model.Category;
import dev.alphacentaurii.RETROWARE.model.Comment;
import dev.alphacentaurii.RETROWARE.model.Game;
import dev.alphacentaurii.RETROWARE.model.RatingCount;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsiteContentService {
    
    private final int MAX_SEARCH_TOKENS = 10;
    public static final int MAX_SEARCH_STRING_LENGTH = 30;
    public static final int MAX_USER_COMMENT_LENGTH = 150;

    private final CategoryDAO categoryDAO;
    private final GameDAO gameDAO;
    private final CommentDAO commentDAO;
    private final RatingCountDAO ratingsCountDAO;
    
    private HashMap<String, Category> categories;
    private final SearchSortCriteria[] sort_options;
    private final int sort_options_len;

    @Autowired
    public WebsiteContentService(CategoryDAO categoryDAO, GameDAO gameDAO, CommentDAO commentDAO, RatingCountDAO ratingsCountDAO){
        this.categoryDAO = categoryDAO;
        this.gameDAO = gameDAO;
        this.commentDAO = commentDAO;
        this.ratingsCountDAO = ratingsCountDAO;

        sort_options = SearchSortCriteria.values();
        sort_options_len = sort_options.length;

        categories = new HashMap<String, Category>();
        for(Category c : categoryDAO.getAllCategories()){
            categories.put(c.title().toLowerCase().replace(' ', '_'), c);
        }
    }

    public List<Game> getFeaturedGames(){
        return gameDAO.getFeaturedGames();
    }

     public List<Game> getPopularGames(){
        return gameDAO.getPopularGames();
    }

    public List<Game> getAllGames(){
        return gameDAO.getAllGames();
    }

    public HashMap<String,Category> getAllCategories(){
        return categories;
    }

    public Category getCategoryById(Integer id){
        if(id == null)
            return null;
        
        return categoryDAO.getCategoryById(id);
    }

    public Category getCategoryByName(String name){
        return categories.get(name);
    }

    public RatingCount getRatingsForGame(Integer id){
        if(id == null)
            return null;

        return ratingsCountDAO.getCurrentRatingCountForGame(id);
    }

    public Game getGame(Integer id){
        if(id == null)
            return null;

        return gameDAO.getGameById(id);
    }

    public int getCategoryId(String key){
        Category c = categories.get(key);

        if(c == null)
            return 0;

        return c.id();
    }

    public List<Game> getUserLikedGames(String username){
        
        if(StringUtils.isBlank(username))
            return null;

        return gameDAO.getUserLikedGames(username);
    }

    public List<Game> searchGames(String search_string, String selected_category, Integer sorting_option_ordinal){
        // Search string may not be empty
        if(StringUtils.isBlank(search_string)){
            log.debug("Blank search string!");
            return null;
        }

        log.debug("Received search string: " + search_string);
        search_string = search_string.trim();
        log.debug("Processed search string: " + search_string);

        //Categories
        int category_id = 0;
        Category c = categories.get(selected_category);
        if(c != null)
            category_id = c.id();


        return gameDAO.searchGames(search_string, category_id, sort_options[sorting_option_ordinal], MAX_SEARCH_TOKENS);
    }

    public SearchSortCriteria[] getSortingOptions(){
        return sort_options;
    }

    public List<Game> getGamesInCategory(String name){
        return getGamesInCategory(name, 0);
    }

    public List<Game> getGamesInCategory(String name, int sortBy){
        if(!categories.containsKey(name))
            return null;

        //Check that the sortBy is withing bounds
        sortBy = sortBy >= sort_options_len ? 0 : sortBy;

        return gameDAO.getAllGamesInCategory(categories.get(name).id(), sort_options[sortBy]);
    }

    public List<Comment> getGameComments(Integer game_id, String username){
        List<Comment> result;

        //Get list of comments for when user is not logged in
        if(StringUtils.isBlank(username)){
            result = commentDAO.getGameComments(game_id);
            return result;
        }

        //Get list of comment for when the user is logged in
        result = commentDAO.getGameComments(game_id, username);

        return result;
    }

    public void addComment(String username, Integer game_id, Timestamp current_time, String comment){
        if(StringUtils.isBlank(comment) || game_id == null || StringUtils.isBlank(username))
            return;

        commentDAO.addComment(username, game_id, current_time, comment);
    }

    public void deleteComment(String username, Integer game_id, String timestamp){
        if(StringUtils.isBlank(username) || game_id == null || StringUtils.isBlank(timestamp))
            return;

        commentDAO.deleteComment(game_id, username, Timestamp.valueOf(timestamp));
    }


}//End of class
