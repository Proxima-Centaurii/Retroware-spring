package dev.alphacentaurii.RETROWARE.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class PlayCount {
    
    private Integer game_id;
    private Integer count;

    public static PlayCount rowMap(ResultSet rs, int rowNum) throws SQLException{
        return new PlayCount(
            rs.getInt("GAME_ID"),
            rs.getInt("COUNT")
        );
    }

}//End of class
