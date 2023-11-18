package dev.alphacentaurii.RETROWARE.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Comment (
    
    String username,
    
    @NotNull
    Integer game_id,

    @NotNull
    Timestamp posted_on,

    @NotBlank
    @Max(150)
    String comment
){
    public static Comment rowMap(ResultSet rs, int rowNum) throws SQLException{
        return new Comment(
            rs.getString("USERNAME"),
            rs.getInt("GAME_ID"),
            rs.getTimestamp("POSTED_ON"),
            rs.getString("COMMENT"));
    }

}
