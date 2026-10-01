package com.friendbook.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByUserNameOrEmail(String username, String email);

	@Query("SELECT u FROM User u WHERE u.userName = :userName")
	Optional<User> findByUserName(String userName);

	@Query("""
			    SELECT u FROM User u
			    WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			       OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			       OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			""")
	List<User> searchUsers(@Param("keyword") String keyword);

	@Query("""
			    SELECT f.following.userId
			    FROM Follow f
			    WHERE f.follower.userId = :userId
			""")
	List<Long> findFollowingUserIds(Long userId);
}
