package dev.alphacentaurii.RETROWARE.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserProfile implements Serializable{
  
    @Id
    @NotBlank
    @Max(50)
    private String username;

    @NotNull
    private Date join_date;

    @NotNull
    private String description;
    
    private String picture_name;

    public static UserProfile rowMap(ResultSet rs, int rowNum) throws SQLException{
        return new UserProfile(
            rs.getString("USERNAME"),
            rs.getDate("JOIN_DATE"),
            rs.getString("DESCRIPTION"),
            rs.getString("PICTURE_NAME")
        );
    }
}