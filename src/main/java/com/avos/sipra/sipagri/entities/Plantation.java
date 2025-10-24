package com.avos.sipra.sipagri.entities;

import com.avos.sipra.sipagri.enums.PlantationStatus;
import com.avos.sipra.sipagri.types.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
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
    @SequenceGenerator(name = "plantation_seq", sequenceName = "plantation_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "farmed_area")
    private Double farmedArea;

    private PlantationStatus status;

    private String sector;

    @Embedded
    private Location gpsLocation;

    @ManyToOne
    @JoinColumn(name = "planter_id")
    private Planter planter;

    @OneToMany(mappedBy = "plantation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Production> productions;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "kit_id")
    private Kit kit;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}