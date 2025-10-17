package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.PlanterDTO;
import org.springframework.data.domain.Pageable;

public interface PlanterService extends CrudService<PlanterDTO, Long> {

    PaginationResponseDTO<PlanterDTO> findPlanterBySupervisor(Pageable pageable, Long supervisorId);

    PaginationResponseDTO<PlanterDTO> findAllPagedByVillage(Pageable pageable, String params);
}
