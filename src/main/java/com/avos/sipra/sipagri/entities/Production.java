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

/**
 * Represents a production entity which is associated with a plantation
 * and tracks details about the production amount, purchase price,
 * payment status, and timestamps for creation and updates.
 * <p>
 * This entity uses JPA annotations for ORM mapping and Hibernate features
 * for automatic timestamping on creation and update events.
 * The primary key is auto-generated using a sequence generator.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Productions")
public class Production {
    /**
     * The unique identifier for the Production entity.
     * <p>
     * This field serves as the primary key for the Production entity and is
     * automatically generated using a sequence generator named "production_seq".
     * The sequence is defined with a name "production_seq" and an allocation
     * size of 1, ensuring that the ID values increment sequentially.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "production_seq")
    @SequenceGenerator(name = "production_seq", sequenceName = "production_seq", allocationSize = 1)
    private Long id;

    /**
     * Represents the relationship between this Production entity and a Plantation.
     * Indicates that multiple production records can be associated with a single plantation.
     * This is a many-to-one association where this entity holds the foreign key.
     * <p>
     * The plantation information is joined via the plantation_id column in the database.
     */
    @ManyToOne
    @JoinColumn(name = "plantation_id")
    private Plantation plantation;

    /**
     * Represents the amount of production measured in kilograms.
     * <p>
     * This field is mandatory and mapped to the "prod_in_kg" column in the
     * "Productions" database table. It tracks the production quantity associated
     * with a plantation and cannot be null.
     */
    @Column(name = "prod_in_kg", nullable = false)
    private Double productionInKg;

    /**
     * Represents the purchase price associated with a production entity.
     * <p>
     * This field holds the monetary value that corresponds to the price
     * at which the production was or will be purchased. It is mapped to
     * the "purchase_price" column in the database table "Productions".
     * <p>
     * The purchase price is optional and may not always have a value
     * associated with a given production record.
     */
    @Column(name = "purchase_price")
    private Double purchasePrice;

    /**
     * Indicates whether the payment for the production must be enforced.
     * <p>
     * This field maps to the "must_be_paid" column in the "Productions" table
     * and signifies if the corresponding production's payment obligation
     * should be considered mandatory.
     * It is represented as a Boolean, where {@code true} implies the
     * payment is required, and {@code false} indicates otherwise.
     */
    @Column(name = "must_be_paid")
    private Boolean mustBePaid;

    /**
     * Represents the year associated with the production.
     * <p>
     * This field maps to the column "year" in the "Productions" database table.
     * It stores the date or year relevant to the production entry as a {@code Date} object.
     */
    @Column(name = "year")
    private Date year;

    /**
     * Represents the timestamp when the production entity was created.
     * <p>
     * This field is automatically populated with the current date and time when
     * the entity is first persisted to the database. It is immutable and cannot
     * be updated after creation. The value is stored in the "created_at" column
     * in the database table and is managed using Hibernate's {@code @CreationTimestamp} annotation.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp indicating when the entity was last updated.
     * <p>
     * This field is automatically managed and updated by Hibernate whenever
     * the entity is modified. It is marked with the {@code @UpdateTimestamp}
     * annotation, which ensures the value is set to the current date
     * and time during the update operation.
     * <p>
     * It is mapped to the "updated_at" column in the database.
     * The field uses the {@code LocalDateTime} type to store date
     * and time information.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}