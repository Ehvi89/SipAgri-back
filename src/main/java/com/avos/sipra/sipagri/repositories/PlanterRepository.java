package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Planter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanterRepository extends JpaRepository<Planter, Long> {

}
