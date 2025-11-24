package com.avos.sipra.sipagri.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the {@code KitProduct} entity, which associates a specific product
 * with a kit in the system, including details about the product quantity
 * and its total cost within the kit.
 * <p>
 * The {@code KitProduct} entity maps to the {@code Kit_Products} table in the database
 * and maintains a reference to the {@code Product} entity it corresponds to.
 * <p>
 * Fields:
 * - {@code id}: The unique identifier of the KitProduct entity, managed as
 *   a primary key with a sequence generator configuration.
 * - {@code product}: A many-to-one relationship referencing the {@code Product}
 *   entity. Represents the specific product associated with the kit.
 * - {@code quantity}: Represents the number of units of the given product included in the kit.
 * - {@code totalCost}: Represents the cumulative cost of the product based on its quantity and unit price.
 * <p>
 * Relationships:
 * - Each {@code KitProduct} is associated with exactly one {@code Product}.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Kit_Products")
public class KitProduct {
    /**
     * Represents the unique identifier for the {@code KitProduct} entity.
     * <p>
     * This field is the primary key of the {@code Kit_Products} table in the database.
     * It is automatically generated using a sequence generator named {@code kit_product_seq},
     * with an allocation size of 1 to ensure sequential generation of IDs.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kit_product_seq")
    @SequenceGenerator(name = "kit_product_seq", sequenceName = "kit_product_seq", allocationSize = 1)
    private Long id;

    /**
     * Represents the association between a KitProduct entity and a Product entity.
     * <p>
     * This field establishes a many-to-one relationship with the {@code Product} entity,
     * meaning that multiple {@code KitProduct} entities can be associated with the same
     * {@code Product}. It serves as a reference to the specific {@code Product} included
     * in the kit.
     * <p>
     * The relationship is mapped to the {@code product_id} column in the database, which
     * acts as a foreign key linking to the {@code Products} table. This field is marked
     * as non-nullable, indicating that a {@code KitProduct} must always reference a
     * specific {@code Product}.
     */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Represents the quantity of a specific product included in a kit.
     * <p>
     * This field holds the number of units of the product associated with the
     * {@code KitProduct} entity. It is a mandatory field and maps to the
     * {@code quantity} column in the database table.
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Represents the cumulative cost of the product associated with this entity.
     * <p>
     * This field is calculated based on the product's unit price and its quantity
     * within the kit. It is a mandatory field and maps to the "total_cost" column
     * in the "Kit_Products" database table.
     */
    @Column(name = "total_cost", nullable = false)
    private Double totalCost;
}
