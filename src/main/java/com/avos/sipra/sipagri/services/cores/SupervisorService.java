package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.enums.SupervisorProfile;
import com.avos.sipra.sipagri.services.dtos.PaginationResponseDTO;
import com.avos.sipra.sipagri.services.dtos.SupervisorDTO;
import org.springframework.data.domain.Pageable;

public interface SupervisorService extends CrudService<SupervisorDTO, Long> {
    SupervisorDTO findByEmail(String email);

    PaginationResponseDTO<SupervisorDTO> findAllPagedByParams(Pageable pageable, String params, SupervisorProfile profile);
}
