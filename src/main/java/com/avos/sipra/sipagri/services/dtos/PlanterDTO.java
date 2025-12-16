package com.avos.sipra.sipagri.services.dtos;

import com.avos.sipra.sipagri.enums.HumanGender;
import com.avos.sipra.sipagri.enums.MaritalStatus;
import com.avos.sipra.sipagri.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Data Transfer Object representing a {@code Planter} for API requests/responses.
 * <p>
 * Mirrors the {@link com.avos.sipra.sipagri.entities.Planter} entity but is tailored for
 * serialization and transport. It may omit internal fields and include nested DTOs
 * for convenience (e.g., {@link SupervisorDTO}, {@link PlantationDTO}).
 * </p>
 */
public class PlanterDTO {
    private Long id;

    private String uidPlanter;

    private String firstname;

    private String lastname;

    private Date birthday; 

    private HumanGender gender;

    private String phoneNumber;
    
    private MaritalStatus maritalStatus;

    private PaymentMethod paymentMethod;

    private Integer childrenNumber;

    private String village;

    private SupervisorDTO supervisor;

    private List<PlantationDTO> plantations;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

