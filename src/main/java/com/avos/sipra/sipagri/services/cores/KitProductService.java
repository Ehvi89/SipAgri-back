package com.avos.sipra.sipagri.services.cores;

import com.avos.sipra.sipagri.services.dtos.KitProductDTO;

/**
 * Service interface for managing KitProduct entities.
 * Extends the generic {@code CrudService} interface to provide standard
 * Create, Read, Update, and Delete operations for {@code KitProductDTO}.
 *
 * This interface is intended to define methods specific to KitProduct management
 * and should be implemented by a concrete service class.
 *
 * @see CrudService
 * @see KitProductDTO
 */
public interface KitProductService extends CrudService<KitProductDTO, Long>{
}
