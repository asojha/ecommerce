package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "processed_notifications",
       uniqueConstraints = @UniqueConstraint(columnNames = {"store", "notification_id"}))
@Data
@NoArgsConstructor
public class ProcessedNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ProductStoreMapping.Store store;

    @Column(name = "notification_id", nullable = false, length = 200)
    private String notificationId;

    @Column(length = 50)
    private String notificationType;

    @Column(nullable = false)
    private Instant processedAt;
}
