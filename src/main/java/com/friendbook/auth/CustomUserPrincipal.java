package com.friendbook.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.friendbook.user.User;

public class CustomUserPrincipal implements UserDetails {

	private final User user;

	public CustomUserPrincipal(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}
}