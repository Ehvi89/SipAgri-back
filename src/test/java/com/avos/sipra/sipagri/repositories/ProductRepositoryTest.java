package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldFindAll() {
        List<Product> products = productRepository.findAll();

        assertEquals(4,  products.size());
        assertEquals(1,  products.get(0).getId());
        assertEquals("Pesticide bio", products.get(2).getName());
    }

    @Test
    void shouldFindById() {
        Optional<Product> product = productRepository.findById(1L);

        assertEquals(1,  product.get().getId());
        assertEquals("Engrais NPK", product.get().getName());
    }

    @Test
    void shouldSave() {
        Product product = new Product();
        product.setName("New Product bio");
        product.setPrice(230.5);

        Product savedProduct = productRepository.save(product);

        assertEquals(230.5,  savedProduct.getPrice());
        assertEquals("New Product bio", savedProduct.getName());
    }

    @Test
    void shouldUpdate() {
        Product product = productRepository.findById(1L).get();
        product.setName("Updated Product bio");

        Product updatedProduct = productRepository.save(product);

        assertEquals("Updated Product bio", updatedProduct.getName());
    }

    @Test
    void shouldDelete() {
        productRepository.deleteById(1L);

        Optional<Product> product = productRepository.findById(1L);

        assertFalse(product.isPresent());
    }
}