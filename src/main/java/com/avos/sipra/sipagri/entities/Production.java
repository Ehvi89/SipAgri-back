package com.avos.sipra.sipagri.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Productions")
public class Production {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "production_seq")
    @SequenceGenerator(name = "production_seq", sequenceName = "production_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plantation_id")
    private Plantation plantation;

    @Column(name = "prod_in_kg", nullable = false)
    private Double productionInKg;

    @Column(name = "purchase_price")
    private Double purchasePrice;

    @Column(name = "must_be_paid")
    private Boolean mustBePaid;

    @Column(name = "year")
    private Date year;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}