package com.elave.selfservicesupermarketdemo1.model.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id", insertable = false, updatable = false)
    private ShoppingSession shoppingSession;
    
    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "payment_order_no")
    private String paymentOrderNo;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;
    
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
    
    @Column(name = "update_time")
    private LocalDateTime updateTime = LocalDateTime.now();
    
    public enum TransactionType {
        PAYMENT, REFUND, ADJUSTMENT
    }
    
    public enum TransactionStatus {
        PENDING, SUCCESS, FAILED, CANCELLED
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}