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
@Table(name = "Products")
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    private String description;
    
    @Column(nullable = false)
    private Double price;
}
