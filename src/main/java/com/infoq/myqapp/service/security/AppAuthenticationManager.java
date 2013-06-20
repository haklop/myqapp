package com.infoq.myqapp.service.security;

import com.infoq.myqapp.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppAuthenticationManager implements AuthenticationManager, UserDetailsService {

    @Resource
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (userService.isAuthorized((String) authentication.getPrincipal())) {
            return new PreAuthenticatedAuthenticationToken(authentication.getPrincipal(),
                    authentication.getCredentials(), getAuthorities((String) authentication.getPrincipal()));
        }
        throw new PreAuthenticatedCredentialsNotFoundException("Not authorized");
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String principal) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        List<String> userAuthorities = userService.get(principal).getAuthorities();
        if (userAuthorities != null) {
            for (String authority : userAuthorities) {
                authorities.add(new SimpleGrantedAuthority(authority));
            }
        }
        authorities.add(new SimpleGrantedAuthority("ANONYMOUS"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        return authorities;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new User(username, null, getAuthorities(username));
    }
}
