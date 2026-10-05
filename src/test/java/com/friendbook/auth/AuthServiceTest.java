package com.friendbook.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.friendbook.exception.InvalidException;
import com.friendbook.user.User;
import com.friendbook.user.UserLoginRequestDTO;
import com.friendbook.user.UserLoginResponseDTO;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private AuthUtils authUtils;

	@InjectMocks
	private AuthService authService;

	@Test
	void loginRequest_shouldReturnLoginResponse_whenCredentialsAreValid() {

		// arrangement :- In this we have to arrange everything that is required for
		// testing like creating or injecting dependencies for method calling.
		UserLoginRequestDTO loginRequestDTO = new UserLoginRequestDTO();
		loginRequestDTO.setUserNameOrEmail("rishi");
		loginRequestDTO.setPassword("1234");

		User user = new User();
		user.setUserId(1L);
		user.setUserName("rishi");
		user.setEmail("rishi@gmail.com");

		CustomUserPrincipal customUserPrincipal = new CustomUserPrincipal(user);

		Authentication authentication = mock(Authentication.class);

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);

		when(authentication.getPrincipal()).thenReturn(customUserPrincipal);

		when(authUtils.generateAccessToken(user)).thenReturn("MOCK-JWT-TOKEN");

		// Action :- testing, just performing actual test
		UserLoginResponseDTO loginResponseDTO = authService.loginRequest(loginRequestDTO);

		// Assert :- Checking or verifying the actual test result.

		assertNotNull(loginResponseDTO);
		assertEquals("MOCK-JWT-TOKEN", loginResponseDTO.getJwt());
		assertEquals("rishi@gmail.com", loginResponseDTO.getEmail());
		assertEquals("rishi", loginResponseDTO.getName());

		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

		verify(authUtils).generateAccessToken(user);

	}

	@Test
	void loginRequest_shouldThrowException_whenCredentialsAreInvalid() {

		UserLoginRequestDTO loginRequestDTO = new UserLoginRequestDTO();
		loginRequestDTO.setUserNameOrEmail("rishi");
		loginRequestDTO.setPassword("wrongPass");

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(new BadCredentialsException("Invalid credentials"));

		assertThrows(BadCredentialsException.class, () -> authService.loginRequest(loginRequestDTO));

		verify(authUtils, never()).generateAccessToken(any());

	}

	@Test
	void loginRequest_shouldThrowException_whenRequestIsNull() {

		assertThrows(InvalidException.class, () -> authService.loginRequest(null));
	}

	@Test
	void loginRequest_shouldThrowException_whenCredentialsAreNull() {

		UserLoginRequestDTO requestDTO = new UserLoginRequestDTO();

		requestDTO.setUserNameOrEmail(null);
		requestDTO.setPassword(null);

		assertThrows(InvalidException.class, () -> authService.loginRequest(requestDTO));
	}
}
