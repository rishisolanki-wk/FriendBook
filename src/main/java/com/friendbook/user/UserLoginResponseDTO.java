package com.friendbook.user;

import lombok.Data;

@Data
public class UserLoginResponseDTO {

	private String Jwt;
	private String name;
	private String email;

}
