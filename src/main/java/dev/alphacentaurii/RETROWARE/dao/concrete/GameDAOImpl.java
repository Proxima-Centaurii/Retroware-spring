package dev.alphacentaurii.RETROWARE.dao.concrete;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;

import dev.alphacentaurii.RETROWARE.dao.GameDAO;
import dev.alphacentaurii.RETROWARE.enums.SearchSortCriteria;
import dev.alphacentaurii.RETROWARE.model.Game;
import dev.alphacentaurii.RETROWARE.model.PlayCount;
import io.micrometer.common.util.StringUtils;

@Repository
public class GameDAOImpl implements GameDAO {
    
    JdbcTemplate jdbcTemplate;

    private final String FEATURED_GAMES = "SELECT * FROM FEATURED_GAMES FETCH FIRST 100 ROWS ONLY";
    private final String POPULAR_GAMES = "SELECT * FROM POPULAR_GAMES FETCH FIRST 100 ROWS ONLY";
    private final String ALL_GAMES_FIRST_100 = "SELECT * FROM GAMES WHERE UNLISTED = FALSE FETCH FIRST 100 ROWS ONLY";
    private final String FIND_GAME_BY_ID = "SELECT * FROM GAMES WHERE GAME_ID = ?";
    private final String UPDATE_PLAY_COUNT = "UPDATE GAMES SET PLAY_COUNT = (PLAY_COUNT + ?) WHERE GAME_ID = ?";
    private final String UPDATE_GAME_RATING = "UPDATE GAMES SET RATING = ? WHERE GAME_ID = ?";

    private final String ADVANCED_SEARCH_TEMPLATE = "SELECT * FROM GAMES %s %s UNLISTED = FALSE ORDER BY %s FETCH FIRST 100 ROWS ONLY";
    private final String SEARCH_IN_CATEGORY = "RIGHT JOIN GAME_CATEGORIES ON GAMES.GAME_ID = GAME_CATEGORIES.GAME_ID WHERE GAME_CATEGORIES.CATEGORY_ID = ? AND";

    private final String GET_USER_LIKED_GAMES = "SELECT * FROM GAMES INNER JOIN GAME_RATINGS ON GAME_RATINGS.GAME_ID = GAMES.GAME_ID WHERE USERNAME = ? AND GAME_RATINGS.RATING = 1";


    @Autowired
    public GameDAOImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Game getGameById(int game_id) {
        Game result;
        
        try{
            result = jdbcTemplate.queryForObject(FIND_GAME_BY_ID, Game::rowMap, game_id);
            return result;
        }catch(IncorrectResultSetColumnCountException e){
            if(e.getActualCount() > 1)
                e.printStackTrace();
            
            return null;
        }
    }

    @Override
    public List<Game> getFeaturedGames() {
        return jdbcTemplate.query(FEATURED_GAMES ,Game::rowMap);
    }

    @Override
    public List<Game> getPopularGames() {
        return jdbcTemplate.query(POPULAR_GAMES, Game::rowMap);
    }

    @Override
    public List<Game> getAllGames() {
        return jdbcTemplate.query(ALL_GAMES_FIRST_100, Game::rowMap);
    }

    /**
     * Performs a database search and returns a list of games based on the search string.
     * @param search_string Input from the user.
     * @param selected_category Will search only in the category specified or in all categories if this value is 0.
     * @param sort_criteria An enum that indicates how to sort the results. The SQL necessary for sorting is contained in this enum value.
     * @param token_limit This value represents the maximum amount of tokens to process in the search string. Must be a positive number.
     * @return Returns a list of type 'Game'
     */
    @Override
    public List<Game> searchGames(String search_string, int selected_category, SearchSortCriteria sort_criteria, int token_limit) {

        String query;
        boolean search_in_all_games = (selected_category == 0);
        boolean  blank_search_string = StringUtils.isBlank(search_string);

        String[] tokens = search_string.split(" ");

        int total_search_tokens = (tokens.length < token_limit ? tokens.length : token_limit);

        query = String.format(ADVANCED_SEARCH_TEMPLATE,
                                search_in_all_games ? "WHERE" : SEARCH_IN_CATEGORY,
                                blank_search_string ? "" : sqlFromTokens(total_search_tokens),
                                sort_criteria.SQL
                                );


        //Fetch a List of the games based on the search tokens
        List<Game> result = jdbcTemplate.query(query, 
        
        //Set the token values in query manually via PreparedStatementSetter class (passed as 2nd argument)
        (PreparedStatement p) -> {
                    int start_pos = 1;
                    
                    if(!search_in_all_games){
                        start_pos = 2;
                        p.setInt(1, selected_category);
                    }

                    if(!blank_search_string){

                        for(int i=0; i<total_search_tokens; i++){
                            p.setString(start_pos++, "%" + tokens[i] + "%");
                        }

                    }
                        
            }
            , Game::rowMap);
        

        return result;
    }

     @Override
    public List<Game> getAllGamesInCategory(int selected_category, SearchSortCriteria sort_criteria) {
        String query = String.format(ADVANCED_SEARCH_TEMPLATE, SEARCH_IN_CATEGORY, "", sort_criteria.SQL);
        
        return jdbcTemplate.query(query, Game::rowMap, selected_category);
    }

    @Override
    public void updateDeltaGamePlayCount(long delta_play_count, int game_id) {
        jdbcTemplate.update(UPDATE_PLAY_COUNT, delta_play_count, game_id);
    }

    @Override
    public void updateDeltaGamePlayCount(List<PlayCount> delta_play_counts) {
        jdbcTemplate.batchUpdate(UPDATE_PLAY_COUNT, 
                                new BatchPreparedStatementSetter() {

                                    @Override
                                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                                        PlayCount delta = delta_play_counts.get(i);
                                        ps.setInt(1, delta.getCount());
                                        ps.setInt(2, delta.getGame_id());
                                    }

                                    @Override
                                    public int getBatchSize() {
                                        return delta_play_counts.size();
                                    }
                                    
                                });
    }

    private String sqlFromTokens(int token_count){
        StringBuilder sql = new StringBuilder("( ");

        for(int i=token_count-1; i > 0; i--)
            sql.append("TITLE ILIKE ? OR ");
        
        sql.append("TITLE ILIKE ? ) AND");

        return sql.toString();
    }

    @Override
    public List<Game> getUserLikedGames(String username) {
        return jdbcTemplate.query(GET_USER_LIKED_GAMES, Game::rowMap, username);
    }


    @Override
    public void updateGameRating(List<Game> updated_ratings) {
        jdbcTemplate.batchUpdate(UPDATE_GAME_RATING,
                                new BatchPreparedStatementSetter() {

                                    @Override
                                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                                        Game game = updated_ratings.get(i);
                                        ps.setShort(1, game.getRating());
                                        ps.setInt(2, game.getGame_id());
                                    }

                                    @Override
                                    public int getBatchSize() {
                                        return updated_ratings.size();
                                    }
                                    
                                });
    }

}//End of class