package com.elave.selfservicesupermarketdemo1.model.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shopping_sessions")
@Data
public class ShoppingSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "session_id", unique = true, nullable = false)
    private String sessionId;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "cabinet_id", nullable = false)
    private String cabinetId;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SessionStatus status = SessionStatus.ACTIVE;
    
    @Column(name = "video_url")
    private String videoUrl;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();
    
    @OneToMany(mappedBy = "shoppingSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;
    
    @OneToMany(mappedBy = "shoppingSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductRecognition> productRecognitions;
    
    public enum SessionStatus {
        ACTIVE, COMPLETED, SETTLED, CANCELLED
    }
    
    @PreUpdate
    public void preUpdate() {
        if (endTime != null && status == SessionStatus.ACTIVE) {
            status = SessionStatus.COMPLETED;
        }
    }
}