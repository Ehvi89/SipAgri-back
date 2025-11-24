package com.avos.sipra.sipagri.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the Params entity in the system.
 * <p>
 * Mapped to the database table "params", this entity contains configuration parameters
 * used within the application. Each parameter is identified by a unique name and value
 * along with optional metadata like description, codeParams, and encryption status.
 * <p>
 * Fields:
 * - id: Unique identifier for the entity, auto-generated using a sequence generator.
 * - name: The name of the parameter. This field is mandatory.
 * - description: An optional field providing a textual description of the parameter.
 * - value: The value associated with the parameter. This field is mandatory.
 * - codeParams: An optional code or additional identifier for the parameter.
 * - encrypted: Indicates whether the value of the parameter is encrypted, represented
 *   as a Boolean.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "params")
@Builder
public class Params {
    /**
     * The unique identifier for the Params entity.
     * <p>
     * This field serves as the primary key for the Params entity, ensuring its uniqueness
     * within the underlying database table. The value is automatically generated using a
     * sequence generator named "params_seq" with an allocation size of 1. The sequence ensures
     * consistency and efficient generation of unique identifiers for each Params entity record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "params_seq")
    @SequenceGenerator(name = "params_seq", sequenceName = "params_seq", allocationSize = 1)
    private Long id;

    /**
     * Represents the name of the parameter.
     * <p>
     * This field is a mandatory property of the Params entity and is used as an
     * identifier or key for a specific configuration parameter. It is mapped to the
     * "name" column in the "params" database table and cannot be null.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * An optional field providing a textual description of a parameter.
     * <p>
     * Represents additional information or details about the parameter.
     * This field is mapped to the "description" column in the database
     * and can be null if no description is required.
     */
    @Column(name = "description")
    private String description;

    /**
     * Represents the value associated with a specific parameter.
     * <p>
     * This field is mandatory and corresponds to the "value" column
     * in the "params" database table. It contains the actual data
     * or configuration tied to the parameter's name and is critical
     * to the proper functioning of the parameter system.
     */
    @Column(name = "value", nullable = false)
    private String value;

    /**
     * Represents optional code or additional identifier for the parameter.
     * <p>
     * Mapped to the database column "code_params", this field allows storing auxiliary
     * information or metadata related to the parameter. This field is not mandatory.
     */
    @Column(name = "code_params")
    private String codeParams;

    /**
     * Indicates whether the value of the parameter is encrypted.
     * <p>
     * This field represents a Boolean flag that determines if the corresponding parameter's value
     * is stored in an encrypted format. It is mapped to the "encrypted" column in the "params"
     * table of the database.
     */
    @Column(name = "encrypted")
    private Boolean encrypted;
}