package dev.alphacentaurii.RETROWARE.dao.concrete;

import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dev.alphacentaurii.RETROWARE.dao.UserAccountDAO;
import dev.alphacentaurii.RETROWARE.model.UserAccount;

@Repository
public class UserAccountDAOImpl implements UserAccountDAO {
    
    private final JdbcTemplate jdbcTemplate;

    private final String GET_BY_USERNAME = "SELECT * FROM USERS WHERE USERNAME = ?";
    private final String GET_AUTHORITIES_FOR_USER = "SELECT AUTHORITY FROM AUTHORITIES USE INDEX(IX_AUTH_USERNAME) WHERE USERNAME = ?";

    private final String ADD_USER = "INSERT INTO USERS (USERNAME, PASSWORD, ENABLED) VALUES (?,?,?)";
    private final String ADD_AUTHORITY = "INSERT INTO AUTHORITIES (USERNAME, AUTHORITY) VALUES (?,?)";
    private final String ADD_DEFAULT_USER_PROFILE = "INSERT INTO USER_PROFILE (USERNAME, JOIN_DATE, DESCRIPTION, PICTURE_NAME) VALUES (?, ?, ?, ?)";

    private final String COUNT_BY_USERNAME = "SELECT COUNT(USERNAME) as \"count\" FROM USERS WHERE USERNAME = ?";

    private final String DELETE_USER = "DELETE FROM USERS WHERE USERNAME = ?";

    public UserAccountDAOImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserAccount getUserByUsername(String username){
        UserAccount result;

        try{
            result = jdbcTemplate.queryForObject(GET_BY_USERNAME, UserAccount::rowMap, username);
            return result;
        }catch(IncorrectResultSizeDataAccessException e){
            if(e.getActualSize() > 1)
                e.printStackTrace();

            return null;
        }
    }

    public List<String> getUserAuthorities(String username){
        return jdbcTemplate.query(GET_AUTHORITIES_FOR_USER, (rs, rowNum)-> rs.getString("AUTHORITY"), username);
    }

    public Integer usernameCount(String username){
        return jdbcTemplate.queryForObject(COUNT_BY_USERNAME, (rs, rowNum)-> rs.getInt("COUNT"), username);
    }

    public void addUser(String username, String password){
        jdbcTemplate.update(ADD_USER, username, password, true);
        jdbcTemplate.update(ADD_AUTHORITY, username, "ROLE_USER");
        jdbcTemplate.update(ADD_DEFAULT_USER_PROFILE, username, LocalDate.now(), "Hello! I am a new user!", "");
    }

    public void deleteUser(String username){
        jdbcTemplate.update(DELETE_USER, username);
    }

}//End of class
