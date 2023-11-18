package dev.alphacentaurii.RETROWARE.dao;

public interface UserRatingDAO {
    
    public Short getUserGameRating(String username, Integer game_id);

    public void setUserGameRating(String username, Integer game_id, Short rating);

}//End class
