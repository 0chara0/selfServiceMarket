package com.elave.selfservicesupermarketdemo1.model.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vending_cabinets")
@Data
public class VendingCabinet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cabinet_id", unique = true, nullable = false)
    private String cabinetId;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CabinetStatus status = CabinetStatus.OFFLINE;
    
    @Column(name = "qr_code", length = 500)
    private String qrCode;
    
    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();
    
    @Column(name = "updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();
    
    public enum CabinetStatus {
        ONLINE, OFFLINE, MAINTENANCE
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedTime = LocalDateTime.now();
    }
}