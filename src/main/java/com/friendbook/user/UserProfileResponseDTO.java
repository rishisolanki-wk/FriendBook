package com.friendbook.user;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserProfileResponseDTO {

	private String userName;
	private String firstName;
	private String lastName;
	private String email;
	private String mobile;
	private Gender gender;
	private LocalDate createdAt;
}
