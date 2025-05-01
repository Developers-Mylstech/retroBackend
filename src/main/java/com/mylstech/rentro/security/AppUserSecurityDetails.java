package com.mylstech.rentro.security;

import com.mylstech.rentro.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class AppUserSecurityDetails implements UserDetails {

    private final AppUser user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList ( new SimpleGrantedAuthority ( "ROLE_" + user.getRole ( ).name ( ) ) );
    }

    @Override
    public String getPassword() {
        return user.getPassword ( );
    }

    @Override
    public String getUsername() {
        return user.getEmail ( );
    }
}
