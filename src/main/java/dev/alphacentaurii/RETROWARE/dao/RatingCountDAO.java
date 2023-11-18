package dev.alphacentaurii.RETROWARE.dao;

import java.sql.Timestamp;
import java.util.List;

import dev.alphacentaurii.RETROWARE.model.RatingCount;

public interface RatingCountDAO {
    
    public RatingCount getDeltaRatingCountForGame(Integer game_id, Timestamp threshold);
    public void updateRatingCountForGame(final List<RatingCount> delta_ratings);
    public void updateUserRatingsLastProcessed(final List<RatingCount> ratings);

    public RatingCount getCurrentRatingCountForGame(Integer game_id);
    public List<RatingCount> getRatingCountByRowBounds(Integer offset, Integer size);

}//End class
