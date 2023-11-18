package dev.alphacentaurii.RETROWARE.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record Category (
    @Id
    Integer id,
    @NotBlank
    @Max(25)
    String title,
    @Max(150)
    String description
){
    public static Category rowMap(ResultSet rs, int rowNum) throws SQLException{
        return new Category(
            rs.getInt("CATEGORY_ID"), 
            rs.getString("TITLE"),
            rs.getString("DESCRIPTION")
            );
    }
}
