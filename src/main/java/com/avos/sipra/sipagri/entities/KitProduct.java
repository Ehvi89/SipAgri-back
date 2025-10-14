package com.avos.sipra.sipagri.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Kit_Products")
public class KitProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kit_product_seq")
    @SequenceGenerator(name = "kit_product_seq", sequenceName = "kit_product_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total_cost", nullable = false)
    private Double totalCost;
}
