package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.Kit;
import com.avos.sipra.sipagri.entities.KitProduct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class KitRepositoryIntegrationTest {

    @Autowired
    private KitRepository kitRepository;

    @Test
    void should_find_all_kits() {
        List<Kit> kits = kitRepository.findAll();

        assertThat(kits).hasSize(4);
        assertThat(kits)
                .extracting(Kit::getName)
                .containsExactlyInAnyOrder("Kit Céréales", "Kit Maraîchage", "Kit Bio", "Kit Bambou");
    }

    @Test
    void should_find_kit_by_id() {
        // ID 1 correspond au premier kit inséré dans data.sql
        Optional<Kit> foundKit = kitRepository.findById(1L);

        assertThat(foundKit).isPresent();
        assertThat(foundKit.get())
                .hasFieldOrPropertyWithValue("name", "Kit Céréales")
                .hasFieldOrPropertyWithValue("totalCost", 35000.0);
    }

    @Test
    void should_find_kit_with_its_products() {
        Optional<Kit> kitOptional = kitRepository.findById(1L);

        assertThat(kitOptional).isPresent();
        Kit kit = kitOptional.get();
        List<KitProduct> kitProducts = kit.getKitProducts();

        assertThat(kitProducts).hasSize(2);
        assertThat(kitProducts)
                .extracting(kp -> kp.getProduct().getName())
                .containsExactlyInAnyOrder("Engrais NPK", "Semences de maïs");
    }

    @Test
    void should_create_new_kit() {
        Kit newKit = new Kit();
        newKit.setName("Nouveau kit");
        newKit.setTotalCost(35000.0);

        Kit savedKit = kitRepository.save(newKit);

        assertThat(savedKit.getId()).isNotNull();
        assertThat(kitRepository.findById(savedKit.getId())).isPresent();
        assertThat(kitRepository.findAll()).hasSize(5); // 4 initiaux + 1 nouveau
    }

    @Test
    void should_update_kit() {
        Kit kitToUpdate = kitRepository.findById(1L).get();
        kitToUpdate.setTotalCost(38000.0);
        kitToUpdate.setDescription("Description modifiée");

        kitRepository.save(kitToUpdate);

        Kit updatedKit = kitRepository.findById(1L).get();
        assertThat(updatedKit.getTotalCost()).isEqualTo(38000.0);
        assertThat(updatedKit.getDescription()).isEqualTo("Description modifiée");
    }

    @Test
    void should_delete_kit() {
        kitRepository.deleteById(4L);

        Optional<Kit> foundKit = kitRepository.findById(4L);
        assertFalse(foundKit.isPresent());
    }

    @Test
    void should_verify_name_uniqueness_constraint() {
        Kit duplicateNameKit = Kit.builder()
                .name("Kit Céréales") // Nom déjà existant dans data.sql
                .totalCost(10000.0)
                .build();

        // On s'attend à ce que ça échoue à cause de la contrainte d'unicité
        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> kitRepository.saveAndFlush(duplicateNameKit)
        );
    }
}