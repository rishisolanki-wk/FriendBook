package com.friendbook.posts;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
	Optional<List<Post>> findByUser_UserId(Long id);

	Page<Post> findByUser_UserIdIn(List<Long> userIds, Pageable pageable);
}
