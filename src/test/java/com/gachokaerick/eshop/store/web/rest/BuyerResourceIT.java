package com.gachokaerick.eshop.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.gachokaerick.eshop.store.IntegrationTest;
import com.gachokaerick.eshop.store.domain.Buyer;
import com.gachokaerick.eshop.store.domain.User;
import com.gachokaerick.eshop.store.domain.enumeration.Gender;
import com.gachokaerick.eshop.store.repository.BuyerRepository;
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
 * Integration tests for the {@link BuyerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class BuyerResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final String DEFAULT_EMAIL = "u*\"L]h@$F./";
    private static final String UPDATED_EMAIL = "-tS{k*@ZXZ:.tmC";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/buyers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Buyer buyer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Buyer createEntity(EntityManager em) {
        Buyer buyer = new Buyer()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .gender(DEFAULT_GENDER)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        buyer.setUser(user);
        return buyer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Buyer createUpdatedEntity(EntityManager em) {
        Buyer buyer = new Buyer()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        buyer.setUser(user);
        return buyer;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Buyer.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        UserResourceIT.deleteEntities(em);
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
        buyer = createEntity(em);
    }

    @Test
    void createBuyer() throws Exception {
        int databaseSizeBeforeCreate = buyerRepository.findAll().collectList().block().size();
        // Create the Buyer
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(buyer))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Buyer in the database
        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeCreate + 1);
        Buyer testBuyer = buyerList.get(buyerList.size() - 1);
        assertThat(testBuyer.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testBuyer.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testBuyer.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testBuyer.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testBuyer.getPhone()).isEqualTo(DEFAULT_PHONE);
    }

    @Test
    void createBuyerWithExistingId() throws Exception {
        // Create the Buyer with an existing ID
        buyer.setId(1L);

        int databaseSizeBeforeCreate = buyerRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(buyer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Buyer in the database
        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = buyerRepository.findAll().collectList().block().size();
        // set the field null
        buyer.setFirstName(null);

        // Create the Buyer, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(buyer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = buyerRepository.findAll().collectList().block().size();
        // set the field null
        buyer.setLastName(null);

        // Create the Buyer, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(buyer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkGenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = buyerRepository.findAll().collectList().block().size();
        // set the field null
        buyer.setGender(null);

        // Create the Buyer, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(buyer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = buyerRepository.findAll().collectList().block().size();
        // set the field null
        buyer.setEmail(null);

        // Create the Buyer, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(buyer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = buyerRepository.findAll().collectList().block().size();
        // set the field null
        buyer.setPhone(null);

        // Create the Buyer, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(buyer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllBuyers() {
        // Initialize the database
        buyerRepository.save(buyer).block();

        // Get all the buyerList
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
            .value(hasItem(buyer.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].gender")
            .value(hasItem(DEFAULT_GENDER.toString()))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE));
    }

    @Test
    void getBuyer() {
        // Initialize the database
        buyerRepository.save(buyer).block();

        // Get the buyer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, buyer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(buyer.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.gender")
            .value(is(DEFAULT_GENDER.toString()))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE));
    }

    @Test
    void getNonExistingBuyer() {
        // Get the buyer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewBuyer() throws Exception {
        // Initialize the database
        buyerRepository.save(buyer).block();

        int databaseSizeBeforeUpdate = buyerRepository.findAll().collectList().block().size();

        // Update the buyer
        Buyer updatedBuyer = buyerRepository.findById(buyer.getId()).block();
        updatedBuyer
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedBuyer.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedBuyer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Buyer in the database
        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeUpdate);
        Buyer testBuyer = buyerList.get(buyerList.size() - 1);
        assertThat(testBuyer.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testBuyer.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testBuyer.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testBuyer.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testBuyer.getPhone()).isEqualTo(UPDATED_PHONE);
    }

    @Test
    void putNonExistingBuyer() throws Exception {
        int databaseSizeBeforeUpdate = buyerRepository.findAll().collectList().block().size();
        buyer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, buyer.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(buyer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Buyer in the database
        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBuyer() throws Exception {
        int databaseSizeBeforeUpdate = buyerRepository.findAll().collectList().block().size();
        buyer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(buyer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Buyer in the database
        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBuyer() throws Exception {
        int databaseSizeBeforeUpdate = buyerRepository.findAll().collectList().block().size();
        buyer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(buyer))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Buyer in the database
        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBuyerWithPatch() throws Exception {
        // Initialize the database
        buyerRepository.save(buyer).block();

        int databaseSizeBeforeUpdate = buyerRepository.findAll().collectList().block().size();

        // Update the buyer using partial update
        Buyer partialUpdatedBuyer = new Buyer();
        partialUpdatedBuyer.setId(buyer.getId());

        partialUpdatedBuyer.lastName(UPDATED_LAST_NAME).gender(UPDATED_GENDER).email(UPDATED_EMAIL).phone(UPDATED_PHONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBuyer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBuyer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Buyer in the database
        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeUpdate);
        Buyer testBuyer = buyerList.get(buyerList.size() - 1);
        assertThat(testBuyer.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testBuyer.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testBuyer.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testBuyer.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testBuyer.getPhone()).isEqualTo(UPDATED_PHONE);
    }

    @Test
    void fullUpdateBuyerWithPatch() throws Exception {
        // Initialize the database
        buyerRepository.save(buyer).block();

        int databaseSizeBeforeUpdate = buyerRepository.findAll().collectList().block().size();

        // Update the buyer using partial update
        Buyer partialUpdatedBuyer = new Buyer();
        partialUpdatedBuyer.setId(buyer.getId());

        partialUpdatedBuyer
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBuyer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBuyer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Buyer in the database
        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeUpdate);
        Buyer testBuyer = buyerList.get(buyerList.size() - 1);
        assertThat(testBuyer.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testBuyer.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testBuyer.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testBuyer.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testBuyer.getPhone()).isEqualTo(UPDATED_PHONE);
    }

    @Test
    void patchNonExistingBuyer() throws Exception {
        int databaseSizeBeforeUpdate = buyerRepository.findAll().collectList().block().size();
        buyer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, buyer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(buyer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Buyer in the database
        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBuyer() throws Exception {
        int databaseSizeBeforeUpdate = buyerRepository.findAll().collectList().block().size();
        buyer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(buyer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Buyer in the database
        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBuyer() throws Exception {
        int databaseSizeBeforeUpdate = buyerRepository.findAll().collectList().block().size();
        buyer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(buyer))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Buyer in the database
        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBuyer() {
        // Initialize the database
        buyerRepository.save(buyer).block();

        int databaseSizeBeforeDelete = buyerRepository.findAll().collectList().block().size();

        // Delete the buyer
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, buyer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Buyer> buyerList = buyerRepository.findAll().collectList().block();
        assertThat(buyerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
