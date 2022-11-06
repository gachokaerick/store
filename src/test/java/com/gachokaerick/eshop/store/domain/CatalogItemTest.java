package com.gachokaerick.eshop.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gachokaerick.eshop.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CatalogItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CatalogItem.class);
        CatalogItem catalogItem1 = new CatalogItem();
        catalogItem1.setId(1L);
        CatalogItem catalogItem2 = new CatalogItem();
        catalogItem2.setId(catalogItem1.getId());
        assertThat(catalogItem1).isEqualTo(catalogItem2);
        catalogItem2.setId(2L);
        assertThat(catalogItem1).isNotEqualTo(catalogItem2);
        catalogItem1.setId(null);
        assertThat(catalogItem1).isNotEqualTo(catalogItem2);
    }
}
