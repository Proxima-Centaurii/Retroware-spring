package dev.alphacentaurii.RETROWARE.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is used to store rating counts, likes and dislikes, for a
 * single game and it is used in updating the ratings of games.
 */

 @AllArgsConstructor
 @NoArgsConstructor
 @Data
public class RatingCount implements Serializable{

    @NotNull
    private Integer game_id;

    private Integer likes;

    private Integer dislikes;

    private Timestamp last_update;

    // Initialising delta ratings
    public RatingCount(Integer likes, Integer dislikes){
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public void add(RatingCount delta){
        likes += delta.likes;
        dislikes += delta.dislikes;
    }

    public static RatingCount rowMapDelta(ResultSet rs, int rowNum) throws SQLException{
        return new RatingCount(rs.getInt(1), rs.getInt(2));
    }

    public static RatingCount rowMap(ResultSet rs, int rowNum) throws SQLException{
        return new RatingCount(
            rs.getInt(1),
            rs.getInt(2),
            rs.getInt(3),
            rs.getTimestamp(4)
        );
    }
}