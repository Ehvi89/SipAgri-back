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

    @Column(name = "farmed_area")
    private Double farmedArea;

    @Embedded
    @Column(name = "gps_location", unique = true)
    private Location gpsLocation;

    @Column(name = "planter_id")
    private Long planterId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Production> productions;

    @ManyToOne(optional = false)
    @JoinColumn(name = "kit_id")
    private Kit kit;
}
