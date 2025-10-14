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
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supervisor_seq")
    @SequenceGenerator(name = "supervisor_seq", sequenceName = "supervisor_seq", allocationSize = 1)
    private Long id;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "profile")
    @Enumerated(EnumType.STRING)
    private SupervisorProfile profile;

    @Column(name = "phone")
    private String phone;

    @OneToMany(mappedBy = "supervisor")
    private List<Planter> planters;
}