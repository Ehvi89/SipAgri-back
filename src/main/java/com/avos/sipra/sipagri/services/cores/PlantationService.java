package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.PlantationDTO;

public interface PlantationService extends CrudService<PlantationDTO, Long> {
    public PlantationDTO findByProductions_id(Long productionsId);
}
