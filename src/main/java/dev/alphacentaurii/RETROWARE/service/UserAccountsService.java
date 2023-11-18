package dev.alphacentaurii.RETROWARE.service;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.alphacentaurii.RETROWARE.dao.UserAccountDAO;
import dev.alphacentaurii.RETROWARE.dao.UserProfileDAO;
import dev.alphacentaurii.RETROWARE.dao.UserRatingDAO;
import dev.alphacentaurii.RETROWARE.enums.UserRegistrationStatus;
import dev.alphacentaurii.RETROWARE.model.UserProfile;
import io.micrometer.common.util.StringUtils;

@Service
public class UserAccountsService {
    
    public final static int MAX_USER_DESCRIPTION_LENGTH = 200;
    public final static int MIN_USER_PASSWORD_LENGTH = 5;
    public final static int MAX_USER_PASSWORD_LENGTH = 50;
    private final int MIN_PASSWORD_ENTROPY = 25;

    private final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){1,24}$");
    private final Pattern HAS_UPPERCASE_PATTERN = Pattern.compile("[A-Z]{1}");
    private final Pattern HAS_LOWERCASE_PATTERN = Pattern.compile("[a-z]{1}");
    private final Pattern HAS_DIGIT_PATTERN = Pattern.compile("[0-9]{1}");
    private final Pattern HAS_SYMBOLS_PATTERN = Pattern.compile("[!\\|\\\\¬`\\\"£\\$%\\^&\\*\\(\\)_\\-\\+=\\/\\?><\\.,:;@'~#\\[\\]{}]{1}");
    
    private final UserAccountDAO userAccounts;
    private final UserProfileDAO userProfiles;
    private final UserRatingDAO userRatings;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserAccountsService(UserAccountDAO userAccounts, UserProfileDAO userProfiles, UserRatingDAO userRatings, PasswordEncoder passwordEncoder){
        this.userAccounts = userAccounts;
        this.userProfiles = userProfiles;
        this.userRatings = userRatings;

        this.passwordEncoder = passwordEncoder;
    }

    public UserProfile getUserProfile(String username){
        return userProfiles.getUserProfile(username);
    }

    public UserRegistrationStatus registerUser(String username, String password, String confirm_passowrd){
        
        //Check username
        if(StringUtils.isEmpty(username) || !USERNAME_PATTERN.matcher(username).matches())
            return UserRegistrationStatus.USERNAME_UNACCEPTABLE;

        Integer user_count = userAccounts.usernameCount(username);
        if(user_count != null && user_count > 0)
            return UserRegistrationStatus.USER_ALREADY_EXISTS;

        //Check password
        if(StringUtils.isBlank(password))
            return UserRegistrationStatus.PASSWORD_UNACCEPTABLE;

        //Checking password length
        if(password.length() < MIN_USER_PASSWORD_LENGTH || password.length() > MAX_USER_PASSWORD_LENGTH)
            return UserRegistrationStatus.PASSWORD_UNACCEPTABLE;

        if(passwordEntropy(password) < MIN_PASSWORD_ENTROPY)
            return UserRegistrationStatus.PASSWORD_UNACCEPTABLE;

        //Check password is confirmed
        if(!password.equals(confirm_passowrd))
            return UserRegistrationStatus.PASSWORD_CONFIRMATION_FAILED;
        
        //All checks passed
        userAccounts.addUser(username,passwordEncoder.encode(password));

        return UserRegistrationStatus.OK;
    }

    public void updateUserDescription(String new_description, String username){
        if(StringUtils.isBlank(new_description) || StringUtils.isBlank(username))
            return;

        new_description = new_description.trim();

        userProfiles.updateDescription(new_description, username);
    }

    public void deleteUser(String username){
        if(StringUtils.isBlank(username))
            return;

        userAccounts.deleteUser(username);
    }

    public Short getUserRatingForGame(String username, Integer game_id){
        if(StringUtils.isBlank(username) || game_id == null)
            return 0;

        Short rating = userRatings.getUserGameRating(username, game_id);

        return rating == null ? 0 : rating; 
    }

    public void setUserRatingForGame(String username, Integer game_id, Short rating){
        if(StringUtils.isBlank(username) || game_id == null || rating == null)
            return;
            
        userRatings.setUserGameRating(username, game_id, rating);
    }

    private double passwordEntropy(String password){
        int L = password.length();
        int R = 0;
        
        if(HAS_UPPERCASE_PATTERN.matcher(password).find())
            R += 26;

        if(HAS_LOWERCASE_PATTERN.matcher(password).find())
            R += 26;

        if(HAS_DIGIT_PATTERN.matcher(password).find())
            R += 10;
        
        if(HAS_SYMBOLS_PATTERN.matcher(password).find())
            R += 34;
        
        
        // E = L * log_2(R) if R != 0
        // log_2(R) = log_e(R) / log_e(2)  => changing log base
        
        return (R != 0) ? (L * (Math.log(R)/Math.log(2))) : 0;
    }

}//End of class
