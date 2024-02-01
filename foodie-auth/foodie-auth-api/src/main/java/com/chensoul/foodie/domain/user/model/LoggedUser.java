package com.chensoul.foodie.domain.user.model;

import com.chensoul.foodie.domain.user.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 登录认证对象
 */
@Getter
@Setter
public class LoggedUser extends User implements UserDetails, CredentialsContainer {
    private static final long serialVersionUID = 2893861964284628170L;
    private List<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (StringUtils.isNotBlank(getRoles())) {
            authorities = Stream.of(getRoles().split(",")).map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());
        } else {
            authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
        }
        return authorities;
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
        return true;
    }

    @Override
    public void eraseCredentials() {
        setPassword(null);
    }
}
