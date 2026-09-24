package com.friendbook.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDTO {
	@NotBlank
	private String userNameOrEmail;
	@NotBlank
	private String password;
}
