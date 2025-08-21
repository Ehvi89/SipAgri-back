package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.KitProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitProductRepository extends JpaRepository<KitProduct, Long> {

}
