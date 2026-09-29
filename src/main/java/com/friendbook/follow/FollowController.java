package com.friendbook.follow;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.user.User;
import com.friendbook.user.UserProfileSearchResponseDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {

	private final FollowService followService;

	@PostMapping("/request/{followerId}")
	public ResponseEntity<String> followUserById(@PathVariable("followerId") Long toId, Authentication authentication) {
		User fromUser = (User) authentication.getPrincipal();
		return ResponseEntity.status(HttpStatus.OK).body(followService.followUserRequest(toId, fromUser));

	}

	@GetMapping("/followers")
	public ResponseEntity<List<UserProfileSearchResponseDTO>> getAllFollowers(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		return ResponseEntity.status(HttpStatus.OK).body(followService.getAllFollowersById(user.getUserId()));
	}

	@GetMapping("/followings")
	public ResponseEntity<List<UserProfileSearchResponseDTO>> getAllFollowings(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		return ResponseEntity.status(HttpStatus.OK).body(followService.getAllFollowingsById(user.getUserId()));
	}

	@DeleteMapping("/unfollow/{userId}")
	public ResponseEntity<String> unfollowUserById(@PathVariable("userId") Long toId, Authentication authentication) {
		User fromUser = (User) authentication.getPrincipal();
		return ResponseEntity.status(HttpStatus.OK).body(followService.unfollowUserById(toId, fromUser));

	}

	@GetMapping("/requests/pending")
	public ResponseEntity<List<UserProfileSearchResponseDTO>> getAllPendingFollowerRequest(
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		return ResponseEntity.status(HttpStatus.OK).body(followService.getPendingRequests(user.getUserId()));
	}

	@PostMapping("/requests/{followerId}/accept")
	public ResponseEntity<String> acceptPendingRequest(@PathVariable("followerId") Long requestId,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		followService.markStatusToFollowRequest(requestId, user, true);
		return ResponseEntity.status(HttpStatus.OK).body("Request Accepted!");
	}

	@PostMapping("/requests/{followerId}/reject")
	public ResponseEntity<String> rejectPendingRequest(@PathVariable("followerId") Long requestId,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		followService.markStatusToFollowRequest(requestId, user, false);
		return ResponseEntity.status(HttpStatus.OK).body("Request Rejected!");
	}

}
