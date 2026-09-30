package com.friendbook.notifications;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.friendbook.posts.Post;
import com.friendbook.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;

	public Long getUnreadNotificationCount(Long userId) {
		return notificationRepository.countByRecipient_UserIdAndIsRead(userId, false);
	}

	public List<NotificationResponseDTO> getAllNewUserNotifications(Long userId) {
		List<Notification> notifications = notificationRepository.findByRecipient_UserIdAndIsRead(userId, false);
		return notificationResponseMapper(notifications);
	}

	private List<NotificationResponseDTO> notificationResponseMapper(List<Notification> notifications) {
		List<NotificationResponseDTO> respDtos = new ArrayList<>();
		for (Notification notification : notifications) {
			notification.setRead(true);
			Long postId = notification.getPost() != null ? notification.getPost().getPostId() : null;
			NotificationResponseDTO dto = new NotificationResponseDTO(notification.getNotificationId(),
					notification.getActor().getUserId(), notification.getActor().getUserName(), notification.getType(),
					postId, notification.isRead(), notification.getCreatedAt());
			respDtos.add(dto);
		}
		return respDtos;
	}

	public void createNotification(User recipient, User actor, NotificationType type, Post post) {
		Notification notification = new Notification();
		notification.setRecipient(recipient);
		notification.setActor(actor);
		notification.setType(type);
		notification.setPost(post);
		notification.setRead(false);
		notification.setCreatedAt(LocalDateTime.now());
		notificationRepository.save(notification);
	}

	public List<NotificationResponseDTO> getAllUserNotifications(Long userId) {
		List<Notification> notifications = notificationRepository.findAllNotifications(userId,
				LocalDateTime.now().minusDays(10));
		return notificationResponseMapper(notifications);
	}
}
