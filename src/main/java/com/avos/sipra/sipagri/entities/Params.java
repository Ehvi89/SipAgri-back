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
    @SequenceGenerator(name = "params_seq", sequenceName = "params_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "code_params")
    private String codeParams;

    @Column(name = "encrypted")
    private Boolean encrypted;
}