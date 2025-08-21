package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.ParamsDTO;

import java.util.List;

public interface ParamsService extends CrudService<ParamsDTO, Long> {
    List<ParamsDTO> findByCode(String code);

    ParamsDTO findByName(String name);
}
