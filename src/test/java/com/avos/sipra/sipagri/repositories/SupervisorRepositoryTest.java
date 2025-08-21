package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Supervisor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class SupervisorRepositoryTest {
    @Autowired
    private SupervisorRepository supervisorRepository;

    @Test
    void shouldFindAll() {
        List<Supervisor> supervisors = supervisorRepository.findAll();

        assertEquals(3,  supervisors.size());
        assertEquals(1,  supervisors.get(0).getId());
        assertEquals("Pierre", supervisors.get(2).getFirstname());
    }

    @Test
    void shouldFindById() {
        Optional<Supervisor> supervisor = supervisorRepository.findById(1L);

        assertEquals(1,  supervisor.get().getId());
        assertEquals("Jean", supervisor.get().getFirstname());
    }

    @Test
    void shouldSave() {
        Supervisor supervisor = new Supervisor();
        supervisor.setFirstname("New Supervisor");

        Supervisor savedSupervisor = supervisorRepository.save(supervisor);

        assertEquals("New Supervisor", savedSupervisor.getFirstname());
    }

    @Test
    void shouldUpdate() {
        Supervisor supervisor = supervisorRepository.findById(1L).get();
        supervisor.setLastname("Updated Supervisor bio");

        Supervisor updatedSupervisor = supervisorRepository.save(supervisor);

        assertEquals("Updated Supervisor bio", updatedSupervisor.getLastname());
    }

    @Test
    void shouldDelete() {
        supervisorRepository.deleteById(1L);

        Optional<Supervisor> supervisor = supervisorRepository.findById(1L);

        assertFalse(supervisor.isPresent());
    }
}