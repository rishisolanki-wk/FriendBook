package com.friendbook.follow;

import org.springframework.data.jpa.repository.JpaRepository;

import com.friendbook.user.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	boolean existsByFollowerAndFollowing(User fromUser, User followingUser);

	long countByFollowing_UserId(Long userId);

	long countByFollower_UserId(Long userId);

}
