package com.friendbook.follow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.friendbook.exception.InvalidException;
import com.friendbook.exception.ResourceAlreadyExistsException;
import com.friendbook.exception.ResourceNotFoundException;
import com.friendbook.exception.UserNotFoundException;
import com.friendbook.notifications.NotificationService;
import com.friendbook.notifications.NotificationType;
import com.friendbook.user.AccountStatus;
import com.friendbook.user.User;
import com.friendbook.user.UserProfileSearchResponseDTO;
import com.friendbook.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

	@Mock
	private FollowRepository followRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private FollowService followService;

	@Test
	void followUserRequest_shouldAcceptFollow_whenTargetIsPublic() {

		Long targetId = 2L;

		User fromUser = new User();
		fromUser.setUserId(1L);

		User targetUser = new User();
		targetUser.setUserId(targetId);
		targetUser.setAccountStatus(AccountStatus.PUBLIC);

		when(userRepository.findById(targetId)).thenReturn(Optional.of(targetUser));

		when(followRepository.existsByFollower_UserIdAndFollowing_UserId(1L, targetId)).thenReturn(false);

		String result = followService.followUserRequest(targetId, fromUser);

		assertEquals("Followed successfully", result);

		verify(followRepository).save(any(Follow.class));

		verify(notificationService).createNotification(targetUser, fromUser, NotificationType.FOLLOW, null);
	}

	@Test
	void followUserRequest_shouldCreatePendingRequest_whenTargetIsPrivate() {

		Long targetId = 2L;

		User fromUser = new User();
		fromUser.setUserId(1L);

		User targetUser = new User();
		targetUser.setUserId(targetId);
		targetUser.setAccountStatus(AccountStatus.PRIVATE);

		when(userRepository.findById(targetId)).thenReturn(Optional.of(targetUser));

		when(followRepository.existsByFollower_UserIdAndFollowing_UserId(1L, targetId)).thenReturn(false);

		String result = followService.followUserRequest(targetId, fromUser);

		assertEquals("Follow request sent", result);

		verify(followRepository).save(any(Follow.class));

		verify(notificationService).createNotification(targetUser, fromUser, NotificationType.FOLLOW_REQUEST, null);
	}

	@Test
	void followUserRequest_shouldSaveAcceptedFollow_whenTargetIsPublic() {

		Long targetId = 2L;

		User fromUser = new User();
		fromUser.setUserId(1L);

		User targetUser = new User();
		targetUser.setUserId(targetId);
		targetUser.setAccountStatus(AccountStatus.PUBLIC);

		when(userRepository.findById(targetId)).thenReturn(Optional.of(targetUser));

		when(followRepository.existsByFollower_UserIdAndFollowing_UserId(1L, targetId)).thenReturn(false);

		followService.followUserRequest(targetId, fromUser);

		ArgumentCaptor<Follow> captor = ArgumentCaptor.forClass(Follow.class);

		verify(followRepository).save(captor.capture());

		Follow savedFollow = captor.getValue();

		assertEquals(fromUser, savedFollow.getFollower());
		assertEquals(targetUser, savedFollow.getFollowing());
		assertEquals(FollowStatus.ACCEPTED, savedFollow.getStatus());
	}

	@Test
	void followUserRequest_shouldThrowException_whenUserFollowsHimself() {

		Long userId = 1L;

		User user = new User();
		user.setUserId(userId);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		assertThrows(InvalidException.class, () -> followService.followUserRequest(userId, user));

		verify(followRepository, never()).save(any(Follow.class));

		verify(notificationService, never()).createNotification(any(), any(), any(), any());
	}

	@Test
	void followUserRequest_shouldThrowException_whenAlreadyFollowing() {

		Long targetId = 2L;

		User fromUser = new User();
		fromUser.setUserId(1L);

		User targetUser = new User();
		targetUser.setUserId(targetId);
		targetUser.setAccountStatus(AccountStatus.PUBLIC);

		when(userRepository.findById(targetId)).thenReturn(Optional.of(targetUser));

		when(followRepository.existsByFollower_UserIdAndFollowing_UserId(1L, targetId)).thenReturn(true);

		assertThrows(ResourceAlreadyExistsException.class, () -> followService.followUserRequest(targetId, fromUser));

		verify(followRepository, never()).save(any(Follow.class));

		verify(notificationService, never()).createNotification(any(), any(), any(), any());
	}

	@Test
	void followUserRequest_shouldThrowException_whenTargetUserDoesNotExist() {

		Long targetId = 2L;

		User fromUser = new User();
		fromUser.setUserId(1L);

		when(userRepository.findById(targetId)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> followService.followUserRequest(targetId, fromUser));

		verify(followRepository, never()).save(any(Follow.class));

		verify(notificationService, never()).createNotification(any(), any(), any(), any());
	}

	@Test
	void getAllFollowersById_shouldReturnFollowers() {

		Long userId = 1L;

		User follower = new User();
		follower.setUserId(2L);
		follower.setUserName("john");
		follower.setFirstName("John");
		follower.setLastName("Doe");
		follower.setProfileImage("john.jpg");

		Follow follow = new Follow();
		follow.setFollower(follower);

		when(followRepository.findByFollowing_UserId(userId)).thenReturn(List.of(follow));

		List<UserProfileSearchResponseDTO> result = followService.getAllFollowersById(userId);

		assertEquals(1, result.size());

		UserProfileSearchResponseDTO dto = result.get(0);

		assertEquals(2L, dto.getUserId());
		assertEquals("john", dto.getUserName());
		assertEquals("John", dto.getFirstName());
		assertEquals("Doe", dto.getLastName());
		assertEquals("john.jpg", dto.getProfileImage());

		verify(followRepository).findByFollowing_UserId(userId);
	}

	@Test
	void getAllFollowingsById_shouldReturnFollowings() {

		Long userId = 1L;

		User following = new User();
		following.setUserId(3L);
		following.setUserName("alex");
		following.setFirstName("Alex");
		following.setLastName("Smith");
		following.setProfileImage("alex.jpg");

		Follow follow = new Follow();
		follow.setFollowing(following);

		when(followRepository.findByFollower_UserId(userId)).thenReturn(List.of(follow));

		List<UserProfileSearchResponseDTO> result = followService.getAllFollowingsById(userId);

		assertEquals(1, result.size());

		assertEquals(3L, result.get(0).getUserId());
		assertEquals("alex", result.get(0).getUserName());

		verify(followRepository).findByFollower_UserId(userId);
	}

	@Test
	void unfollowUserById_shouldDeleteFollowRelationship() {

		Long targetId = 2L;

		User fromUser = new User();
		fromUser.setUserId(1L);

		User targetUser = new User();
		targetUser.setUserId(targetId);

		when(userRepository.findById(targetId)).thenReturn(Optional.of(targetUser));

		String result = followService.unfollowUserById(targetId, fromUser);

		assertEquals("Unfollowed", result);

		verify(followRepository).deleteByFollower_UserIdAndFollowing_UserId(1L, targetId);
	}

	@Test
	void unfollowUserById_shouldThrowException_whenTargetUserDoesNotExist() {

		Long targetId = 2L;

		User fromUser = new User();
		fromUser.setUserId(1L);

		when(userRepository.findById(targetId)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> followService.unfollowUserById(targetId, fromUser));

		verify(followRepository, never()).deleteByFollower_UserIdAndFollowing_UserId(anyLong(), anyLong());
	}

	@Test
	void getPendingRequests_shouldReturnPendingFollowers() {

		Long userId = 1L;

		User follower = new User();
		follower.setUserId(2L);
		follower.setUserName("john");
		follower.setFirstName("John");
		follower.setLastName("Doe");

		Follow follow = new Follow();
		follow.setFollower(follower);
		follow.setStatus(FollowStatus.PENDING);

		when(followRepository.findByFollowing_UserIdAndStatus(userId, FollowStatus.PENDING))
				.thenReturn(List.of(follow));

		List<UserProfileSearchResponseDTO> result = followService.getPendingRequests(userId);

		assertEquals(1, result.size());
		assertEquals(2L, result.get(0).getUserId());
		assertEquals("john", result.get(0).getUserName());

		verify(followRepository).findByFollowing_UserIdAndStatus(userId, FollowStatus.PENDING);
	}

	@Test
	void markStatusToFollowRequest_shouldAcceptRequest_whenIsAcceptedIsTrue() {

		Long followerId = 2L;
		Long currentUserId = 1L;

		User currentUser = new User();
		currentUser.setUserId(currentUserId);

		User follower = new User();
		follower.setUserId(followerId);

		Follow follow = new Follow();
		follow.setFollower(follower);
		follow.setFollowing(currentUser);
		follow.setStatus(FollowStatus.PENDING);

		when(followRepository.findByFollower_UserIdAndFollowing_UserIdAndStatus(followerId, currentUserId,
				FollowStatus.PENDING)).thenReturn(Optional.of(follow));

		when(userRepository.findById(followerId)).thenReturn(Optional.of(follower));

		String result = followService.markStatusToFollowRequest(followerId, currentUser, true);

		assertEquals("Follow request accepted", result);
		assertEquals(FollowStatus.ACCEPTED, follow.getStatus());

		verify(notificationService).createNotification(follower, currentUser, NotificationType.FOLLOW_ACCEPTED, null);
	}

	@Test
	void markStatusToFollowRequest_shouldRejectRequest_whenIsAcceptedIsFalse() {

		Long followerId = 2L;
		Long currentUserId = 1L;

		User currentUser = new User();
		currentUser.setUserId(currentUserId);

		User follower = new User();
		follower.setUserId(followerId);

		Follow follow = new Follow();
		follow.setStatus(FollowStatus.PENDING);

		when(followRepository.findByFollower_UserIdAndFollowing_UserIdAndStatus(followerId, currentUserId,
				FollowStatus.PENDING)).thenReturn(Optional.of(follow));

		when(userRepository.findById(followerId)).thenReturn(Optional.of(follower));

		String result = followService.markStatusToFollowRequest(followerId, currentUser, false);

		assertEquals("Follow request rejected", result);
		assertEquals(FollowStatus.REJECTED, follow.getStatus());

		verify(notificationService, never()).createNotification(any(), any(), any(), any());
	}

	@Test
	void markStatusToFollowRequest_shouldThrowException_whenPendingRequestDoesNotExist() {

		Long followerId = 2L;

		User currentUser = new User();
		currentUser.setUserId(1L);

		when(followRepository.findByFollower_UserIdAndFollowing_UserIdAndStatus(followerId, 1L, FollowStatus.PENDING))
				.thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> followService.markStatusToFollowRequest(followerId, currentUser, true));

		verify(userRepository, never()).findById(anyLong());

		verify(notificationService, never()).createNotification(any(), any(), any(), any());
	}

	@Test
	void markStatusToFollowRequest_shouldThrowException_whenFollowerUserDoesNotExist() {

		Long followerId = 2L;

		User currentUser = new User();
		currentUser.setUserId(1L);

		Follow follow = new Follow();
		follow.setStatus(FollowStatus.PENDING);

		when(followRepository.findByFollower_UserIdAndFollowing_UserIdAndStatus(followerId, 1L, FollowStatus.PENDING))
				.thenReturn(Optional.of(follow));

		when(userRepository.findById(followerId)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class,
				() -> followService.markStatusToFollowRequest(followerId, currentUser, true));

		verify(notificationService, never()).createNotification(any(), any(), any(), any());
	}
}