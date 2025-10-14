package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlantationDTO;
import org.springframework.data.domain.Pageable;

public interface PlantationService extends CrudService<PlantationDTO, Long> {
    public PlantationDTO findByProductions_id(Long productionsId);

    PaginationResponseDTO<PlantationDTO> findAllPagedPlantationByPlanterSupervisor(Pageable pageable, Long supervisorId);
}
