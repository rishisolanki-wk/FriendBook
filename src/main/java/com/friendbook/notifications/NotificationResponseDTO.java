package com.friendbook.notifications;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationResponseDTO {

	private Long notificationId;

	private Long actorId;
	private String actorUserName;

	private NotificationType type;

	private Long postId;

	private boolean isRead;

	private LocalDateTime createdAt;
}