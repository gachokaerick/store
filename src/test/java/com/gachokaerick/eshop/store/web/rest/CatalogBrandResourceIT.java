package com.gachokaerick.eshop.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.gachokaerick.eshop.store.IntegrationTest;
import com.gachokaerick.eshop.store.domain.CatalogBrand;
import com.gachokaerick.eshop.store.repository.CatalogBrandRepository;
import com.gachokaerick.eshop.store.service.EntityManager;
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
 * Integration tests for the {@link CatalogBrandResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CatalogBrandResourceIT {

    private static final String DEFAULT_BRAND = "AAAAAAAAAA";
    private static final String UPDATED_BRAND = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/catalog-brands";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CatalogBrandRepository catalogBrandRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CatalogBrand catalogBrand;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CatalogBrand createEntity(EntityManager em) {
        CatalogBrand catalogBrand = new CatalogBrand().brand(DEFAULT_BRAND);
        return catalogBrand;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CatalogBrand createUpdatedEntity(EntityManager em) {
        CatalogBrand catalogBrand = new CatalogBrand().brand(UPDATED_BRAND);
        return catalogBrand;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CatalogBrand.class).block();
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
        catalogBrand = createEntity(em);
    }

    @Test
    void createCatalogBrand() throws Exception {
        int databaseSizeBeforeCreate = catalogBrandRepository.findAll().collectList().block().size();
        // Create the CatalogBrand
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogBrand))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CatalogBrand in the database
        List<CatalogBrand> catalogBrandList = catalogBrandRepository.findAll().collectList().block();
        assertThat(catalogBrandList).hasSize(databaseSizeBeforeCreate + 1);
        CatalogBrand testCatalogBrand = catalogBrandList.get(catalogBrandList.size() - 1);
        assertThat(testCatalogBrand.getBrand()).isEqualTo(DEFAULT_BRAND);
    }

    @Test
    void createCatalogBrandWithExistingId() throws Exception {
        // Create the CatalogBrand with an existing ID
        catalogBrand.setId(1L);

        int databaseSizeBeforeCreate = catalogBrandRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogBrand))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogBrand in the database
        List<CatalogBrand> catalogBrandList = catalogBrandRepository.findAll().collectList().block();
        assertThat(catalogBrandList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkBrandIsRequired() throws Exception {
        int databaseSizeBeforeTest = catalogBrandRepository.findAll().collectList().block().size();
        // set the field null
        catalogBrand.setBrand(null);

        // Create the CatalogBrand, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogBrand))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CatalogBrand> catalogBrandList = catalogBrandRepository.findAll().collectList().block();
        assertThat(catalogBrandList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCatalogBrands() {
        // Initialize the database
        catalogBrandRepository.save(catalogBrand).block();

        // Get all the catalogBrandList
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
            .value(hasItem(catalogBrand.getId().intValue()))
            .jsonPath("$.[*].brand")
            .value(hasItem(DEFAULT_BRAND));
    }

    @Test
    void getCatalogBrand() {
        // Initialize the database
        catalogBrandRepository.save(catalogBrand).block();

        // Get the catalogBrand
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, catalogBrand.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(catalogBrand.getId().intValue()))
            .jsonPath("$.brand")
            .value(is(DEFAULT_BRAND));
    }

    @Test
    void getNonExistingCatalogBrand() {
        // Get the catalogBrand
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCatalogBrand() throws Exception {
        // Initialize the database
        catalogBrandRepository.save(catalogBrand).block();

        int databaseSizeBeforeUpdate = catalogBrandRepository.findAll().collectList().block().size();

        // Update the catalogBrand
        CatalogBrand updatedCatalogBrand = catalogBrandRepository.findById(catalogBrand.getId()).block();
        updatedCatalogBrand.brand(UPDATED_BRAND);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCatalogBrand.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCatalogBrand))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CatalogBrand in the database
        List<CatalogBrand> catalogBrandList = catalogBrandRepository.findAll().collectList().block();
        assertThat(catalogBrandList).hasSize(databaseSizeBeforeUpdate);
        CatalogBrand testCatalogBrand = catalogBrandList.get(catalogBrandList.size() - 1);
        assertThat(testCatalogBrand.getBrand()).isEqualTo(UPDATED_BRAND);
    }

    @Test
    void putNonExistingCatalogBrand() throws Exception {
        int databaseSizeBeforeUpdate = catalogBrandRepository.findAll().collectList().block().size();
        catalogBrand.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, catalogBrand.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogBrand))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogBrand in the database
        List<CatalogBrand> catalogBrandList = catalogBrandRepository.findAll().collectList().block();
        assertThat(catalogBrandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCatalogBrand() throws Exception {
        int databaseSizeBeforeUpdate = catalogBrandRepository.findAll().collectList().block().size();
        catalogBrand.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogBrand))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogBrand in the database
        List<CatalogBrand> catalogBrandList = catalogBrandRepository.findAll().collectList().block();
        assertThat(catalogBrandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCatalogBrand() throws Exception {
        int databaseSizeBeforeUpdate = catalogBrandRepository.findAll().collectList().block().size();
        catalogBrand.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogBrand))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CatalogBrand in the database
        List<CatalogBrand> catalogBrandList = catalogBrandRepository.findAll().collectList().block();
        assertThat(catalogBrandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCatalogBrandWithPatch() throws Exception {
        // Initialize the database
        catalogBrandRepository.save(catalogBrand).block();

        int databaseSizeBeforeUpdate = catalogBrandRepository.findAll().collectList().block().size();

        // Update the catalogBrand using partial update
        CatalogBrand partialUpdatedCatalogBrand = new CatalogBrand();
        partialUpdatedCatalogBrand.setId(catalogBrand.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCatalogBrand.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCatalogBrand))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CatalogBrand in the database
        List<CatalogBrand> catalogBrandList = catalogBrandRepository.findAll().collectList().block();
        assertThat(catalogBrandList).hasSize(databaseSizeBeforeUpdate);
        CatalogBrand testCatalogBrand = catalogBrandList.get(catalogBrandList.size() - 1);
        assertThat(testCatalogBrand.getBrand()).isEqualTo(DEFAULT_BRAND);
    }

    @Test
    void fullUpdateCatalogBrandWithPatch() throws Exception {
        // Initialize the database
        catalogBrandRepository.save(catalogBrand).block();

        int databaseSizeBeforeUpdate = catalogBrandRepository.findAll().collectList().block().size();

        // Update the catalogBrand using partial update
        CatalogBrand partialUpdatedCatalogBrand = new CatalogBrand();
        partialUpdatedCatalogBrand.setId(catalogBrand.getId());

        partialUpdatedCatalogBrand.brand(UPDATED_BRAND);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCatalogBrand.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCatalogBrand))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CatalogBrand in the database
        List<CatalogBrand> catalogBrandList = catalogBrandRepository.findAll().collectList().block();
        assertThat(catalogBrandList).hasSize(databaseSizeBeforeUpdate);
        CatalogBrand testCatalogBrand = catalogBrandList.get(catalogBrandList.size() - 1);
        assertThat(testCatalogBrand.getBrand()).isEqualTo(UPDATED_BRAND);
    }

    @Test
    void patchNonExistingCatalogBrand() throws Exception {
        int databaseSizeBeforeUpdate = catalogBrandRepository.findAll().collectList().block().size();
        catalogBrand.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, catalogBrand.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogBrand))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogBrand in the database
        List<CatalogBrand> catalogBrandList = catalogBrandRepository.findAll().collectList().block();
        assertThat(catalogBrandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCatalogBrand() throws Exception {
        int databaseSizeBeforeUpdate = catalogBrandRepository.findAll().collectList().block().size();
        catalogBrand.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogBrand))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogBrand in the database
        List<CatalogBrand> catalogBrandList = catalogBrandRepository.findAll().collectList().block();
        assertThat(catalogBrandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCatalogBrand() throws Exception {
        int databaseSizeBeforeUpdate = catalogBrandRepository.findAll().collectList().block().size();
        catalogBrand.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogBrand))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CatalogBrand in the database
        List<CatalogBrand> catalogBrandList = catalogBrandRepository.findAll().collectList().block();
        assertThat(catalogBrandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCatalogBrand() {
        // Initialize the database
        catalogBrandRepository.save(catalogBrand).block();

        int databaseSizeBeforeDelete = catalogBrandRepository.findAll().collectList().block().size();

        // Delete the catalogBrand
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, catalogBrand.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CatalogBrand> catalogBrandList = catalogBrandRepository.findAll().collectList().block();
        assertThat(catalogBrandList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
