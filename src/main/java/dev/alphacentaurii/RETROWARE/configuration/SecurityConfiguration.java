package dev.alphacentaurii.RETROWARE.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import dev.alphacentaurii.RETROWARE.security.DaoUserDetailsService;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration{


    @Autowired
    private DaoUserDetailsService userDetailsService;

    @Bean
    public AuthenticationProvider authProvider(){
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
         return new BCryptPasswordEncoder(10);
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector){
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception{
        http
            
            .headers( headers -> headers 
                .frameOptions(frameOptions -> frameOptions
                    .sameOrigin()
                )
            )
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers(
                    AntPathRequestMatcher.antMatcher("/h2-console/**"),
                    mvc.pattern("/games/**"),
                    mvc.pattern("/media/**"),
                    mvc.pattern("/scripts/**"),
                    mvc.pattern("/styles/**"),
                    mvc.pattern("/license.txt"),
                    mvc.pattern("/*"),
                    mvc.pattern("/profile_picture/*")
                    ).permitAll()
                .anyRequest().authenticated()
            )
            .csrf( c -> c
                .ignoringRequestMatchers(
                    AntPathRequestMatcher.antMatcher("/h2-console/**")
                )
            )
            .formLogin((form) -> form 
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/login-success", true)
                .failureUrl("/login?failed=1").permitAll()
            )
            .logout((logout) -> logout
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(AntPathRequestMatcher.antMatcher("/logout"))
                .logoutSuccessUrl("/home")
            );

        return http.build();
    }
    
}//End of class
