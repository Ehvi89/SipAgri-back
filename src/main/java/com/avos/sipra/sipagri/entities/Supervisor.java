package com.avos.sipra.sipagri.entities;

import com.avos.sipra.sipagri.enums.SupervisorProfile;
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
@Table(name = "supervisors")
public class Supervisor {
    /**
     * The unique identifier for the Supervisor entity.
     * <p>
     * This field serves as the primary key for the Supervisor table in the database.
     * It is automatically generated using a sequence generator named "supervisor_seq".
     * The sequence is configured to increment with an allocation size of 1.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supervisor_seq")
    @SequenceGenerator(name = "supervisor_seq", sequenceName = "supervisor_seq", allocationSize = 1)
    private Long id;

    /**
     * Represents the first name of the supervisor.
     * <p>
     * This field is mapped to the "firstname" column in the "supervisors" database table.
     * It holds the first name of the supervisor as a string value and is a part of
     * identifying or describing the supervisor entity.
     */
    @Column(name = "firstname")
    private String firstname;

    /**
     * Represents the last name of a supervisor.
     * <p>
     * Mapped to the "lastname" column in the "supervisors" database table.
     * This field stores the surname or family name of the supervisor
     * and is part of the supervisor's identity information.
     */
    @Column(name = "lastname")
    private String lastname;

    /**
     * Represents the email address of the supervisor.
     * <p>
     * This field is mapped to the "email" column in the "supervisors" database table.
     * It is used to store the email contact information of the supervisor.
     */
    @Column(name = "email")
    private String email;

    /**
     * Represents the password of the Supervisor.
     * <p>
     * This field stores a Supervisor's password and is mapped to the "password" column
     * in the "supervisors" database table. It is expected to hold a hashed string
     * representation for security purposes and should be handled with care to
     * prevent unauthorized access or exposure.
     */
    @Column(name = "password")
    private String password;

    /**
     * Specifies the profile type of the supervisor.
     * <p>
     * This field is an enumeration that defines the role or profile
     * of the supervisor in the system. It is mapped to the "profile"
     * column in the "supervisors" database table and stored as a
     * string representation of the enum value.
     */
    @Column(name = "profile")
    @Enumerated(EnumType.STRING)
    private SupervisorProfile profile;

    /**
     * Represents the phone number of the supervisor.
     * <p>
     * This field is mapped to the "phone" column in the "supervisors" database table.
     * It holds the contact phone number associated with the supervisor.
     */
    @Column(name = "phone")
    private String phone;

    /**
     * Represents the list of planters that are supervised by this supervisor.
     * This establishes a one-to-many relationship where a supervisor can manage multiple planters.
     * Mapped by the "supervisor" field in the Planter entity.
     */
    @OneToMany(mappedBy = "supervisor")
    private List<Planter> planters;
}