package com.friendbook.follow;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.friendbook.user.User;
import com.friendbook.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;

	public void followUserRequest(Long toId, User fromUser) {
		User followingUser = userRepository.findById(toId)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
		if (fromUser.getUserId().equals(toId)) {
			throw new IllegalArgumentException("You cannot follow yourself");
		}

		if (followRepository.existsByFollowerAndFollowing(fromUser, followingUser)) {
			throw new IllegalArgumentException("Already following this user");
		}
		Follow follow = new Follow();
		follow.setFollower(fromUser);
		follow.setFollowing(followingUser);
		follow.setCreatedAt(LocalDateTime.now());
		followRepository.save(follow);
	}

}
