package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Production;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class ProductionRepositoryTest {
    @Autowired
    private ProductionRepository productionRepository;

    @Test
    void shouldFindAll() {
        List<Production> productions = productionRepository.findAll();

        assertEquals(5, productions.size());
        assertEquals(550,  productions.get(2).getPurchasePrice());
    }

    @Test
    void shouldFindById() {
        Production production = productionRepository.findById(1L).get();

        assertEquals(500,  production.getPurchasePrice());
        assertEquals(1500,  production.getProductionInKg());
    }

    @Test
    void shouldSaveProduction() {
        Production production = new Production();
        production.setProductionInKg(1250.9);
        production.setPurchasePrice(875.0);
        production.setMustBePaid(true);

        Production savedProduction = productionRepository.save(production);

        assertEquals(production.getProductionInKg(), savedProduction.getProductionInKg());
    }

    @Test
    void shouldUpdate() {
        Production production = productionRepository.findById(1L).get();
        production.setProductionInKg(2000.0);

        Production updatedProduction = productionRepository.save(production);
        assertEquals(2000.0, updatedProduction.getProductionInKg());
    }

    @Test
    void shouldDelete() {
        productionRepository.deleteById(1L);

        Optional<Production> production = productionRepository.findById(1L);

        assertFalse(production.isPresent());
    }
}