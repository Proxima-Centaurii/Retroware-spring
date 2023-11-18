package dev.alphacentaurii.RETROWARE.dao.concrete;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dev.alphacentaurii.RETROWARE.dao.CommentDAO;
import dev.alphacentaurii.RETROWARE.model.Comment;

@Repository
public class CommentDAOImpl implements CommentDAO {

    private final JdbcTemplate jdbcTemplate;

    private final String ADD_COMMENT = "INSERT INTO COMMENTS (USERNAME, GAME_ID, POSTED_ON, COMMENT) VALUES (?,?, IFNULL(?, CURRENT_TIMESTAMP(0)) ,?)";

    private final String GET_COMMENTS = "SELECT * FROM COMMENTS WHERE GAME_ID = ? AND USERNAME IS NOT NULL";
    private final String GET_COMMENTS_OF_USER = "SELECT * FROM COMMENTS WHERE USERNAME = ? AND GAME_ID = ?"; 
    private final String GET_COMMENTS_EXCLUDING_USER = "SELECT * FROM COMMENTS WHERE USERNAME <> ? AND USERNAME IS NOT NULL AND GAME_ID = ?";

    private final String DELETE_COMMENT = "DELETE FROM COMMENTS WHERE GAME_ID = ? AND USERNAME = ? AND POSTED_ON = ?";

    @Autowired
    public CommentDAOImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addComment(String username, Integer game_id, Timestamp timestamp, String comment) {
        jdbcTemplate.update(ADD_COMMENT, username, game_id, timestamp, comment);
    }

    /**
     * Get a list of comments for the specified game. This method is used for when the user is not
     * authenticated.
     * @param game_id The id of the game the comment is posted on.
     * @return Returns a list of comments or an empty list if no comments are found.
     */
    @Override
    public List<Comment> getGameComments(Integer game_id){
        return jdbcTemplate.query(GET_COMMENTS, Comment::rowMap, game_id);
    }

    /**
     * Get a list of comments with the authenticated user's comments as first entries in the list.
     * @param game_id The id of the game the comment is posted on.
     * @param username The name of the user that posted the comment.
     * @return Returns a list of comments or an empty list if no comments are found.
     */
    @Override
    public List<Comment> getGameComments(Integer game_id, String username) {
        List<Comment> user_comments = jdbcTemplate.query(GET_COMMENTS_OF_USER, Comment::rowMap, username, game_id);
        List<Comment> user_excluded_comments = jdbcTemplate.query(GET_COMMENTS_EXCLUDING_USER, Comment::rowMap, username, game_id);

        user_comments.addAll(user_excluded_comments);

        return user_comments;
    }

    /**
     * Deletes a specific comment.
     * @param game_id The id of the game the comment was posted on.
     * @param username The user that posted the comment.
     * @param posted_on The time when the comment was posted (added in the database). Format: yyyy-MM-dd HH:mm:ss
     */
    @Override
    public void deleteComment(Integer game_id, String username, Timestamp posted_on) {
        jdbcTemplate.update(DELETE_COMMENT, game_id, username, posted_on);
    }

    
}//End of class