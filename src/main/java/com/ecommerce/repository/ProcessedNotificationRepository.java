package com.ecommerce.repository;

import com.ecommerce.model.ProcessedNotification;
import com.ecommerce.model.ProductStoreMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcessedNotificationRepository extends JpaRepository<ProcessedNotification, Long> {

    Optional<ProcessedNotification> findByStoreAndNotificationId(
            ProductStoreMapping.Store store, String notificationId);
}
