package com.friendbook.user;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public void registerUser(UserRegisterRequestDTO requestDTO) {
		if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
			throw new RuntimeException("User Already Exists!");
		}
		User user = userMapper(requestDTO);
		userRepository.save(user);
	}

	private User userMapper(UserRegisterRequestDTO requestDTO) {
		User user = new User();
		user.setActiveStatus(true);
		user.setCreatedAt(LocalDate.now());
		user.setEmail(requestDTO.getEmail());
		user.setGender(requestDTO.getGender());
		user.setMobile(requestDTO.getMobile());
		user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
		user.setUserName(requestDTO.getUserName());
		user.setFirstName(requestDTO.getFirstName());
		user.setLastName(requestDTO.getLastName());
		return user;
	}
}
