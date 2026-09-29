package com.friendbook.follow;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.friendbook.user.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	boolean existsByFollowerAndFollowing(User fromUser, User followingUser);

	long countByFollowing_UserIdAndStatus(Long userId, FollowStatus status);

	long countByFollower_UserIdAndStatus(Long userId, FollowStatus status);

	boolean existsByFollower_UserIdAndFollowing_UserId(Long followerId, Long followingId);

	List<Follow> findByFollowing_UserId(Long userId);

	List<Follow> findByFollower_UserId(Long userId);

	void deleteByFollower_UserIdAndFollowing_UserId(Long followerId, Long followingId);

	List<Follow> findByFollowing_UserIdAndStatus(Long userId, FollowStatus status);

	Optional<Follow> findByFollower_UserIdAndFollowing_UserIdAndStatus(Long followerId, Long followingId,
			FollowStatus status);
}
