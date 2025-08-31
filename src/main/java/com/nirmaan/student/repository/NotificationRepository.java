package com.nirmaan.student.repository;

import com.nirmaan.student.entity.Notification;
import com.nirmaan.student.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByUserOrderByCreatedAtDesc(User user);

	List<Notification> findByUserAndReadFalse(User user);
}
