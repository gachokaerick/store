package com.gachokaerick.eshop.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gachokaerick.eshop.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BasketCheckoutTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BasketCheckout.class);
        BasketCheckout basketCheckout1 = new BasketCheckout();
        basketCheckout1.setId(1L);
        BasketCheckout basketCheckout2 = new BasketCheckout();
        basketCheckout2.setId(basketCheckout1.getId());
        assertThat(basketCheckout1).isEqualTo(basketCheckout2);
        basketCheckout2.setId(2L);
        assertThat(basketCheckout1).isNotEqualTo(basketCheckout2);
        basketCheckout1.setId(null);
        assertThat(basketCheckout1).isNotEqualTo(basketCheckout2);
    }
}
