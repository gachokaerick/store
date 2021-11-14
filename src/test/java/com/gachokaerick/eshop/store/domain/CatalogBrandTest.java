package com.gachokaerick.eshop.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gachokaerick.eshop.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CatalogBrandTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CatalogBrand.class);
        CatalogBrand catalogBrand1 = new CatalogBrand();
        catalogBrand1.setId(1L);
        CatalogBrand catalogBrand2 = new CatalogBrand();
        catalogBrand2.setId(catalogBrand1.getId());
        assertThat(catalogBrand1).isEqualTo(catalogBrand2);
        catalogBrand2.setId(2L);
        assertThat(catalogBrand1).isNotEqualTo(catalogBrand2);
        catalogBrand1.setId(null);
        assertThat(catalogBrand1).isNotEqualTo(catalogBrand2);
    }
}
