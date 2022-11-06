package com.gachokaerick.eshop.store.web.rest;

import static com.gachokaerick.eshop.store.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.gachokaerick.eshop.store.IntegrationTest;
import com.gachokaerick.eshop.store.domain.BasketItem;
import com.gachokaerick.eshop.store.repository.BasketItemRepository;
import com.gachokaerick.eshop.store.service.EntityManager;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link BasketItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class BasketItemResourceIT {

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final String DEFAULT_PRODUCT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_OLD_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_OLD_UNIT_PRICE = new BigDecimal(2);

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String DEFAULT_PICTURE_URL = "AAAAAAAAAA";
    private static final String UPDATED_PICTURE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_USER_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_USER_LOGIN = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/basket-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BasketItemRepository basketItemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private BasketItem basketItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BasketItem createEntity(EntityManager em) {
        BasketItem basketItem = new BasketItem()
            .productId(DEFAULT_PRODUCT_ID)
            .productName(DEFAULT_PRODUCT_NAME)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .oldUnitPrice(DEFAULT_OLD_UNIT_PRICE)
            .quantity(DEFAULT_QUANTITY)
            .pictureUrl(DEFAULT_PICTURE_URL)
            .userLogin(DEFAULT_USER_LOGIN);
        return basketItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BasketItem createUpdatedEntity(EntityManager em) {
        BasketItem basketItem = new BasketItem()
            .productId(UPDATED_PRODUCT_ID)
            .productName(UPDATED_PRODUCT_NAME)
            .unitPrice(UPDATED_UNIT_PRICE)
            .oldUnitPrice(UPDATED_OLD_UNIT_PRICE)
            .quantity(UPDATED_QUANTITY)
            .pictureUrl(UPDATED_PICTURE_URL)
            .userLogin(UPDATED_USER_LOGIN);
        return basketItem;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(BasketItem.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        basketItem = createEntity(em);
    }

    @Test
    void createBasketItem() throws Exception {
        int databaseSizeBeforeCreate = basketItemRepository.findAll().collectList().block().size();
        // Create the BasketItem
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeCreate + 1);
        BasketItem testBasketItem = basketItemList.get(basketItemList.size() - 1);
        assertThat(testBasketItem.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testBasketItem.getProductName()).isEqualTo(DEFAULT_PRODUCT_NAME);
        assertThat(testBasketItem.getUnitPrice()).isEqualByComparingTo(DEFAULT_UNIT_PRICE);
        assertThat(testBasketItem.getOldUnitPrice()).isEqualByComparingTo(DEFAULT_OLD_UNIT_PRICE);
        assertThat(testBasketItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testBasketItem.getPictureUrl()).isEqualTo(DEFAULT_PICTURE_URL);
        assertThat(testBasketItem.getUserLogin()).isEqualTo(DEFAULT_USER_LOGIN);
    }

    @Test
    void createBasketItemWithExistingId() throws Exception {
        // Create the BasketItem with an existing ID
        basketItem.setId(1L);

        int databaseSizeBeforeCreate = basketItemRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkProductIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketItemRepository.findAll().collectList().block().size();
        // set the field null
        basketItem.setProductId(null);

        // Create the BasketItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkProductNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketItemRepository.findAll().collectList().block().size();
        // set the field null
        basketItem.setProductName(null);

        // Create the BasketItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUnitPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketItemRepository.findAll().collectList().block().size();
        // set the field null
        basketItem.setUnitPrice(null);

        // Create the BasketItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkOldUnitPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketItemRepository.findAll().collectList().block().size();
        // set the field null
        basketItem.setOldUnitPrice(null);

        // Create the BasketItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketItemRepository.findAll().collectList().block().size();
        // set the field null
        basketItem.setQuantity(null);

        // Create the BasketItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPictureUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketItemRepository.findAll().collectList().block().size();
        // set the field null
        basketItem.setPictureUrl(null);

        // Create the BasketItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUserLoginIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketItemRepository.findAll().collectList().block().size();
        // set the field null
        basketItem.setUserLogin(null);

        // Create the BasketItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllBasketItems() {
        // Initialize the database
        basketItemRepository.save(basketItem).block();

        // Get all the basketItemList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(basketItem.getId().intValue()))
            .jsonPath("$.[*].productId")
            .value(hasItem(DEFAULT_PRODUCT_ID.intValue()))
            .jsonPath("$.[*].productName")
            .value(hasItem(DEFAULT_PRODUCT_NAME))
            .jsonPath("$.[*].unitPrice")
            .value(hasItem(sameNumber(DEFAULT_UNIT_PRICE)))
            .jsonPath("$.[*].oldUnitPrice")
            .value(hasItem(sameNumber(DEFAULT_OLD_UNIT_PRICE)))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].pictureUrl")
            .value(hasItem(DEFAULT_PICTURE_URL))
            .jsonPath("$.[*].userLogin")
            .value(hasItem(DEFAULT_USER_LOGIN));
    }

    @Test
    void getBasketItem() {
        // Initialize the database
        basketItemRepository.save(basketItem).block();

        // Get the basketItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, basketItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(basketItem.getId().intValue()))
            .jsonPath("$.productId")
            .value(is(DEFAULT_PRODUCT_ID.intValue()))
            .jsonPath("$.productName")
            .value(is(DEFAULT_PRODUCT_NAME))
            .jsonPath("$.unitPrice")
            .value(is(sameNumber(DEFAULT_UNIT_PRICE)))
            .jsonPath("$.oldUnitPrice")
            .value(is(sameNumber(DEFAULT_OLD_UNIT_PRICE)))
            .jsonPath("$.quantity")
            .value(is(DEFAULT_QUANTITY))
            .jsonPath("$.pictureUrl")
            .value(is(DEFAULT_PICTURE_URL))
            .jsonPath("$.userLogin")
            .value(is(DEFAULT_USER_LOGIN));
    }

    @Test
    void getNonExistingBasketItem() {
        // Get the basketItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewBasketItem() throws Exception {
        // Initialize the database
        basketItemRepository.save(basketItem).block();

        int databaseSizeBeforeUpdate = basketItemRepository.findAll().collectList().block().size();

        // Update the basketItem
        BasketItem updatedBasketItem = basketItemRepository.findById(basketItem.getId()).block();
        updatedBasketItem
            .productId(UPDATED_PRODUCT_ID)
            .productName(UPDATED_PRODUCT_NAME)
            .unitPrice(UPDATED_UNIT_PRICE)
            .oldUnitPrice(UPDATED_OLD_UNIT_PRICE)
            .quantity(UPDATED_QUANTITY)
            .pictureUrl(UPDATED_PICTURE_URL)
            .userLogin(UPDATED_USER_LOGIN);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedBasketItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedBasketItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
        BasketItem testBasketItem = basketItemList.get(basketItemList.size() - 1);
        assertThat(testBasketItem.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testBasketItem.getProductName()).isEqualTo(UPDATED_PRODUCT_NAME);
        assertThat(testBasketItem.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
        assertThat(testBasketItem.getOldUnitPrice()).isEqualTo(UPDATED_OLD_UNIT_PRICE);
        assertThat(testBasketItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testBasketItem.getPictureUrl()).isEqualTo(UPDATED_PICTURE_URL);
        assertThat(testBasketItem.getUserLogin()).isEqualTo(UPDATED_USER_LOGIN);
    }

    @Test
    void putNonExistingBasketItem() throws Exception {
        int databaseSizeBeforeUpdate = basketItemRepository.findAll().collectList().block().size();
        basketItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, basketItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBasketItem() throws Exception {
        int databaseSizeBeforeUpdate = basketItemRepository.findAll().collectList().block().size();
        basketItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBasketItem() throws Exception {
        int databaseSizeBeforeUpdate = basketItemRepository.findAll().collectList().block().size();
        basketItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBasketItemWithPatch() throws Exception {
        // Initialize the database
        basketItemRepository.save(basketItem).block();

        int databaseSizeBeforeUpdate = basketItemRepository.findAll().collectList().block().size();

        // Update the basketItem using partial update
        BasketItem partialUpdatedBasketItem = new BasketItem();
        partialUpdatedBasketItem.setId(basketItem.getId());

        partialUpdatedBasketItem.productId(UPDATED_PRODUCT_ID).oldUnitPrice(UPDATED_OLD_UNIT_PRICE).pictureUrl(UPDATED_PICTURE_URL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBasketItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBasketItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
        BasketItem testBasketItem = basketItemList.get(basketItemList.size() - 1);
        assertThat(testBasketItem.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testBasketItem.getProductName()).isEqualTo(DEFAULT_PRODUCT_NAME);
        assertThat(testBasketItem.getUnitPrice()).isEqualByComparingTo(DEFAULT_UNIT_PRICE);
        assertThat(testBasketItem.getOldUnitPrice()).isEqualByComparingTo(UPDATED_OLD_UNIT_PRICE);
        assertThat(testBasketItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testBasketItem.getPictureUrl()).isEqualTo(UPDATED_PICTURE_URL);
        assertThat(testBasketItem.getUserLogin()).isEqualTo(DEFAULT_USER_LOGIN);
    }

    @Test
    void fullUpdateBasketItemWithPatch() throws Exception {
        // Initialize the database
        basketItemRepository.save(basketItem).block();

        int databaseSizeBeforeUpdate = basketItemRepository.findAll().collectList().block().size();

        // Update the basketItem using partial update
        BasketItem partialUpdatedBasketItem = new BasketItem();
        partialUpdatedBasketItem.setId(basketItem.getId());

        partialUpdatedBasketItem
            .productId(UPDATED_PRODUCT_ID)
            .productName(UPDATED_PRODUCT_NAME)
            .unitPrice(UPDATED_UNIT_PRICE)
            .oldUnitPrice(UPDATED_OLD_UNIT_PRICE)
            .quantity(UPDATED_QUANTITY)
            .pictureUrl(UPDATED_PICTURE_URL)
            .userLogin(UPDATED_USER_LOGIN);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBasketItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBasketItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
        BasketItem testBasketItem = basketItemList.get(basketItemList.size() - 1);
        assertThat(testBasketItem.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testBasketItem.getProductName()).isEqualTo(UPDATED_PRODUCT_NAME);
        assertThat(testBasketItem.getUnitPrice()).isEqualByComparingTo(UPDATED_UNIT_PRICE);
        assertThat(testBasketItem.getOldUnitPrice()).isEqualByComparingTo(UPDATED_OLD_UNIT_PRICE);
        assertThat(testBasketItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testBasketItem.getPictureUrl()).isEqualTo(UPDATED_PICTURE_URL);
        assertThat(testBasketItem.getUserLogin()).isEqualTo(UPDATED_USER_LOGIN);
    }

    @Test
    void patchNonExistingBasketItem() throws Exception {
        int databaseSizeBeforeUpdate = basketItemRepository.findAll().collectList().block().size();
        basketItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, basketItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBasketItem() throws Exception {
        int databaseSizeBeforeUpdate = basketItemRepository.findAll().collectList().block().size();
        basketItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBasketItem() throws Exception {
        int databaseSizeBeforeUpdate = basketItemRepository.findAll().collectList().block().size();
        basketItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketItem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBasketItem() {
        // Initialize the database
        basketItemRepository.save(basketItem).block();

        int databaseSizeBeforeDelete = basketItemRepository.findAll().collectList().block().size();

        // Delete the basketItem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, basketItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<BasketItem> basketItemList = basketItemRepository.findAll().collectList().block();
        assertThat(basketItemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
