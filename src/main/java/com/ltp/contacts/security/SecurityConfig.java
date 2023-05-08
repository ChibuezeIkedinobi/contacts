package com.ltp.contacts.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@AllArgsConstructor
@Configuration     // source for bean definition
public class SecurityConfig {

    private BCryptPasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()   // authorize all http requests
                .antMatchers(HttpMethod.DELETE, "/delete/{id}/contact").hasRole("ADMIN")  // authorization rules. user has no authority to delete
                .antMatchers(HttpMethod.POST).hasAnyRole("ADMIN", "USER")    // authorization rules. user and admin have the authority to POST request(create)
                .anyRequest().authenticated()    // any requests need to be authenticated
                .and()
                .httpBasic()     // authenticate these requests using basic authentication
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);   //avoids the session creation based authentication based on cookies

        return http.build();    // return the security filter chain
    }

    @Bean
    public UserDetailsService users() {      // contains User Details in order to authenticate users

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin-pass"))
                .roles("ADMIN")   // for authorization purposes
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user-pass"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);   // storing the users in memory
    }
}
