package com.avos.sipra.sipagri.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Kits")
public class Kit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kit_seq")
    @SequenceGenerator(name = "kit_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(nullable = false)
    private Double totalCost;

    @Column(length = 255)
    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "kit")
    private List<KitProduct> kitProducts;
}
