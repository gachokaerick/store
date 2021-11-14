package com.gachokaerick.eshop.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gachokaerick.eshop.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CatalogTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CatalogType.class);
        CatalogType catalogType1 = new CatalogType();
        catalogType1.setId(1L);
        CatalogType catalogType2 = new CatalogType();
        catalogType2.setId(catalogType1.getId());
        assertThat(catalogType1).isEqualTo(catalogType2);
        catalogType2.setId(2L);
        assertThat(catalogType1).isNotEqualTo(catalogType2);
        catalogType1.setId(null);
        assertThat(catalogType1).isNotEqualTo(catalogType2);
    }
}
