package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Planter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class PlanterRepositoryTest {
    @Autowired
    private PlanterRepository planterRepository;

    @Test
    void shouldFindAllPlanters() {
        List<Planter> planters = planterRepository.findAll();

        assertEquals(4, planters.size());
        assertEquals("Traoré", planters.get(0).getLastname());
        assertEquals("Sokoura", planters.get(0).getVillage());
    }

    @Test
    void shouldFindById() {
        Planter planter = planterRepository.findById(1L).get();

        assertEquals("Traoré",  planter.getLastname());
        assertEquals("1980-05-15 00:00:00.0", planter.getBirthday().toString());
    }

    @Test
    void shouldUpdatePlanter() {
        Planter planter = planterRepository.findById(1L).get();
        planter.setChildrenNumber(5);

        Planter planterUpdated = planterRepository.save(planter);

        assertEquals(5, planterUpdated.getChildrenNumber());
    }

    @Test
    void shouldDeletePlanter() {
        planterRepository.deleteById(1L);
        Optional<Planter> planter = planterRepository.findById(1L);

        assertFalse(planter.isPresent());
    }
}