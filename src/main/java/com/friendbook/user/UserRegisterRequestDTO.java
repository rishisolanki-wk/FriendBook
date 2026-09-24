package com.friendbook.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegisterRequestDTO {

	@NotBlank
	private String userName;

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String mobile;

	@NotBlank
	private String password;

	@NotNull
	private Gender gender;

}
