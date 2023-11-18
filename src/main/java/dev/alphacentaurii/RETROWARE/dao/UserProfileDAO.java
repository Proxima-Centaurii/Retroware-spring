package dev.alphacentaurii.RETROWARE.dao;

import dev.alphacentaurii.RETROWARE.model.UserProfile;

public interface UserProfileDAO {
    
    public UserProfile getUserProfile(String username);

    public void updateDescription(String new_description, String username);

}//End of class
