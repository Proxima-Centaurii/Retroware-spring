package dev.alphacentaurii.RETROWARE.dao;

import java.sql.Timestamp;
import java.util.List;

import dev.alphacentaurii.RETROWARE.model.Comment;

public interface CommentDAO {
    
    public void addComment(String username, Integer game_id, Timestamp timestamp, String comment);

    public List<Comment> getGameComments(Integer game_id);
    public List<Comment> getGameComments(Integer game_id, String username);

    public void deleteComment(Integer game_id, String username, Timestamp posted_on);

}
