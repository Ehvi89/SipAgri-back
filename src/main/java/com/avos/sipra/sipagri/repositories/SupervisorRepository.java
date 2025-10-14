package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Supervisor;
import com.avos.sipra.sipagri.enums.SupervisorProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@Repository
public interface SupervisorRepository extends JpaRepository<Supervisor, Long> {

    Optional<Supervisor> findByEmail(String email);

    @Query("""

            SELECT s 
    FROM Supervisor s
    WHERE 
        (
            (:firstname IS NULL OR LOWER(s.firstname) LIKE LOWER(CONCAT('%', :firstname, '%')))
        OR 
            (:lastname IS NULL OR LOWER(s.lastname) LIKE LOWER(CONCAT('%', :lastname, '%')))
        )
    AND 
        (:profile IS NULL OR s.profile = :profile)
    """)
    Page<Supervisor> searchSupervisors(Pageable pageable, String firstname, String lastname, SupervisorProfile profile);
}
