package com.avos.sipra.sipagri.types;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    Double latitude;
    Double longitude;
    @AttributeOverride(name = "name", column = @Column(name = "display_name", unique=true))
    String displayName;
}
