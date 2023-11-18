package dev.alphacentaurii.RETROWARE.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.alphacentaurii.RETROWARE.dao.UserAccountDAO;
import dev.alphacentaurii.RETROWARE.model.UserAccount;

@Service
public class DaoUserDetailsService implements UserDetailsService {

    private final UserAccountDAO userAccounts;

    @Autowired
    public DaoUserDetailsService(UserAccountDAO userAccounts){
        this.userAccounts = userAccounts;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = userAccounts.getUserByUsername(username);
        
        if(account == null)
            throw new UsernameNotFoundException(String.format("User \'%s\' not found.", username));

        List<String> userAuthorities = userAccounts.getUserAuthorities(username);

        return new UserAccountDetails(account, userAuthorities);
    }


}//End of class
