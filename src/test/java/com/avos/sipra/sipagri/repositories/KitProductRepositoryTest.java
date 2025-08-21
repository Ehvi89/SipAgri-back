package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.KitProduct;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class KitProductRepositoryTest {

    @Autowired
    private KitProductRepository kitProductRepository;

    @Autowired
    private KitRepository kitRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void should_find_all_kitProducts() {
        List<KitProduct> kitProducts = kitProductRepository.findAll();

        assertThat(kitProducts).hasSize(6);
        assertThat(kitProducts)
                .extracting(kp -> kp.getProduct().getName())
                .contains("Engrais NPK", "Semences de maïs", "Pesticide bio", "Irrigation goutte-à-goutte");
    }

    @Test
    void should_find_kitProduct_by_id() {
        Optional<KitProduct> foundKitProduct = kitProductRepository.findById(1L);

        assertThat(foundKitProduct).isPresent();
        assertThat(foundKitProduct.get())
                .hasFieldOrPropertyWithValue("quantity", 2)
                .hasFieldOrPropertyWithValue("totalCost", 30000.0);
    }

    @Test
    @Transactional
    void should_update_kitProduct() {
        KitProduct kitProduct = kitProductRepository.findById(1L).orElseThrow();
        kitProduct.setQuantity(10);
        kitProduct.setTotalCost(150000.0);

        kitProductRepository.save(kitProduct);
        entityManager.flush();
        entityManager.clear();

        KitProduct updatedKitProduct = kitProductRepository.findById(1L).orElseThrow();
        assertThat(updatedKitProduct)
                .hasFieldOrPropertyWithValue("quantity", 10)
                .hasFieldOrPropertyWithValue("totalCost", 150000.0);
    }

    @Test
    @Transactional
    void should_delete_kitProduct() {
        long countBefore = kitProductRepository.count();
        kitProductRepository.deleteById(1L);
        entityManager.flush();

        assertThat(kitProductRepository.count()).isEqualTo(countBefore - 1);
        assertThat(kitProductRepository.findById(1L)).isEmpty();
    }

    @Test
    void should_verify_kit_product_relationship() {
        KitProduct kitProduct = kitProductRepository.findById(1L).orElseThrow();

        assertThat(kitProduct.getProduct())
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Engrais NPK");
    }
}