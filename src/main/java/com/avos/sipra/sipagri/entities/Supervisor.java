package com.avos.sipra.sipagri.entities;

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
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE, generator = "supervisor_seq")
    @SequenceGenerator(name = "supervisor_seq", allocationSize = 1)
    private Long id;

    private String firstname;
    
    private String lastname;

    @OneToMany(mappedBy = "supervisor")
    private List<Planter> planters;
}
