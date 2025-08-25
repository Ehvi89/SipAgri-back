package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.SupervisorDTO;

public interface SupervisorService extends CrudService<SupervisorDTO, Long> {
    SupervisorDTO findByEmail(String email);
}
