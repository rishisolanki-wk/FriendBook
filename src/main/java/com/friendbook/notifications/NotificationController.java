package com.friendbook.notifications;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.user.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping("/unread-count")
	public ResponseEntity<Long> getUnreadNotificationCount(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		return ResponseEntity.status(HttpStatus.OK)
				.body(notificationService.getUnreadNotificationCount(user.getUserId()));
	}

	@GetMapping("/all-unread")
	public ResponseEntity<List<NotificationResponseDTO>> getAllNewUserNotifications(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		return ResponseEntity.status(HttpStatus.OK)
				.body(notificationService.getAllNewUserNotifications(user.getUserId()));
	}

	@GetMapping("/all")
	public ResponseEntity<List<NotificationResponseDTO>> getAllUserNotifications(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		return ResponseEntity.status(HttpStatus.OK).body(notificationService.getAllUserNotifications(user.getUserId()));
	}
}
