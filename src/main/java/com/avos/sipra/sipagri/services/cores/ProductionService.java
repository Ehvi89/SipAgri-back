package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.ProductionDTO;
import org.springframework.data.domain.Pageable;


public interface ProductionService extends CrudService<ProductionDTO, Long> {

    PaginationResponseDTO<ProductionDTO> findProductionByPlantationPlanterSupervisor(Pageable pageable, Long supervisorId);
}
