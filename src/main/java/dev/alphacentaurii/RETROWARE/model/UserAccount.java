package dev.alphacentaurii.RETROWARE.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UserAccount implements Serializable{
    
    @Id
    @Max(50)
    @Pattern(regexp="^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9])*[a-zA-Z0-9]$")
    private String username;

    @NotBlank
    @Max(60)
    private String password;

    @NotNull
    private boolean enabled;

    //CONSTRUCTORS
    public UserAccount(String username, String password, boolean enabled){
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    //GETTERS AND SETTERS

    public String getUsername(){return username;}
    public void setUsername(String username){this.username = username;}

    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}

    public boolean getEnabled(){return enabled;}
    public void setEnabled(boolean enabled){this.enabled = enabled;}

    //TO STRING
    @Override
    public String toString(){
        return String.format("UserAccount [%s, %s, %s]", username, password, enabled ? "TRUE" : "FALSE");
    }

    //ROW MAPPER
    public static UserAccount rowMap(ResultSet rs, int rowNum) throws SQLException{
        return new UserAccount(
            rs.getString("USERNAME"),
            rs.getString("PASSWORD"),
            rs.getBoolean("ENABLED")
        );
    }

}//End of class
