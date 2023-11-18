package dev.alphacentaurii.RETROWARE.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserRating{
    @Id
    @NotNull
    private Integer game_id;

    @NotNull
    @Min(-1)
    @Max(1)
    private Integer rating;

    public static UserRating rowMap(ResultSet rs, int rowNum) throws SQLException{
        return new UserRating(
            rs.getInt("GAME_ID"),
            rs.getInt("Rating")
        );
    }
}
