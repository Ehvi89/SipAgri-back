package com.avos.sipra.sipagri.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Represents a Password Reset Token entity.
 * <p>
 * This entity is used to manage the tokens required for resetting passwords
 * in the system. Each token is associated with a specific Supervisor, along
 * with its expiration date. It is mapped to the "password_reset_token"
 * database table.
 * <p>
 * Relationships:
 * - Each Password Reset Token is associated with one Supervisor entity,
 *   creating a one-to-one relationship. This relationship ensures that
 *   a unique token is generated and linked to a specific supervisor for
 *   authentication and password reset purposes.
 * <p>
 * Fields:
 * - id: The unique identifier for the Password Reset Token entity.
 * - token: Represents the token string used for password reset validation.
 * - supervisor: Reference to the Supervisor entity to which the token is linked.
 * - expiryDate: Indicates the date and time at which the token becomes invalid.
 * <p>
 * This entity uses various JPA annotations to configure its persistence
 * behavior, including sequence generation for the primary key and table
 * mapping for associated columns and relationships.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "password_reset_token")
public class PasswordResetToken {
    /**
     * The unique identifier for the PasswordResetToken entity.
     * <p>
     * This field serves as the primary key for the PasswordResetToken table
     * in the database. It is automatically generated using a sequence
     * generator named "password_reset_token_seq" with a sequence name of
     * "password_reset_token_seq" and an allocation size of 1. This ID
     *  uniquely identifies each token.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_reset_token_seq")
    @SequenceGenerator(name = "password_reset_token_seq", sequenceName = "password_reset_token_seq", allocationSize = 1)
    private Long id;

    /**
     * Represents the token string used for password reset validation.
     * <p>
     * This field stores the unique token assigned to a password reset request.
     * The value is utilized to verify the validity of the request during the
     * reset process. It is mapped to the "token" column in the
     * "password_reset_token" database table.
     */
    @Column(name = "token")
    private String token;

    /**
     * Represents the Supervisor associated with the Password Reset Token entity.
     * <p>
     * This field establishes a one-to-one relationship between the PasswordResetToken
     * entity and the Supervisor entity, linking a unique password reset token
     * to a specific supervisor. This association is managed using a foreign key
     * column named "supervisor_id" in the database, which references the primary
     * key of the Supervisor entity.
     * <p>
     * Key characteristics:
     * - The `@OneToOne` annotation indicates the one-to-one relationship with the Supervisor entity.
     * - The `@JoinColumn` annotation specifies the configuration of the foreign key column:
     *   - `nullable = false` ensures that the token cannot exist without an associated supervisor.
     *   - `name = "supervisor_id"` specifies the name of the foreign key column in the database.
     * - The `fetch = FetchType.EAGER` setting ensures that the Supervisor entity is immediately
     *   fetched along with the PasswordResetToken whenever the token is retrieved.
     */
    @OneToOne(targetEntity = Supervisor.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "supervisor_id")
    private Supervisor supervisor;

    /**
     * Indicates the date and time at which the password reset token becomes invalid.
     * <p>
     * This field is mapped to the "expiry_date" column in the "password_reset_token" database table.
     * It is used to define the validity period of the token, ensuring that the token is only usable
     * within a specific time frame. After the expiration date, the token is no longer valid for
     * password reset operations.
     */
    @Column(name = "expiry_date")
    private Date expiryDate;
}