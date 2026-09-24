package com.friendbook.user;

import java.time.LocalDate;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.friendbook.posts.PostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final PostService postService;

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

	public UserProfileResponseDTO getUserByUserName(String userName) {
		User user = userRepository.findByUserName(userName)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
		UserProfileResponseDTO dto = new UserProfileResponseDTO();
		dto.setEmail(user.getEmail());
		dto.setGender(user.getGender());
		dto.setMobile(user.getMobile());
		dto.setUserName(user.getUserName());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setCreatedAt(user.getCreatedAt());
		dto.setPosts(postService.getPostByUserId(user.getUserId()));
		return dto;
	}
}
