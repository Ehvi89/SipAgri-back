package com.avos.sipra.sipagri.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Product entity within the system.
 * <p>
 * Mapped to the database table "Products", this entity is used to manage
 * product information including details like its name, description, price, and
 * identifier.
 * <p>
 * Fields:
 * - id: Unique identifier for each Product, generated using a sequence generator.
 * - name: The name of the product, required and limited to 100 characters.
 * - description: An optional field to provide additional details about the product.
 * - price: The price of the product, which is mandatory.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Products")
@Builder
public class Product {
    /**
     * The unique identifier for the Product entity.
     * <p>
     * This field serves as the primary key for the Product entity and is
     * automatically generated using a sequence generator. The sequence generator
     * is named "product_seq", and it is configured to use the "product_seq"
     * database sequence with an allocation size of 1.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "product_seq", allocationSize = 1)
    private Long id;

    /**
     * Represents the name of the Product.
     * <p>
     * This field is mandatory and serves as a descriptive identifier for the product.
     * It is mapped to the "name" column in the "Products" database table with a
     * constraint of being non-null and a maximum length of 100 characters.
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Represents an optional description field for a Product entity.
     * <p>
     * This field provides additional details about the product, but it is not mandatory.
     * It is mapped to the "description" column in the "Products" database table.
     */
    @Column(name = "description")
    private String description;

    /**
     * Represents the price of the product.
     * <p>
     * This field is mandatory and is used to specify the cost of a product.
     * It is stored as a Double precision floating-point number in the database
     * and mapped to the "price" column in the "Products" table.
     */
    @Column(name = "price", nullable = false)
    private Double price;
}