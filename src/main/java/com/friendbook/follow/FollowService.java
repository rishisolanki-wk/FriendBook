package com.friendbook.follow;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.friendbook.user.AccountStatus;
import com.friendbook.user.User;
import com.friendbook.user.UserProfileSearchResponseDTO;
import com.friendbook.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;

	@Transactional
	public String followUserRequest(Long toId, User fromUser) {

		User followingUser = userRepository.findById(toId)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

		if (fromUser.getUserId().equals(toId)) {
			throw new RuntimeException("Cannot follow yourself");
		}

		if (followRepository.existsByFollower_UserIdAndFollowing_UserId(fromUser.getUserId(), toId)) {
			throw new RuntimeException("Already followed or requested");
		}

		Follow follow = new Follow();
		follow.setFollower(fromUser);
		follow.setFollowing(followingUser);
		follow.setCreatedAt(LocalDateTime.now());

		if (followingUser.getAccountStatus() == AccountStatus.PRIVATE) {
			follow.setStatus(FollowStatus.PENDING);
			// Create notification for followingUser
		} else {
			follow.setStatus(FollowStatus.ACCEPTED);
		}

		followRepository.save(follow);

		return followingUser.getAccountStatus() == AccountStatus.PRIVATE ? "Follow request sent"
				: "Followed successfully";
	}

	public List<UserProfileSearchResponseDTO> getAllFollowersById(Long userId) {
		List<Follow> follows = followRepository.findByFollowing_UserId(userId);
		return follows.stream().map(follow -> {
			User follower = follow.getFollower();

			UserProfileSearchResponseDTO dto = new UserProfileSearchResponseDTO(follower.getUserId(),
					follower.getUserName(), follower.getFirstName(), follower.getLastName(),
					follower.getProfileImage());
			return dto;
		}).toList();
	}

	public List<UserProfileSearchResponseDTO> getAllFollowingsById(Long userId) {
		List<Follow> follows = followRepository.findByFollower_UserId(userId);
		return follows.stream().map(follow -> {
			User follower = follow.getFollowing();

			UserProfileSearchResponseDTO dto = new UserProfileSearchResponseDTO(follower.getUserId(),
					follower.getUserName(), follower.getFirstName(), follower.getLastName(),
					follower.getProfileImage());
			return dto;
		}).toList();
	}

	@Transactional
	public String unfollowUserById(Long toId, User fromUser) {
		User followingUser = userRepository.findById(toId)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
		followRepository.deleteByFollower_UserIdAndFollowing_UserId(fromUser.getUserId(), followingUser.getUserId());
		return "Unfollowed";
	}

	public List<UserProfileSearchResponseDTO> getPendingRequests(Long userId) {

		List<Follow> requests = followRepository.findByFollowing_UserIdAndStatus(userId, FollowStatus.PENDING);

		return requests.stream().map(follow -> {
			User follower = follow.getFollower();

			return new UserProfileSearchResponseDTO(follower.getUserId(), follower.getUserName(),
					follower.getFirstName(), follower.getLastName(), follower.getProfileImage());
		}).toList();
	}

	@Transactional
	public String markStatusToFollowRequest(Long followerId, User currentUser, boolean isAccepted) {

		Follow follow = followRepository.findByFollower_UserIdAndFollowing_UserIdAndStatus(followerId,
				currentUser.getUserId(), FollowStatus.PENDING)
				.orElseThrow(() -> new RuntimeException("Pending request not found"));

		if (isAccepted) {
			follow.setStatus(FollowStatus.ACCEPTED);
			return "Follow request accepted";
		}

		follow.setStatus(FollowStatus.REJECTED);
		return "Follow request rejected";
	}

}
