package dev.alphacentaurii.RETROWARE.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Game{

    @Id
    int game_id;
    
    @Max(25)
    @UniqueElements
    String title;

    @Max(150)
    String description;

    long play_count;

    @Min(0)
    @Max(100)
    short rating;

    @NotNull
    boolean unlisted; 

    @Max(32)
    String resource_name;
    Date publish_date;

    public static Game rowMap(ResultSet rs, int rowNum) throws SQLException{
        return new Game(
            rs.getInt("GAME_ID"),
            rs.getString("TITLE"),
            rs.getString("DESCRIPTION"),
            rs.getLong("PLAY_COUNT"),
            rs.getShort("RATING"),
            rs.getBoolean("UNLISTED"),
            rs.getString("RESOURCE_NAME"),
            rs.getDate("PUBLISH_DATE")
        );
    }
}
