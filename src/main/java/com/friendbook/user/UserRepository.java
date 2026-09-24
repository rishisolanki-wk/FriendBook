package com.friendbook.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByUserNameOrEmail(String username, String email);

	Optional<User> findByUserName(String userName);
}
