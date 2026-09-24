package com.friendbook.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.user.UserLoginRequestDTO;
import com.friendbook.user.UserLoginResponseDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<UserLoginResponseDTO> loginUser(@RequestBody UserLoginRequestDTO requestDTO) {
		UserLoginResponseDTO responseDTO = authService.loginRequest(requestDTO);
		if (responseDTO == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
	}
}
