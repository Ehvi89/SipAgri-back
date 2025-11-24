package com.avos.sipra.sipagri.entities;

import com.avos.sipra.sipagri.enums.HumanGender;
import com.avos.sipra.sipagri.enums.MaritalStatus;
import com.avos.sipra.sipagri.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * The Planter class represents an entity for storing information about a planter.
 * It includes personal details such as name, birthday, gender, and contact information,
 * as well as details related to marital status, payment method, number of children,
 * and their associated village. It is linked to other entities such as Supervisor
 * and Plantation through relationships.
 * <p>
 * This entity is annotated with JPA annotations to map it to the corresponding database table.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Planters")
public class Planter {
    /**
     * Represents the unique identifier for a Planter entity.
     * This field serves as the primary key for the Planter table in the database.
     * <p>
     * The value is automatically generated using a sequence generator named "planter_seq".
     * The sequence is configured to increment by an allocation size of 1.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "planter_seq")
    @SequenceGenerator(name = "planter_seq", sequenceName = "planter_seq", allocationSize = 1)
    private Long id;

    /**
     * Represents the last name of the planter.
     * <p>
     * This field is mapped to the "lastname" column in the database table and has the following constraints:
     * - It cannot be null, ensuring that every planter has a recorded last name.
     * - It is limited to a maximum length of 50 characters.
     * <p>
     * This attribute is essential for identifying and organizing planter records.
     */
    @Column(name = "lastname", nullable = false, length = 50)
    private String lastname;

    /**
     * Represents the first name of a Planter entity.
     * <p>
     * This field is mandatory, constrained to a maximum length of 50 characters,
     * and mapped to the "firstname" column in the database. It serves as an identifier
     * for the planter's given name or personal name.
     */
    @Column(name = "firstname", nullable = false, length = 50)
    private String firstname;

    /**
     * Represents the birthdate of the planter.
     * <p>
     * This field is mandatory and cannot be null. It is used to store the date of birth
     * and is mapped to the "birthday" column in the "Planters" table in the database.
     */
    @Column(name = "birthday", nullable = false)
    private Date birthday;

    /**
     * Represents the gender of the planter.
     * <p>
     * This variable is an enumerated type that corresponds to the {@code HumanGender} enumeration.
     * It is used to store the gender of the planter as a string representation in the database.
     * <p>
     * Constraints:
     * - This field is mandatory (non-nullable).
     * - It is mapped to the "gender" column in the "Planters" table.
     * <p>
     * Annotations:
     * - {@code @Column}: Specifies the database column name as "gender" and enforces non-null values.
     * - {@code @Enumerated(EnumType.STRING)}: Indicates that the enum value will be persisted as its string representation.
     */
    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private HumanGender gender;

    /**
     * Represents the contact phone number of a planter.
     * <p>
     * This field stores the phone number as a string and is mapped to the "phone_number"
     * column in the "Planters" database table. The phone number may be used for communication
     * or identification and can optionally be null or empty, depending on the system's
     * requirements.
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Represents the marital status of an individual.
     * <p>
     * This field is mapped to the "marital_status" column in the database
     * and is stored as a string representation of the enumeration values
     * defined in the {@code MaritalStatus} enum.
     * <p>
     * Attributes:
     * - Not nullable: Indicates that the marital status is a required field
     *   and must be provided.
     * - Enumerated: Uses {@code EnumType.STRING} to persist the enum values
     *   as their corresponding string names.
     */
    @Column(name = "marital_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    /**
     * Specifies the payment method chosen by the planter.
     * <p>
     * This field is an enumerated type that defines the available payment method options
     * for a planter. It is mapped to the "payment_method" column in the database table
     * and is stored as a string representation of the enumeration constant.
     * <p>
     * Constraints:
     * - Cannot be null.
     * <p>
     * Annotation Details:
     * - {@code @Enumerated(EnumType.STRING)}: Ensures that the enumeration value is stored
     *   as a string in the database.
     * - {@code @Column(name = "payment_method", nullable = false)}: Maps the field to a
     *   database column named "payment_method" and enforces that it must not be null.
     */
    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    /**
     * Represents the number of children associated with the Planter entity.
     * <p>
     * This field is a mandatory integer value, mapped to the "children_number" column
     * in the database. It holds the total count of children for the corresponding planter.
     */
    @Column(name = "children_number", nullable = false)
    private Integer childrenNumber;

    /**
     * Represents the name of the village associated with the Planter.
     * <p>
     * This field is a mandatory property that holds a non-null string value
     * identifying the village name. It is constrained to a maximum length
     * of 100 characters and is mapped to the "village" column in the database table.
     */
    @Column(name = "village", nullable = false, length = 100)
    private String village;

    /**
     * Represents the supervisor entity linked to the planter.
     * <p>
     * This field establishes a many-to-one relationship between the Planter
     * entity and the Supervisor entity. Each planter is overseen by a single supervisor.
     * The relationship is mapped to the "supervisor_id" column in the database.
     * <p>
     * Annotations:
     * - {@code @ManyToOne}: Specifies the many-to-one relationship.
     * - {@code @JoinColumn(name = "supervisor_id")}: Defines the foreign key column
     *   used to join the two entities in the database.
     * <p>
     * The Supervisor entity includes personal and professional details
     * such as name, contact information, assigned profile, and
     * the associated planters they manage.
     */
    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private Supervisor supervisor;

    /**
     * Represents the plantations associated with this planter.
     * This collection establishes a one-to-many relationship between the Planter entity and the Plantation entity.
     * Each plantation in the list is mapped to the planter using the 'planter' field in the Plantation entity.
     * <p>
     * CascadeType.ALL ensures that all persistence operations (persist, merge, remove, etc.) performed on the planter
     * are cascaded to the associated plantations. OrphanRemoval ensures that orphaned plantations (those no longer
     * associated with a planter) are automatically removed from the database.
     */
    @OneToMany(mappedBy = "planter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plantation> plantations;

    /**
     * The timestamp indicating when the entity was created.
     * <p>
     * This field is automatically populated with the current date and time
     * when the entity is first persisted to the database. It is annotated
     * with {@code @CreationTimestamp} to ensure that the value is assigned
     * only during the creation of the record, and it is never updated afterwards.
     * <p>
     * Mapped to the "created_at" column in the database table and is not
     * allowed to be updated after it is set.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Represents the date and time when the Planter record was last updated.
     * <p>
     * This field is automatically managed and its value is updated whenever
     * the record is modified in the database. Mapped to the "updated_at" column
     * in the database table, it provides a mechanism to track changes and
     * ensure data synchronization.
     * <p>
     * The @UpdateTimestamp annotation ensures that the field is automatically
     * updated to the current timestamp whenever an update operation is performed
     * on the entity.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}