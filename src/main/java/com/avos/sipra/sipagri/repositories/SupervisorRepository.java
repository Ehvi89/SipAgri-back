package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Supervisor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupervisorRepository extends JpaRepository<Supervisor, Long> {

    Optional<Supervisor> findByEmail(String email);

    Page<Supervisor> findSupervisorsByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(Pageable pageable, String firstname, String lastname);
}
