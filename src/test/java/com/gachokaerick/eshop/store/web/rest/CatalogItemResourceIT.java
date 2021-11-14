package com.gachokaerick.eshop.store.web.rest;

import static com.gachokaerick.eshop.store.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.gachokaerick.eshop.store.IntegrationTest;
import com.gachokaerick.eshop.store.domain.CatalogBrand;
import com.gachokaerick.eshop.store.domain.CatalogItem;
import com.gachokaerick.eshop.store.domain.CatalogType;
import com.gachokaerick.eshop.store.repository.CatalogItemRepository;
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
 * Integration tests for the {@link CatalogItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CatalogItemResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final String DEFAULT_PICTURE_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PICTURE_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PICTURE_URL = "AAAAAAAAAA";
    private static final String UPDATED_PICTURE_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_AVAILABLE_STOCK = 1;
    private static final Integer UPDATED_AVAILABLE_STOCK = 2;

    private static final Integer DEFAULT_RESTOCK_THRESHOLD = 1;
    private static final Integer UPDATED_RESTOCK_THRESHOLD = 2;

    private static final Integer DEFAULT_MAX_STOCK_THRESHOLD = 1;
    private static final Integer UPDATED_MAX_STOCK_THRESHOLD = 2;

    private static final Boolean DEFAULT_ON_REORDER = false;
    private static final Boolean UPDATED_ON_REORDER = true;

    private static final String ENTITY_API_URL = "/api/catalog-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CatalogItemRepository catalogItemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CatalogItem catalogItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CatalogItem createEntity(EntityManager em) {
        CatalogItem catalogItem = new CatalogItem()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .pictureFileName(DEFAULT_PICTURE_FILE_NAME)
            .pictureUrl(DEFAULT_PICTURE_URL)
            .availableStock(DEFAULT_AVAILABLE_STOCK)
            .restockThreshold(DEFAULT_RESTOCK_THRESHOLD)
            .maxStockThreshold(DEFAULT_MAX_STOCK_THRESHOLD)
            .onReorder(DEFAULT_ON_REORDER);
        // Add required entity
        CatalogBrand catalogBrand;
        catalogBrand = em.insert(CatalogBrandResourceIT.createEntity(em)).block();
        catalogItem.setCatalogBrand(catalogBrand);
        // Add required entity
        CatalogType catalogType;
        catalogType = em.insert(CatalogTypeResourceIT.createEntity(em)).block();
        catalogItem.setCatalogType(catalogType);
        return catalogItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CatalogItem createUpdatedEntity(EntityManager em) {
        CatalogItem catalogItem = new CatalogItem()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .pictureFileName(UPDATED_PICTURE_FILE_NAME)
            .pictureUrl(UPDATED_PICTURE_URL)
            .availableStock(UPDATED_AVAILABLE_STOCK)
            .restockThreshold(UPDATED_RESTOCK_THRESHOLD)
            .maxStockThreshold(UPDATED_MAX_STOCK_THRESHOLD)
            .onReorder(UPDATED_ON_REORDER);
        // Add required entity
        CatalogBrand catalogBrand;
        catalogBrand = em.insert(CatalogBrandResourceIT.createUpdatedEntity(em)).block();
        catalogItem.setCatalogBrand(catalogBrand);
        // Add required entity
        CatalogType catalogType;
        catalogType = em.insert(CatalogTypeResourceIT.createUpdatedEntity(em)).block();
        catalogItem.setCatalogType(catalogType);
        return catalogItem;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CatalogItem.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        CatalogBrandResourceIT.deleteEntities(em);
        CatalogTypeResourceIT.deleteEntities(em);
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
        catalogItem = createEntity(em);
    }

    @Test
    void createCatalogItem() throws Exception {
        int databaseSizeBeforeCreate = catalogItemRepository.findAll().collectList().block().size();
        // Create the CatalogItem
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogItem))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CatalogItem in the database
        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeCreate + 1);
        CatalogItem testCatalogItem = catalogItemList.get(catalogItemList.size() - 1);
        assertThat(testCatalogItem.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCatalogItem.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCatalogItem.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testCatalogItem.getPictureFileName()).isEqualTo(DEFAULT_PICTURE_FILE_NAME);
        assertThat(testCatalogItem.getPictureUrl()).isEqualTo(DEFAULT_PICTURE_URL);
        assertThat(testCatalogItem.getAvailableStock()).isEqualTo(DEFAULT_AVAILABLE_STOCK);
        assertThat(testCatalogItem.getRestockThreshold()).isEqualTo(DEFAULT_RESTOCK_THRESHOLD);
        assertThat(testCatalogItem.getMaxStockThreshold()).isEqualTo(DEFAULT_MAX_STOCK_THRESHOLD);
        assertThat(testCatalogItem.getOnReorder()).isEqualTo(DEFAULT_ON_REORDER);
    }

    @Test
    void createCatalogItemWithExistingId() throws Exception {
        // Create the CatalogItem with an existing ID
        catalogItem.setId(1L);

        int databaseSizeBeforeCreate = catalogItemRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogItem in the database
        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = catalogItemRepository.findAll().collectList().block().size();
        // set the field null
        catalogItem.setName(null);

        // Create the CatalogItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = catalogItemRepository.findAll().collectList().block().size();
        // set the field null
        catalogItem.setPrice(null);

        // Create the CatalogItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAvailableStockIsRequired() throws Exception {
        int databaseSizeBeforeTest = catalogItemRepository.findAll().collectList().block().size();
        // set the field null
        catalogItem.setAvailableStock(null);

        // Create the CatalogItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkRestockThresholdIsRequired() throws Exception {
        int databaseSizeBeforeTest = catalogItemRepository.findAll().collectList().block().size();
        // set the field null
        catalogItem.setRestockThreshold(null);

        // Create the CatalogItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkMaxStockThresholdIsRequired() throws Exception {
        int databaseSizeBeforeTest = catalogItemRepository.findAll().collectList().block().size();
        // set the field null
        catalogItem.setMaxStockThreshold(null);

        // Create the CatalogItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCatalogItems() {
        // Initialize the database
        catalogItemRepository.save(catalogItem).block();

        // Get all the catalogItemList
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
            .value(hasItem(catalogItem.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].pictureFileName")
            .value(hasItem(DEFAULT_PICTURE_FILE_NAME))
            .jsonPath("$.[*].pictureUrl")
            .value(hasItem(DEFAULT_PICTURE_URL))
            .jsonPath("$.[*].availableStock")
            .value(hasItem(DEFAULT_AVAILABLE_STOCK))
            .jsonPath("$.[*].restockThreshold")
            .value(hasItem(DEFAULT_RESTOCK_THRESHOLD))
            .jsonPath("$.[*].maxStockThreshold")
            .value(hasItem(DEFAULT_MAX_STOCK_THRESHOLD))
            .jsonPath("$.[*].onReorder")
            .value(hasItem(DEFAULT_ON_REORDER.booleanValue()));
    }

    @Test
    void getCatalogItem() {
        // Initialize the database
        catalogItemRepository.save(catalogItem).block();

        // Get the catalogItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, catalogItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(catalogItem.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.price")
            .value(is(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.pictureFileName")
            .value(is(DEFAULT_PICTURE_FILE_NAME))
            .jsonPath("$.pictureUrl")
            .value(is(DEFAULT_PICTURE_URL))
            .jsonPath("$.availableStock")
            .value(is(DEFAULT_AVAILABLE_STOCK))
            .jsonPath("$.restockThreshold")
            .value(is(DEFAULT_RESTOCK_THRESHOLD))
            .jsonPath("$.maxStockThreshold")
            .value(is(DEFAULT_MAX_STOCK_THRESHOLD))
            .jsonPath("$.onReorder")
            .value(is(DEFAULT_ON_REORDER.booleanValue()));
    }

    @Test
    void getNonExistingCatalogItem() {
        // Get the catalogItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCatalogItem() throws Exception {
        // Initialize the database
        catalogItemRepository.save(catalogItem).block();

        int databaseSizeBeforeUpdate = catalogItemRepository.findAll().collectList().block().size();

        // Update the catalogItem
        CatalogItem updatedCatalogItem = catalogItemRepository.findById(catalogItem.getId()).block();
        updatedCatalogItem
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .pictureFileName(UPDATED_PICTURE_FILE_NAME)
            .pictureUrl(UPDATED_PICTURE_URL)
            .availableStock(UPDATED_AVAILABLE_STOCK)
            .restockThreshold(UPDATED_RESTOCK_THRESHOLD)
            .maxStockThreshold(UPDATED_MAX_STOCK_THRESHOLD)
            .onReorder(UPDATED_ON_REORDER);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCatalogItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCatalogItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CatalogItem in the database
        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeUpdate);
        CatalogItem testCatalogItem = catalogItemList.get(catalogItemList.size() - 1);
        assertThat(testCatalogItem.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCatalogItem.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCatalogItem.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testCatalogItem.getPictureFileName()).isEqualTo(UPDATED_PICTURE_FILE_NAME);
        assertThat(testCatalogItem.getPictureUrl()).isEqualTo(UPDATED_PICTURE_URL);
        assertThat(testCatalogItem.getAvailableStock()).isEqualTo(UPDATED_AVAILABLE_STOCK);
        assertThat(testCatalogItem.getRestockThreshold()).isEqualTo(UPDATED_RESTOCK_THRESHOLD);
        assertThat(testCatalogItem.getMaxStockThreshold()).isEqualTo(UPDATED_MAX_STOCK_THRESHOLD);
        assertThat(testCatalogItem.getOnReorder()).isEqualTo(UPDATED_ON_REORDER);
    }

    @Test
    void putNonExistingCatalogItem() throws Exception {
        int databaseSizeBeforeUpdate = catalogItemRepository.findAll().collectList().block().size();
        catalogItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, catalogItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogItem in the database
        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCatalogItem() throws Exception {
        int databaseSizeBeforeUpdate = catalogItemRepository.findAll().collectList().block().size();
        catalogItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogItem in the database
        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCatalogItem() throws Exception {
        int databaseSizeBeforeUpdate = catalogItemRepository.findAll().collectList().block().size();
        catalogItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogItem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CatalogItem in the database
        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCatalogItemWithPatch() throws Exception {
        // Initialize the database
        catalogItemRepository.save(catalogItem).block();

        int databaseSizeBeforeUpdate = catalogItemRepository.findAll().collectList().block().size();

        // Update the catalogItem using partial update
        CatalogItem partialUpdatedCatalogItem = new CatalogItem();
        partialUpdatedCatalogItem.setId(catalogItem.getId());

        partialUpdatedCatalogItem
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .restockThreshold(UPDATED_RESTOCK_THRESHOLD)
            .maxStockThreshold(UPDATED_MAX_STOCK_THRESHOLD);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCatalogItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCatalogItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CatalogItem in the database
        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeUpdate);
        CatalogItem testCatalogItem = catalogItemList.get(catalogItemList.size() - 1);
        assertThat(testCatalogItem.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCatalogItem.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCatalogItem.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testCatalogItem.getPictureFileName()).isEqualTo(DEFAULT_PICTURE_FILE_NAME);
        assertThat(testCatalogItem.getPictureUrl()).isEqualTo(DEFAULT_PICTURE_URL);
        assertThat(testCatalogItem.getAvailableStock()).isEqualTo(DEFAULT_AVAILABLE_STOCK);
        assertThat(testCatalogItem.getRestockThreshold()).isEqualTo(UPDATED_RESTOCK_THRESHOLD);
        assertThat(testCatalogItem.getMaxStockThreshold()).isEqualTo(UPDATED_MAX_STOCK_THRESHOLD);
        assertThat(testCatalogItem.getOnReorder()).isEqualTo(DEFAULT_ON_REORDER);
    }

    @Test
    void fullUpdateCatalogItemWithPatch() throws Exception {
        // Initialize the database
        catalogItemRepository.save(catalogItem).block();

        int databaseSizeBeforeUpdate = catalogItemRepository.findAll().collectList().block().size();

        // Update the catalogItem using partial update
        CatalogItem partialUpdatedCatalogItem = new CatalogItem();
        partialUpdatedCatalogItem.setId(catalogItem.getId());

        partialUpdatedCatalogItem
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .pictureFileName(UPDATED_PICTURE_FILE_NAME)
            .pictureUrl(UPDATED_PICTURE_URL)
            .availableStock(UPDATED_AVAILABLE_STOCK)
            .restockThreshold(UPDATED_RESTOCK_THRESHOLD)
            .maxStockThreshold(UPDATED_MAX_STOCK_THRESHOLD)
            .onReorder(UPDATED_ON_REORDER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCatalogItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCatalogItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CatalogItem in the database
        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeUpdate);
        CatalogItem testCatalogItem = catalogItemList.get(catalogItemList.size() - 1);
        assertThat(testCatalogItem.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCatalogItem.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCatalogItem.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testCatalogItem.getPictureFileName()).isEqualTo(UPDATED_PICTURE_FILE_NAME);
        assertThat(testCatalogItem.getPictureUrl()).isEqualTo(UPDATED_PICTURE_URL);
        assertThat(testCatalogItem.getAvailableStock()).isEqualTo(UPDATED_AVAILABLE_STOCK);
        assertThat(testCatalogItem.getRestockThreshold()).isEqualTo(UPDATED_RESTOCK_THRESHOLD);
        assertThat(testCatalogItem.getMaxStockThreshold()).isEqualTo(UPDATED_MAX_STOCK_THRESHOLD);
        assertThat(testCatalogItem.getOnReorder()).isEqualTo(UPDATED_ON_REORDER);
    }

    @Test
    void patchNonExistingCatalogItem() throws Exception {
        int databaseSizeBeforeUpdate = catalogItemRepository.findAll().collectList().block().size();
        catalogItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, catalogItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogItem in the database
        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCatalogItem() throws Exception {
        int databaseSizeBeforeUpdate = catalogItemRepository.findAll().collectList().block().size();
        catalogItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogItem in the database
        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCatalogItem() throws Exception {
        int databaseSizeBeforeUpdate = catalogItemRepository.findAll().collectList().block().size();
        catalogItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogItem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CatalogItem in the database
        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCatalogItem() {
        // Initialize the database
        catalogItemRepository.save(catalogItem).block();

        int databaseSizeBeforeDelete = catalogItemRepository.findAll().collectList().block().size();

        // Delete the catalogItem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, catalogItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CatalogItem> catalogItemList = catalogItemRepository.findAll().collectList().block();
        assertThat(catalogItemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
