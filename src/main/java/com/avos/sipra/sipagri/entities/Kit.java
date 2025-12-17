package com.avos.sipra.sipagri.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a Kit entity managed in the system.
 * <p>
 * Mapped to the database table "Kits". A Kit is a collection of products
 * grouped and managed together. This entity contains details such as the
 * name of the kit, its total cost, an optional description, and the list
 * of products included in the kit.
 * <p>
 * Relationships:
 * - Contains a one-to-many relationship with the KitProduct entity,
 *   which identifies the individual products included in the kit
 *   and their quantities.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Kits")
public class Kit {

    /**
     * The unique identifier of the Kit entity.
     * <p>
     * This field is a primary key, automatically generated using a sequence generator
     * named "kit_seq". The sequence is configured to increment by an allocation size of 1.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kit_seq")
    @SequenceGenerator(name = "kit_seq", sequenceName = "kit_seq", allocationSize = 1)
    private Long id;

    /**
     * Represents the name of the Kit.
     * <p>
     * This field is mandatory and constrained to a maximum length of 100 characters.
     * It is mapped to the "name" column in the "Kits" database table and serves as
     * an essential identifier or label for the kit.
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Represents the total cost of the kit.
     * <p>
     * This field holds the aggregated cost of all products included in the kit,
     * calculated based on the individual product prices and their respective quantities.
     * It is a mandatory field and is mapped to the "total_cost" column in the "Kits" database table.
     */
    @Column(name = "total_cost", nullable = false)
    private Double totalCost;

    /**
     * Optional short description of the kit.
     * <p>
     * Represents a textual description that provides additional details
     * about the kit. The description is limited to a maximum length of
     * 255 characters and is not mandatory.
     */
    @Column(name = "description", length = 255)
    private String description;

    /**
     * Represents a collection of products that belong to a specific Kit.
     * <p>
     * This field establishes a one-to-many relationship with the {@code KitProduct} entity,
     * where each Kit can contain multiple KitProducts. The {@code cascade} attribute ensures
     * that persistence operations (such as persist, merge, remove) applied to the Kit entity
     * are propagated to the associated KitProducts. Setting {@code orphanRemoval} to true
     * ensures that any KitProduct removed from the collection will also be removed
     * from the database.
     * <p>
     * The relationship is managed using a foreign key column named {@code kit} in the
     * database, which associates each {@code KitProduct} with its corresponding {@code Kit}.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "kit")
    private List<KitProduct> kitProducts;
}