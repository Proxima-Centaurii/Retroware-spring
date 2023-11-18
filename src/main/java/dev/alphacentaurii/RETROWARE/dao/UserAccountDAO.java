package dev.alphacentaurii.RETROWARE.dao;

import java.util.List;

import dev.alphacentaurii.RETROWARE.model.UserAccount;

public interface UserAccountDAO {
    
    public UserAccount getUserByUsername(String username);

    public List<String> getUserAuthorities(String username);

    public Integer usernameCount(String username);

    public void addUser(String username, String password);

    public void deleteUser(String username);

}//End of class
