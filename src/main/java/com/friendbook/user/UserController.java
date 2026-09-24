package com.friendbook.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegisterRequestDTO requestDTO) {
		try {
			userService.registerUser(requestDTO);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User not registered : " + ex.getMessage());
		}
		return ResponseEntity.status(HttpStatus.CREATED).body("Registered Successfuly");
	}

	@GetMapping("/profile")
	public ResponseEntity<UserProfileResponseDTO> getProfile(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		UserProfileResponseDTO userProfileResponseDTO = userService.getUserByUserName(user.getUserName());
		if (userProfileResponseDTO == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		return ResponseEntity.status(HttpStatus.OK).body(userProfileResponseDTO);
	}
}
