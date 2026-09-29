package com.friendbook.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.friendbook.follow.FollowRepository;
import com.friendbook.follow.FollowStatus;
import com.friendbook.posts.CloudinaryService;
import com.friendbook.posts.CloudinaryUploadResult;
import com.friendbook.posts.PostResponseDTO;
import com.friendbook.posts.PostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final PostService postService;
	private final CloudinaryService cloudinaryService;
	private final FollowRepository followRepository;

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
		user.setProfileBio("New Account!");
		user.setProfileImage("https://res.cloudinary.com/wcpxca8k/image/upload/v1790620003/default_user_img.avif");
		user.setImagePublicId("default_user_img");
		user.setAccountStatus(AccountStatus.PUBLIC);
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
		dto.setProfileBio(user.getProfileBio());
		dto.setProfileImage(user.getProfileImage());
		dto.setFollowers(followRepository.countByFollowing_UserIdAndStatus(user.getUserId(), FollowStatus.ACCEPTED));
		dto.setFollowings(followRepository.countByFollower_UserIdAndStatus(user.getUserId(), FollowStatus.ACCEPTED));
		return dto;
	}

	public UserFeedPostsDTO getFeedPosts(User user) {
		return postService.getFeedPosts(user.getUserId());
	}

	public void editProfile(UserProfileEditRequestDTO editRequestDTO, User user) {
		user = userRepository.findById(user.getUserId())
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
		if (editRequestDTO.getProfileBio() != null)
			user.setProfileBio(editRequestDTO.getProfileBio());
		if (!editRequestDTO.getAccountStatus().equals(user.getAccountStatus()))
			user.setAccountStatus(editRequestDTO.getAccountStatus());
		if (editRequestDTO.getProfileImage() != null) {
			CloudinaryUploadResult uploadResult = cloudinaryService.uploadImage(editRequestDTO.getProfileImage());
			user.setProfileImage(uploadResult.getImageUrl());
			user.setImagePublicId(uploadResult.getImagePublicId());
		}
		userRepository.save(user);
	}

	public List<UserProfileSearchResponseDTO> searchUsers(String keyword) {
		return userRepository.searchUsers(keyword).stream()
				.map(user -> new UserProfileSearchResponseDTO(user.getUserId(), user.getUserName(), user.getFirstName(),
						user.getLastName(), user.getProfileImage()))
				.toList();
	}

	public PublicProfileViewDTO getPublicProfileViewById(Long userId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
		long followerCount = followRepository.countByFollower_UserIdAndStatus(user.getUserId(), FollowStatus.ACCEPTED);
		long followingCount = followRepository.countByFollowing_UserIdAndStatus(user.getUserId(),
				FollowStatus.ACCEPTED);
		if (!user.isActiveStatus() || user.getAccountStatus() == AccountStatus.BLOCKED) {
			throw new IllegalArgumentException("Account unavailable");
		}

		List<PostResponseDTO> posts = new ArrayList<>();

		if (user.getAccountStatus() == AccountStatus.PUBLIC) {
			posts = postService.getPostByUserId(user.getUserId());
		} else {
			posts = null;

		}
		return new PublicProfileViewDTO(user.getUserId(), user.getUserName(), user.getFirstName(), user.getLastName(),
				user.getProfileImage(), user.getProfileBio(), user.isActiveStatus(), user.getAccountStatus(), posts,
				followingCount, followerCount);
	}
}
