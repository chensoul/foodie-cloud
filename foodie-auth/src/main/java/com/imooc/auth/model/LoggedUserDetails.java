package com.imooc.auth.model;

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
public class LoggedUserDetails implements UserDetails, CredentialsContainer {

	private static final long serialVersionUID = 2893861964284628170L;
	private Long id;
	private String username;
	private String nickname;
	private String password;
	private String phone;
	private String email;
	private String avatar;
	private String roles;
	private List<GrantedAuthority> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (StringUtils.isNotBlank(this.roles)) {
			this.authorities = Stream.of(this.roles.split(",")).map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());
		} else {
			this.authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
		}
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
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
		this.password = null;
	}
}
