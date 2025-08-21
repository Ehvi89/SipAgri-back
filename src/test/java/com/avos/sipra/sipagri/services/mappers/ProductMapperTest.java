package com.avos.sipra.sipagri.services.mappers;

import com.avos.sipra.sipagri.entities.Product;
import com.avos.sipra.sipagri.services.dtos.ProductDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    private final ProductMapper productMapper = new ProductMapper();

    @Test
    void toDTO() {
        // given
        Product product = new Product();
        product.setId(1L);
        product.setName("Engrais NPK");
        product.setPrice(15000.0);
        product.setDescription("Engrais complet 15-15-15");

        // when
        ProductDTO dto = productMapper.toDTO(product);

        // then
        assertNotNull(dto);
        assertEquals(product.getId(), dto.getId());
        assertEquals(product.getName(), dto.getName());
        assertEquals(product.getPrice(), dto.getPrice());
        assertEquals(product.getDescription(), dto.getDescription());
    }

    @Test
    void toEntity() {
        // given
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(2L);
        productDTO.setName("Semence Maïs");
        productDTO.setPrice(5000.0);
        productDTO.setDescription("Semence hybride");

        // when
        Product product = productMapper.toEntity(productDTO);

        // then
        assertNotNull(product);
        assertEquals(productDTO.getId(), product.getId());
        assertEquals(productDTO.getName(), product.getName());
        assertEquals(productDTO.getPrice(), product.getPrice());
        assertEquals(productDTO.getDescription(), product.getDescription());
    }

    @Test
    void partialUpdate() {
        // given
        Product product = new Product();
        product.setId(3L);
        product.setName("Produit initial");
        product.setPrice(1000.0);
        product.setDescription("Description initiale");

        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setName("Produit modifié");
        updateDTO.setPrice(2000.0);

        // when
        productMapper.partialUpdate(product, updateDTO);

        // then
        assertEquals("Produit modifié", product.getName());
        assertEquals(2000.0, product.getPrice());
        assertEquals("Description initiale", product.getDescription()); // non modifié
    }
}
