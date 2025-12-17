package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Plantation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class PlantationRepositoryTest {
    @Autowired
    private PlantationRepository plantationRepository;

    @Test
    void shouldFindAll() {
        List<Plantation> plantations = plantationRepository.findAll();

        assertEquals(4, plantations.size());
        assertEquals(2.5, plantations.get(0).getFarmedArea());
    }

    @Test
    void shouldFindById() {
        Optional<Plantation> plantation = plantationRepository.findById(1L);

        assertTrue(plantation.isPresent());
        assertEquals(2.5, plantation.get().getFarmedArea());
    }

    @Test
    void shouldUpdate() {
        Plantation plantation = plantationRepository.findById(1L).get();
        plantation.setFarmedArea(5.5);

        Plantation savedPlantation = plantationRepository.save(plantation);

        assertEquals(5.5, savedPlantation.getFarmedArea());
    }

    @Test
    void shouldDelete() {
        plantationRepository.deleteById(1L);

        Optional<Plantation> plantation = plantationRepository.findById(1L);

        assertTrue(plantation.isEmpty());
    }
}