package com.friendbook.notifications;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	long countByRecipient_UserIdAndIsRead(Long userId, boolean isRead);

	List<Notification> findByRecipient_UserIdAndIsRead(Long userId, boolean b);

	@Query("""
			    SELECT n
			    FROM Notification n
			    WHERE n.recipient.userId = :userId
			      AND (
			            n.isRead = true
			            OR n.createdAt >= :tenDaysAgo
			          )
			    ORDER BY n.createdAt DESC
			""")
	List<Notification> findAllNotifications(@Param("userId") Long userId,
			@Param("tenDaysAgo") LocalDateTime tenDaysAgo);

}
