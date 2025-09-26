package com.vendi.vendi_ms.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Classe base para todas as entidades do sistema.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@MappedSuperclass
@Data
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @PreUpdate
    @PrePersist
    public void prePersist() {
        if (deleted == null) {
            deleted = false;
        }
    }

    public boolean isActive() {
        return !deleted;
    }

    public void softDelete() {
        this.deleted = true;
    }

    public void restore() {
        this.deleted = false;
    }
}
