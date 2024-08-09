package com.exmple.queueservice.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exmple.queueservice.database.entity.QueueRequest;
public interface QueueRequestRepository extends JpaRepository<QueueRequest, Long> {
    QueueRequest findByUserId(String userId);
}
