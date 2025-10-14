package com.avos.sipra.sipagri.services.dtos;

import com.avos.sipra.sipagri.types.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantationDTO {
    private Long id;

    private String name;

    private String description;

    private Location gpsLocation;

    private Double farmedArea;

    private java.util.List<ProductionDTO> productions;

    private Long planterId;

    private KitDTO kit;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
