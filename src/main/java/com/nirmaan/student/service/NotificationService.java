package com.nirmaan.student.service;

import com.nirmaan.student.entity.Notification;
import com.nirmaan.student.entity.User;
import com.nirmaan.student.repository.NotificationRepository;
import com.nirmaan.student.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	public Notification createNotification(Long userId, String title, String message, String type) {
		User user = userRepository.findById(userId).orElse(null);
		if (user == null)
			return null;

		Notification notification = new Notification();
		notification.setUser(user);
		notification.setTitle(title);
		notification.setMessage(message);
		notification.setType(type);
		notification.setCreatedAt(LocalDateTime.now());

		return notificationRepository.save(notification);
	}

	public List<Notification> getUserNotifications(Long userId) {
		User user = userRepository.findById(userId).orElse(null);
		if (user == null)
			return List.of();

		return notificationRepository.findByUserOrderByCreatedAtDesc(user);
	}

	public void markAsRead(Long notificationId) {
		notificationRepository.findById(notificationId).ifPresent(notification -> {
			notification.setRead(true);
			notificationRepository.save(notification);
		});
	}
}
