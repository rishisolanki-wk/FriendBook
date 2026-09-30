package com.friendbook.notifications;

import java.time.LocalDateTime;

import com.friendbook.posts.Post;
import com.friendbook.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "notifications")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long notificationId;

	@ManyToOne
	private User recipient;

	@ManyToOne
	private User actor;

	@Enumerated(EnumType.STRING)
	private NotificationType type;

	@ManyToOne
	private Post post;

	private boolean isRead;

	private LocalDateTime createdAt;
}
