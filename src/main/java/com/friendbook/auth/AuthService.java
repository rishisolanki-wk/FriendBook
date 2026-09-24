package com.friendbook.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.friendbook.user.User;
import com.friendbook.user.UserLoginRequestDTO;
import com.friendbook.user.UserLoginResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final AuthUtils authUtils;

	public UserLoginResponseDTO loginRequest(UserLoginRequestDTO requestDTO) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(requestDTO.getUserNameOrEmail(), requestDTO.getPassword()));
		CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

		User user = principal.getUser();

		String token = authUtils.generateAccessToken(user);
		UserLoginResponseDTO responseDTO = new UserLoginResponseDTO();
		responseDTO.setJwt(token);
		responseDTO.setEmail(user.getEmail());
		responseDTO.setName(user.getUserName());
		return responseDTO;
	}

}
