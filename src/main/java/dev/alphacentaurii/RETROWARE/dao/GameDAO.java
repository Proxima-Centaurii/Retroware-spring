package dev.alphacentaurii.RETROWARE.dao;

import java.util.List;

import dev.alphacentaurii.RETROWARE.enums.SearchSortCriteria;
import dev.alphacentaurii.RETROWARE.model.Game;
import dev.alphacentaurii.RETROWARE.model.PlayCount;

public interface GameDAO {

    public Game getGameById(int game_id);
    public List<Game> getFeaturedGames();
    public List<Game> getPopularGames();
    public List<Game> getAllGames();
    public List<Game> searchGames(String search_string, int selected_category, SearchSortCriteria sort_criteria, int token_limit);
    public List<Game> getAllGamesInCategory(int selected_category, SearchSortCriteria sort_criteria);
    public List<Game> getUserLikedGames(String username);

    public void updateDeltaGamePlayCount(long delta_play_count, int game_id);
    public void updateDeltaGamePlayCount(List<PlayCount> delta_play_count);
    public void updateGameRating(List<Game> updated_ratings);

}
