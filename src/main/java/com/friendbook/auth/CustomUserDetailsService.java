package com.friendbook.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.friendbook.user.User;
import com.friendbook.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String userNameOrEmail) throws UsernameNotFoundException {
		User user = userRepository.findByUserNameOrEmail(userNameOrEmail, userNameOrEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found!"));
		return new CustomUserPrincipal(user);
	}
}
