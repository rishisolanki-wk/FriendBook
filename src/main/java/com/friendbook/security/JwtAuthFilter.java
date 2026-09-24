package com.friendbook.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.friendbook.auth.AuthUtils;
import com.friendbook.user.User;
import com.friendbook.user.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final UserRepository userRepository;
	private final AuthUtils authUtils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String requestTokenHeader = request.getHeader("Authorization");
		if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
			filterChain.doFilter(request, response);
			return;
		}
		String token = requestTokenHeader.split("Bearer ")[1];
		String userName = authUtils.getUserNameFromToken(token);

		if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			User user = userRepository.findByUserNameOrEmail(userName, userName)
					.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
					user, null, null);
			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
		}
		filterChain.doFilter(request, response);
	}

}
