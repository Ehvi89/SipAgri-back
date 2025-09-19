package com.avos.sipra.sipagri.entities;

import com.avos.sipra.sipagri.types.Location;
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
@Table(name = "plantations")
public class Plantation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plantation_seq")
    @SequenceGenerator(name = "plantation_seq", allocationSize = 1)
    private Long id;

    private String name;

    private String description;

    @Column(name = "farmed_area")
    private Double farmedArea;

    @Embedded
    private Location gpsLocation;

    @Column(name = "planter_id")
    private Long planterId;

    @OneToMany(mappedBy = "plantation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Production> productions;

    @ManyToOne(optional = false)
    @JoinColumn(name = "kit_id")
    private Kit kit;
}
