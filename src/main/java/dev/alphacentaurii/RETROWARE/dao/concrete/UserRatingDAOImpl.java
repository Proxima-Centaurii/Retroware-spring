package dev.alphacentaurii.RETROWARE.dao.concrete;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dev.alphacentaurii.RETROWARE.dao.UserRatingDAO;

@Repository
public class UserRatingDAOImpl implements UserRatingDAO {
    
    private final JdbcTemplate jdbcTemplate;

    private final String GET_USER_RATING_FOR_GAME = "SELECT RATING FROM GAME_RATINGS WHERE USERNAME = ? AND GAME_ID = ?";
    private final String ADD_USER_RATING_FOR_GAME = "INSERT INTO GAME_RATINGS(GAME_ID, USERNAME, RATING) VALUES (?,?,?)";
    private final String UPDATE_USER_RATING_FOR_GAME = "UPDATE GAME_RATINGS SET RATING = ?, RATED_ON = CURRENT_TIMESTAMP(0) WHERE USERNAME = ? AND GAME_ID = ?";


    public UserRatingDAOImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Short getUserGameRating(String username, Integer game_id){
        Short result;
        
        try{
            result = jdbcTemplate.queryForObject(GET_USER_RATING_FOR_GAME, (rs, row)-> rs.getShort("RATING"), username, game_id);
            return result;
        }catch(IncorrectResultSizeDataAccessException e){
            if(e.getActualSize() > 1)
                e.printStackTrace();

            return null;
        }

    }
    
    public void setUserGameRating(String username, Integer game_id, Short rating){
        Short result = getUserGameRating(username, game_id);

        if(result != null)
            jdbcTemplate.update(UPDATE_USER_RATING_FOR_GAME, rating, username, game_id);
        else
            jdbcTemplate.update(ADD_USER_RATING_FOR_GAME, game_id, username, rating);

    }

}//End of class
