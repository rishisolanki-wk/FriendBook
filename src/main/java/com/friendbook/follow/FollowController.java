package com.friendbook.follow;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.user.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {

	private final FollowService followService;

	@PostMapping("/request/{followerId}")
	public void followUserById(@PathVariable("followerId") Long toId, Authentication authentication) {
		User fromUser = (User) authentication.getPrincipal();
		followService.followUserRequest(toId, fromUser);

	}
}
