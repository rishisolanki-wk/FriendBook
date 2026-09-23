package com.friendbook.user;

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
	public String registerUser(@Valid @RequestBody UserRegisterRequestDTO requestDTO) {
		try {
			userService.registerUser(requestDTO);
		} catch (Exception ex) {
			return ex.getMessage();
		}
		return "Registered Successfuly";
	}
}
