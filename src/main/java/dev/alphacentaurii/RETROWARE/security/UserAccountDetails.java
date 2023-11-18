package dev.alphacentaurii.RETROWARE.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import dev.alphacentaurii.RETROWARE.model.UserAccount;

public class UserAccountDetails implements UserDetails{

    private UserAccount userEntry;
    private Collection<SimpleGrantedAuthority> authorities;

    public UserAccountDetails(UserAccount userEntry, List<String> authorities){
        this.userEntry = userEntry;
        
        this.authorities = new ArrayList<SimpleGrantedAuthority>();
        for(String authority : authorities){
            this.authorities.add(new SimpleGrantedAuthority(authority));
        }

    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return userEntry.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntry.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userEntry.getEnabled();
    }
    
}
