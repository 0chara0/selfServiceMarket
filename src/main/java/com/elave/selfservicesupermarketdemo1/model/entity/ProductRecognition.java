package com.elave.selfservicesupermarketdemo1.model.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_recognitions")
@Data
public class ProductRecognition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id", insertable = false, updatable = false)
    private ShoppingSession shoppingSession;
    
    @Column(name = "product_id")
    private String productId;
    
    @Column(name = "product_name")
    private String productName;
    
    @Column(name = "quantity")
    private Integer quantity = 1;
    
    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "recognition_confidence")
    private Double recognitionConfidence;
    
    @Column(name = "recognition_time")
    private LocalDateTime recognitionTime = LocalDateTime.now();
    
    @Column(name = "is_final_result")
    private Boolean isFinalResult = false;
}