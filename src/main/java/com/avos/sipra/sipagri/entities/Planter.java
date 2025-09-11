package com.avos.sipra.sipagri.entities;

import com.avos.sipra.sipagri.enums.HumanGender;
import com.avos.sipra.sipagri.enums.MaritalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Planters")
public class Planter {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "planter_seq")
    @SequenceGenerator(name = "planter_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 50)
    private String lastname;

    @Column(nullable = false, length = 50)
    private String firstname;

    @Column(nullable = false)
    private Date birthday;

    @Column(nullable = false)
    private HumanGender gender;

    private String phoneNumber;

    @Column(name = "marital_status", nullable = false)
    private MaritalStatus maritalStatus;

    @Column(nullable = false)
    private Integer childrenNumber;

    @Column(nullable = false, length = 100)
    private String village;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    // Assuming a planter is supervised by one supervisor
    private Supervisor supervisor;

    @OneToMany(cascade = CascadeType.ALL)
    // Assuming a planter can have multiple plantations
    private List<Plantation> plantations;
}
