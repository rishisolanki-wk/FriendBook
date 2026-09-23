package com.friendbook.user;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Data
@Getter
@Setter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@NotNull
	@Column(unique = true, nullable = false)
	private String userName;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@NotNull
	@Email
	@Column(unique = true, nullable = false)
	private String email;

	@NotNull
	@Column(unique = true, nullable = false)
	private String mobile;

	@NotNull
	private String password;

	@NotNull
	private Gender gender;

	private LocalDate createdAt;

	private boolean activeStatus;
}
