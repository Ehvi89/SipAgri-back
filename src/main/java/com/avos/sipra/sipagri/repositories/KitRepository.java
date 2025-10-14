package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Kit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitRepository extends JpaRepository<Kit, Long> {
    Page<Kit> findKitByNameContainingIgnoreCase(Pageable pageable, String name);
}
