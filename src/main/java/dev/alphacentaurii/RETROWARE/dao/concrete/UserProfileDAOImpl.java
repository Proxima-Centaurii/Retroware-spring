package dev.alphacentaurii.RETROWARE.dao.concrete;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dev.alphacentaurii.RETROWARE.dao.UserProfileDAO;
import dev.alphacentaurii.RETROWARE.model.UserProfile;

@Repository
public class UserProfileDAOImpl implements UserProfileDAO {
    
    private final JdbcTemplate jdbcTemplate;

    private final String GET_PROFILE = "SELECT * FROM USER_PROFILE WHERE USERNAME = ?";
    private final String UPDATE_DESCRIPTION = "UPDATE USER_PROFILE SET DESCRIPTION = ? WHERE USERNAME = ?";

    
    public UserProfileDAOImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserProfile getUserProfile(String username){
        UserProfile result;

        try{
            result = jdbcTemplate.queryForObject(GET_PROFILE, UserProfile::rowMap, username);    
            return result; 
        }catch(IncorrectResultSizeDataAccessException e){
            if(e.getActualSize() > 1)
                e.printStackTrace();

            return null;
        }
    }

    public void updateDescription(String new_description, String username){
        jdbcTemplate.update(UPDATE_DESCRIPTION, new_description, username);
    }

}//End of class
