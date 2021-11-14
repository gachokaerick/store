package com.gachokaerick.eshop.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.gachokaerick.eshop.store.IntegrationTest;
import com.gachokaerick.eshop.store.domain.CatalogType;
import com.gachokaerick.eshop.store.repository.CatalogTypeRepository;
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
 * Integration tests for the {@link CatalogTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CatalogTypeResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/catalog-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CatalogTypeRepository catalogTypeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CatalogType catalogType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CatalogType createEntity(EntityManager em) {
        CatalogType catalogType = new CatalogType().type(DEFAULT_TYPE);
        return catalogType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CatalogType createUpdatedEntity(EntityManager em) {
        CatalogType catalogType = new CatalogType().type(UPDATED_TYPE);
        return catalogType;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CatalogType.class).block();
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
        catalogType = createEntity(em);
    }

    @Test
    void createCatalogType() throws Exception {
        int databaseSizeBeforeCreate = catalogTypeRepository.findAll().collectList().block().size();
        // Create the CatalogType
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogType))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CatalogType in the database
        List<CatalogType> catalogTypeList = catalogTypeRepository.findAll().collectList().block();
        assertThat(catalogTypeList).hasSize(databaseSizeBeforeCreate + 1);
        CatalogType testCatalogType = catalogTypeList.get(catalogTypeList.size() - 1);
        assertThat(testCatalogType.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    void createCatalogTypeWithExistingId() throws Exception {
        // Create the CatalogType with an existing ID
        catalogType.setId(1L);

        int databaseSizeBeforeCreate = catalogTypeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogType))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogType in the database
        List<CatalogType> catalogTypeList = catalogTypeRepository.findAll().collectList().block();
        assertThat(catalogTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = catalogTypeRepository.findAll().collectList().block().size();
        // set the field null
        catalogType.setType(null);

        // Create the CatalogType, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogType))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CatalogType> catalogTypeList = catalogTypeRepository.findAll().collectList().block();
        assertThat(catalogTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCatalogTypes() {
        // Initialize the database
        catalogTypeRepository.save(catalogType).block();

        // Get all the catalogTypeList
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
            .value(hasItem(catalogType.getId().intValue()))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE));
    }

    @Test
    void getCatalogType() {
        // Initialize the database
        catalogTypeRepository.save(catalogType).block();

        // Get the catalogType
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, catalogType.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(catalogType.getId().intValue()))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE));
    }

    @Test
    void getNonExistingCatalogType() {
        // Get the catalogType
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCatalogType() throws Exception {
        // Initialize the database
        catalogTypeRepository.save(catalogType).block();

        int databaseSizeBeforeUpdate = catalogTypeRepository.findAll().collectList().block().size();

        // Update the catalogType
        CatalogType updatedCatalogType = catalogTypeRepository.findById(catalogType.getId()).block();
        updatedCatalogType.type(UPDATED_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCatalogType.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCatalogType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CatalogType in the database
        List<CatalogType> catalogTypeList = catalogTypeRepository.findAll().collectList().block();
        assertThat(catalogTypeList).hasSize(databaseSizeBeforeUpdate);
        CatalogType testCatalogType = catalogTypeList.get(catalogTypeList.size() - 1);
        assertThat(testCatalogType.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void putNonExistingCatalogType() throws Exception {
        int databaseSizeBeforeUpdate = catalogTypeRepository.findAll().collectList().block().size();
        catalogType.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, catalogType.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogType))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogType in the database
        List<CatalogType> catalogTypeList = catalogTypeRepository.findAll().collectList().block();
        assertThat(catalogTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCatalogType() throws Exception {
        int databaseSizeBeforeUpdate = catalogTypeRepository.findAll().collectList().block().size();
        catalogType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogType))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogType in the database
        List<CatalogType> catalogTypeList = catalogTypeRepository.findAll().collectList().block();
        assertThat(catalogTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCatalogType() throws Exception {
        int databaseSizeBeforeUpdate = catalogTypeRepository.findAll().collectList().block().size();
        catalogType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogType))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CatalogType in the database
        List<CatalogType> catalogTypeList = catalogTypeRepository.findAll().collectList().block();
        assertThat(catalogTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCatalogTypeWithPatch() throws Exception {
        // Initialize the database
        catalogTypeRepository.save(catalogType).block();

        int databaseSizeBeforeUpdate = catalogTypeRepository.findAll().collectList().block().size();

        // Update the catalogType using partial update
        CatalogType partialUpdatedCatalogType = new CatalogType();
        partialUpdatedCatalogType.setId(catalogType.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCatalogType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCatalogType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CatalogType in the database
        List<CatalogType> catalogTypeList = catalogTypeRepository.findAll().collectList().block();
        assertThat(catalogTypeList).hasSize(databaseSizeBeforeUpdate);
        CatalogType testCatalogType = catalogTypeList.get(catalogTypeList.size() - 1);
        assertThat(testCatalogType.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    void fullUpdateCatalogTypeWithPatch() throws Exception {
        // Initialize the database
        catalogTypeRepository.save(catalogType).block();

        int databaseSizeBeforeUpdate = catalogTypeRepository.findAll().collectList().block().size();

        // Update the catalogType using partial update
        CatalogType partialUpdatedCatalogType = new CatalogType();
        partialUpdatedCatalogType.setId(catalogType.getId());

        partialUpdatedCatalogType.type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCatalogType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCatalogType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CatalogType in the database
        List<CatalogType> catalogTypeList = catalogTypeRepository.findAll().collectList().block();
        assertThat(catalogTypeList).hasSize(databaseSizeBeforeUpdate);
        CatalogType testCatalogType = catalogTypeList.get(catalogTypeList.size() - 1);
        assertThat(testCatalogType.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void patchNonExistingCatalogType() throws Exception {
        int databaseSizeBeforeUpdate = catalogTypeRepository.findAll().collectList().block().size();
        catalogType.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, catalogType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogType))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogType in the database
        List<CatalogType> catalogTypeList = catalogTypeRepository.findAll().collectList().block();
        assertThat(catalogTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCatalogType() throws Exception {
        int databaseSizeBeforeUpdate = catalogTypeRepository.findAll().collectList().block().size();
        catalogType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogType))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CatalogType in the database
        List<CatalogType> catalogTypeList = catalogTypeRepository.findAll().collectList().block();
        assertThat(catalogTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCatalogType() throws Exception {
        int databaseSizeBeforeUpdate = catalogTypeRepository.findAll().collectList().block().size();
        catalogType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(catalogType))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CatalogType in the database
        List<CatalogType> catalogTypeList = catalogTypeRepository.findAll().collectList().block();
        assertThat(catalogTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCatalogType() {
        // Initialize the database
        catalogTypeRepository.save(catalogType).block();

        int databaseSizeBeforeDelete = catalogTypeRepository.findAll().collectList().block().size();

        // Delete the catalogType
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, catalogType.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CatalogType> catalogTypeList = catalogTypeRepository.findAll().collectList().block();
        assertThat(catalogTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
