package com.avos.sipra.sipagri.services.mappers;

import com.avos.sipra.sipagri.entities.Kit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class KitMapperTest {

    @Test
    void shouldTransformToDTO() {
        Kit kit = new Kit();
        kit.setName("Kit1");
    }

    @Test
    void shouldTransformToEntity() {
    }

    @Test
    void shouldPartialUpdate() {
    }
}