package com.avos.sipra.sipagri.entities;

import com.avos.sipra.sipagri.enums.PlantationStatus;
import com.avos.sipra.sipagri.types.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a Plantation entity in the system.
 * <p>
 * Mapped to the "plantations" database table, this entity encapsulates details
 * about a specific plantation, such as its name, description, farmed area, location,
 * and associated relationships with planters, productions, and kits. A plantation
 * serves as a key unit in the system to manage agricultural activities and track
 * relevant information.
 * <p>
 * Features:
 * - Tracks plantation metadata such as name, description, farmed area, and sector.
 * - Maintains geographic location details via an embedded {@code Location} entity.
 * - Links to a {@code Planter} entity (many-to-one relationship) to identify the planter associated with the plantation.
 * - Includes a one-to-many relationship with {@code Production} entities to manage production data from the plantation.
 * - Associates with a {@code Kit} entity (many-to-one relationship) to link resources used on the plantation.
 * - Tracks lifecycle timestamps, including creation and last update times.
 * <p>
 * Relationships:
 * - {@code planter}: Many plantations can be linked to one planter.
 * - {@code productions}: A plantation can have multiple production records.
 * - {@code kit}: Each plantation is associated with a specific kit.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "plantations")
public class Plantation {
    /**
     * Represents the unique identifier of the Plantation entity.
     * <p>
     * This field acts as the primary key for the Plantation entity. It is annotated
     * with {@code @Id} to designate it as the primary key and uses {@code @GeneratedValue}
     * with a sequence generator strategy for automatic value generation.
     * <p>
     * The sequence generator, named "plantation_seq", is configured to use the sequence
     * "plantation_seq" in the database with an allocation size of 1. This ensures that
     * each new Plantation entity is assigned a unique, incrementing identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plantation_seq")
    @SequenceGenerator(name = "plantation_seq", sequenceName = "plantation_seq", allocationSize = 1)
    private Long id;

    /**
     * Represents the name of the plantation.
     * <p>
     * This field is mapped to the "name" column in the corresponding database table.
     * It indicates the name or title of the plantation, which serves as one of its primary
     * identifying attributes. It is expected to hold a descriptive string value for the plantation.
     */
    @Column(name = "name")
    private String name;

    /**
     * Represents a textual description of the plantation.
     * <p>
     * This field is mapped to the "description" column in the database table. It provides
     * additional details or explanatory text about the plantation. The description is optional
     * and can be null if no additional information is required.
     */
    @Column(name = "description")
    private String description;

    /**
     * Represents the area of land used for farming purposes in the plantation.
     * <p>
     * This field is mapped to the "farmed_area" column in the database table. It indicates
     * the total size, in hectares, of the land allocated or utilized for farming activities
     * within the context of a plantation. The value is stored as a floating-point number to
     * accommodate non-integer measurements.
     */
    @Column(name = "farmed_area")
    private Double farmedArea;

    /**
     * Represents the current status of the plantation.
     * <p>
     * The {@code status} field indicates the lifecycle or operational state of the plantation.
     * It uses the {@code PlantationStatus} enumeration to categorize the status of the plantation,
     * aiding in the management and tracking of its progress or activity level.
     */
    private PlantationStatus status;

    /**
     * Represents the sector associated with the plantation.
     * <p>
     * The sector is a distinguishing or grouping factor that classifies
     * the plantation within a specific category, region, or type.
     * It is primarily a textual description and may hold key information
     * significant to the plantation's identity or management.
     */
    private String sector;

    /**
     * Represents the GPS location of the plantation.
     * <p>
     * This field is embedded and mapped using the Location class to capture
     * geographical coordinates or details related to the plantation's location.
     * It is used to store specific location information necessary for identifying
     * or managing plantations geographically.
     */
    @Embedded
    private Location gpsLocation;

    /**
     * Represents the planter associated with a specific plantation.
     * <p>
     * Establishes a many-to-one relationship with the {@code Planter} entity to
     * link a single planter to potentially multiple plantations. The associated
     * planter is stored in the {@code planter_id} column in the "plantations" table.
     * <p>
     * This relationship helps in identifying the farmer responsible for managing
     * the plantation. A planter can manage multiple plantations, but each plantation
     * is linked to only one planter.
     */
    @ManyToOne
    @JoinColumn(name = "planter_id")
    private Planter planter;

    /**
     * Represents the list of production records associated with this Plantation.
     * <p>
     * The relationship is established as a one-to-many, where one Plantation can have multiple
     * {@code Production} entities. Each production record provides details about a specific yield
     * or output generated by the plantation, and it is directly mapped to the {@code plantation}
     * field in the {@code Production} entity.
     * <p>
     * Characteristics:
     * - The relationship is bi-directional, managed via the {@code plantation} field in the {@code Production} entity.
     * - When a Plantation is persisted, updated, or removed, the corresponding changes are cascaded to
     *   the associated {@code Production} records due to the CascadeType.ALL configuration.
     * - The {@code orphanRemoval} property ensures that any {@code Production} record removed from
     *   the list is also deleted from the database.
     * <p>
     * Use cases:
     * - Tracks historical or periodic production metrics for the plantation.
     * - Enables monitoring and management of plantation-based outputs across different time periods.
     */
    @OneToMany(mappedBy = "plantation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Production> productions;

    /**
     * Represents the association with the {@code Kit} entity for a specific {@code Plantation}.
     * <p>
     * This field establishes a many-to-one relationship between the {@code Plantation} and
     * {@code Kit} entities. Each {@code Plantation} instance is associated with a single
     * {@code Kit}, while a single {@code Kit} can be associated with multiple {@code Plantation}
     * instances.
     * <p>
     * This relationship is defined with the following attributes:
     * - {@code optional = false}: The relationship is mandatory, and a {@code Plantation}
     *   cannot exist without an associated {@code Kit}.
     * - {@code cascade = CascadeType.ALL}: All persistence operations performed on
     *   {@code Plantation} are cascaded to the related {@code Kit}.
     * <p>
     * The {@code @JoinColumn} annotation specifies that the foreign key column in the
     * {@code Plantation} table, named {@code kit_id}, is used to establish this relationship
     * with the {@code Kit} entity.
     */
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "kit_id")
    private Kit kit;

    /**
     * Tracks the creation timestamp of the plantation record.
     * <p>
     * This field represents the date and time when the plantation entity was first created.
     * The value is automatically generated and managed by the database using the
     * {@code @CreationTimestamp} annotation. It is not intended to be updated, as indicated by its
     * {@code updatable = false} property.
     * <p>
     * Mapped to the "created_at" column in the "plantations" database table.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Represents the timestamp of the last update to the Plantation entity.
     * <p>
     * This field is automatically updated with the current date and time whenever
     * the entity is modified. It is annotated with {@code @UpdateTimestamp},
     * ensuring that the value is managed by the persistence provider without
     * requiring manual updates in the code. The {@code updatedAt} field is mapped
     * to the "updated_at" column in the database table.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}