package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Planter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

@Repository
public interface PlanterRepository extends JpaRepository<Planter, Long> {
    Page<Planter> findPlanterByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(Pageable pageable, String firstname, String lastname);
}
