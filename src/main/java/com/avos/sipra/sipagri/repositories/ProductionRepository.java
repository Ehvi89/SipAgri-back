package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Production;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionRepository extends JpaRepository<Production, Long> {

}
