package com.avos.sipra.sipagri.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "params")
@Builder
public class Params {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "params_seq")
    @SequenceGenerator(name = "params_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String value;

    private String codeParams;

    private Boolean encrypted;
}
