package com.gachokaerick.eshop.store.web.rest;

import static com.gachokaerick.eshop.store.web.rest.TestUtil.sameInstant;
import static com.gachokaerick.eshop.store.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.gachokaerick.eshop.store.IntegrationTest;
import com.gachokaerick.eshop.store.domain.BasketCheckout;
import com.gachokaerick.eshop.store.repository.BasketCheckoutRepository;
import com.gachokaerick.eshop.store.service.EntityManager;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link BasketCheckoutResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class BasketCheckoutResourceIT {

    private static final String DEFAULT_STREET = "AAAAAAAAAA";
    private static final String UPDATED_STREET = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_TOWN = "AAAAAAAAAA";
    private static final String UPDATED_TOWN = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String DEFAULT_ZIPCODE = "AAAAAAAAAA";
    private static final String UPDATED_ZIPCODE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_PAYMENT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_PAYER_COUNTRY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_PAYER_COUNTRY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_PAYER_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_PAYER_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PAYER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PAYER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PAYER_SURNAME = "AAAAAAAAAA";
    private static final String UPDATED_PAYER_SURNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PAYER_ID = "AAAAAAAAAA";
    private static final String UPDATED_PAYER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_PAYMENT_ID = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_ID = "BBBBBBBBBB";

    private static final String DEFAULT_USER_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_USER_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/basket-checkouts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BasketCheckoutRepository basketCheckoutRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private BasketCheckout basketCheckout;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BasketCheckout createEntity(EntityManager em) {
        BasketCheckout basketCheckout = new BasketCheckout()
            .street(DEFAULT_STREET)
            .city(DEFAULT_CITY)
            .town(DEFAULT_TOWN)
            .country(DEFAULT_COUNTRY)
            .zipcode(DEFAULT_ZIPCODE)
            .createTime(DEFAULT_CREATE_TIME)
            .updateTime(DEFAULT_UPDATE_TIME)
            .paymentStatus(DEFAULT_PAYMENT_STATUS)
            .payerCountryCode(DEFAULT_PAYER_COUNTRY_CODE)
            .payerEmail(DEFAULT_PAYER_EMAIL)
            .payerName(DEFAULT_PAYER_NAME)
            .payerSurname(DEFAULT_PAYER_SURNAME)
            .payerId(DEFAULT_PAYER_ID)
            .currency(DEFAULT_CURRENCY)
            .amount(DEFAULT_AMOUNT)
            .paymentId(DEFAULT_PAYMENT_ID)
            .userLogin(DEFAULT_USER_LOGIN)
            .description(DEFAULT_DESCRIPTION);
        return basketCheckout;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BasketCheckout createUpdatedEntity(EntityManager em) {
        BasketCheckout basketCheckout = new BasketCheckout()
            .street(UPDATED_STREET)
            .city(UPDATED_CITY)
            .town(UPDATED_TOWN)
            .country(UPDATED_COUNTRY)
            .zipcode(UPDATED_ZIPCODE)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .payerCountryCode(UPDATED_PAYER_COUNTRY_CODE)
            .payerEmail(UPDATED_PAYER_EMAIL)
            .payerName(UPDATED_PAYER_NAME)
            .payerSurname(UPDATED_PAYER_SURNAME)
            .payerId(UPDATED_PAYER_ID)
            .currency(UPDATED_CURRENCY)
            .amount(UPDATED_AMOUNT)
            .paymentId(UPDATED_PAYMENT_ID)
            .userLogin(UPDATED_USER_LOGIN)
            .description(UPDATED_DESCRIPTION);
        return basketCheckout;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(BasketCheckout.class).block();
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
        basketCheckout = createEntity(em);
    }

    @Test
    void createBasketCheckout() throws Exception {
        int databaseSizeBeforeCreate = basketCheckoutRepository.findAll().collectList().block().size();
        // Create the BasketCheckout
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the BasketCheckout in the database
        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeCreate + 1);
        BasketCheckout testBasketCheckout = basketCheckoutList.get(basketCheckoutList.size() - 1);
        assertThat(testBasketCheckout.getStreet()).isEqualTo(DEFAULT_STREET);
        assertThat(testBasketCheckout.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testBasketCheckout.getTown()).isEqualTo(DEFAULT_TOWN);
        assertThat(testBasketCheckout.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testBasketCheckout.getZipcode()).isEqualTo(DEFAULT_ZIPCODE);
        assertThat(testBasketCheckout.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
        assertThat(testBasketCheckout.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);
        assertThat(testBasketCheckout.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);
        assertThat(testBasketCheckout.getPayerCountryCode()).isEqualTo(DEFAULT_PAYER_COUNTRY_CODE);
        assertThat(testBasketCheckout.getPayerEmail()).isEqualTo(DEFAULT_PAYER_EMAIL);
        assertThat(testBasketCheckout.getPayerName()).isEqualTo(DEFAULT_PAYER_NAME);
        assertThat(testBasketCheckout.getPayerSurname()).isEqualTo(DEFAULT_PAYER_SURNAME);
        assertThat(testBasketCheckout.getPayerId()).isEqualTo(DEFAULT_PAYER_ID);
        assertThat(testBasketCheckout.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testBasketCheckout.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testBasketCheckout.getPaymentId()).isEqualTo(DEFAULT_PAYMENT_ID);
        assertThat(testBasketCheckout.getUserLogin()).isEqualTo(DEFAULT_USER_LOGIN);
        assertThat(testBasketCheckout.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createBasketCheckoutWithExistingId() throws Exception {
        // Create the BasketCheckout with an existing ID
        basketCheckout.setId(1L);

        int databaseSizeBeforeCreate = basketCheckoutRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BasketCheckout in the database
        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketCheckoutRepository.findAll().collectList().block().size();
        // set the field null
        basketCheckout.setCity(null);

        // Create the BasketCheckout, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTownIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketCheckoutRepository.findAll().collectList().block().size();
        // set the field null
        basketCheckout.setTown(null);

        // Create the BasketCheckout, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCountryIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketCheckoutRepository.findAll().collectList().block().size();
        // set the field null
        basketCheckout.setCountry(null);

        // Create the BasketCheckout, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCreateTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketCheckoutRepository.findAll().collectList().block().size();
        // set the field null
        basketCheckout.setCreateTime(null);

        // Create the BasketCheckout, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUpdateTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketCheckoutRepository.findAll().collectList().block().size();
        // set the field null
        basketCheckout.setUpdateTime(null);

        // Create the BasketCheckout, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPayerNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketCheckoutRepository.findAll().collectList().block().size();
        // set the field null
        basketCheckout.setPayerName(null);

        // Create the BasketCheckout, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPayerIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketCheckoutRepository.findAll().collectList().block().size();
        // set the field null
        basketCheckout.setPayerId(null);

        // Create the BasketCheckout, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCurrencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketCheckoutRepository.findAll().collectList().block().size();
        // set the field null
        basketCheckout.setCurrency(null);

        // Create the BasketCheckout, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketCheckoutRepository.findAll().collectList().block().size();
        // set the field null
        basketCheckout.setAmount(null);

        // Create the BasketCheckout, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUserLoginIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketCheckoutRepository.findAll().collectList().block().size();
        // set the field null
        basketCheckout.setUserLogin(null);

        // Create the BasketCheckout, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllBasketCheckouts() {
        // Initialize the database
        basketCheckoutRepository.save(basketCheckout).block();

        // Get all the basketCheckoutList
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
            .value(hasItem(basketCheckout.getId().intValue()))
            .jsonPath("$.[*].street")
            .value(hasItem(DEFAULT_STREET))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].town")
            .value(hasItem(DEFAULT_TOWN))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY))
            .jsonPath("$.[*].zipcode")
            .value(hasItem(DEFAULT_ZIPCODE))
            .jsonPath("$.[*].createTime")
            .value(hasItem(sameInstant(DEFAULT_CREATE_TIME)))
            .jsonPath("$.[*].updateTime")
            .value(hasItem(sameInstant(DEFAULT_UPDATE_TIME)))
            .jsonPath("$.[*].paymentStatus")
            .value(hasItem(DEFAULT_PAYMENT_STATUS))
            .jsonPath("$.[*].payerCountryCode")
            .value(hasItem(DEFAULT_PAYER_COUNTRY_CODE))
            .jsonPath("$.[*].payerEmail")
            .value(hasItem(DEFAULT_PAYER_EMAIL))
            .jsonPath("$.[*].payerName")
            .value(hasItem(DEFAULT_PAYER_NAME))
            .jsonPath("$.[*].payerSurname")
            .value(hasItem(DEFAULT_PAYER_SURNAME))
            .jsonPath("$.[*].payerId")
            .value(hasItem(DEFAULT_PAYER_ID))
            .jsonPath("$.[*].currency")
            .value(hasItem(DEFAULT_CURRENCY))
            .jsonPath("$.[*].amount")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.[*].paymentId")
            .value(hasItem(DEFAULT_PAYMENT_ID))
            .jsonPath("$.[*].userLogin")
            .value(hasItem(DEFAULT_USER_LOGIN))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getBasketCheckout() {
        // Initialize the database
        basketCheckoutRepository.save(basketCheckout).block();

        // Get the basketCheckout
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, basketCheckout.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(basketCheckout.getId().intValue()))
            .jsonPath("$.street")
            .value(is(DEFAULT_STREET))
            .jsonPath("$.city")
            .value(is(DEFAULT_CITY))
            .jsonPath("$.town")
            .value(is(DEFAULT_TOWN))
            .jsonPath("$.country")
            .value(is(DEFAULT_COUNTRY))
            .jsonPath("$.zipcode")
            .value(is(DEFAULT_ZIPCODE))
            .jsonPath("$.createTime")
            .value(is(sameInstant(DEFAULT_CREATE_TIME)))
            .jsonPath("$.updateTime")
            .value(is(sameInstant(DEFAULT_UPDATE_TIME)))
            .jsonPath("$.paymentStatus")
            .value(is(DEFAULT_PAYMENT_STATUS))
            .jsonPath("$.payerCountryCode")
            .value(is(DEFAULT_PAYER_COUNTRY_CODE))
            .jsonPath("$.payerEmail")
            .value(is(DEFAULT_PAYER_EMAIL))
            .jsonPath("$.payerName")
            .value(is(DEFAULT_PAYER_NAME))
            .jsonPath("$.payerSurname")
            .value(is(DEFAULT_PAYER_SURNAME))
            .jsonPath("$.payerId")
            .value(is(DEFAULT_PAYER_ID))
            .jsonPath("$.currency")
            .value(is(DEFAULT_CURRENCY))
            .jsonPath("$.amount")
            .value(is(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.paymentId")
            .value(is(DEFAULT_PAYMENT_ID))
            .jsonPath("$.userLogin")
            .value(is(DEFAULT_USER_LOGIN))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingBasketCheckout() {
        // Get the basketCheckout
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewBasketCheckout() throws Exception {
        // Initialize the database
        basketCheckoutRepository.save(basketCheckout).block();

        int databaseSizeBeforeUpdate = basketCheckoutRepository.findAll().collectList().block().size();

        // Update the basketCheckout
        BasketCheckout updatedBasketCheckout = basketCheckoutRepository.findById(basketCheckout.getId()).block();
        updatedBasketCheckout
            .street(UPDATED_STREET)
            .city(UPDATED_CITY)
            .town(UPDATED_TOWN)
            .country(UPDATED_COUNTRY)
            .zipcode(UPDATED_ZIPCODE)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .payerCountryCode(UPDATED_PAYER_COUNTRY_CODE)
            .payerEmail(UPDATED_PAYER_EMAIL)
            .payerName(UPDATED_PAYER_NAME)
            .payerSurname(UPDATED_PAYER_SURNAME)
            .payerId(UPDATED_PAYER_ID)
            .currency(UPDATED_CURRENCY)
            .amount(UPDATED_AMOUNT)
            .paymentId(UPDATED_PAYMENT_ID)
            .userLogin(UPDATED_USER_LOGIN)
            .description(UPDATED_DESCRIPTION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedBasketCheckout.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedBasketCheckout))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BasketCheckout in the database
        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeUpdate);
        BasketCheckout testBasketCheckout = basketCheckoutList.get(basketCheckoutList.size() - 1);
        assertThat(testBasketCheckout.getStreet()).isEqualTo(UPDATED_STREET);
        assertThat(testBasketCheckout.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testBasketCheckout.getTown()).isEqualTo(UPDATED_TOWN);
        assertThat(testBasketCheckout.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testBasketCheckout.getZipcode()).isEqualTo(UPDATED_ZIPCODE);
        assertThat(testBasketCheckout.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
        assertThat(testBasketCheckout.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
        assertThat(testBasketCheckout.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);
        assertThat(testBasketCheckout.getPayerCountryCode()).isEqualTo(UPDATED_PAYER_COUNTRY_CODE);
        assertThat(testBasketCheckout.getPayerEmail()).isEqualTo(UPDATED_PAYER_EMAIL);
        assertThat(testBasketCheckout.getPayerName()).isEqualTo(UPDATED_PAYER_NAME);
        assertThat(testBasketCheckout.getPayerSurname()).isEqualTo(UPDATED_PAYER_SURNAME);
        assertThat(testBasketCheckout.getPayerId()).isEqualTo(UPDATED_PAYER_ID);
        assertThat(testBasketCheckout.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testBasketCheckout.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testBasketCheckout.getPaymentId()).isEqualTo(UPDATED_PAYMENT_ID);
        assertThat(testBasketCheckout.getUserLogin()).isEqualTo(UPDATED_USER_LOGIN);
        assertThat(testBasketCheckout.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void putNonExistingBasketCheckout() throws Exception {
        int databaseSizeBeforeUpdate = basketCheckoutRepository.findAll().collectList().block().size();
        basketCheckout.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, basketCheckout.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BasketCheckout in the database
        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBasketCheckout() throws Exception {
        int databaseSizeBeforeUpdate = basketCheckoutRepository.findAll().collectList().block().size();
        basketCheckout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BasketCheckout in the database
        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBasketCheckout() throws Exception {
        int databaseSizeBeforeUpdate = basketCheckoutRepository.findAll().collectList().block().size();
        basketCheckout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BasketCheckout in the database
        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBasketCheckoutWithPatch() throws Exception {
        // Initialize the database
        basketCheckoutRepository.save(basketCheckout).block();

        int databaseSizeBeforeUpdate = basketCheckoutRepository.findAll().collectList().block().size();

        // Update the basketCheckout using partial update
        BasketCheckout partialUpdatedBasketCheckout = new BasketCheckout();
        partialUpdatedBasketCheckout.setId(basketCheckout.getId());

        partialUpdatedBasketCheckout
            .street(UPDATED_STREET)
            .updateTime(UPDATED_UPDATE_TIME)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .payerName(UPDATED_PAYER_NAME)
            .payerSurname(UPDATED_PAYER_SURNAME)
            .payerId(UPDATED_PAYER_ID)
            .amount(UPDATED_AMOUNT)
            .paymentId(UPDATED_PAYMENT_ID)
            .description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBasketCheckout.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBasketCheckout))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BasketCheckout in the database
        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeUpdate);
        BasketCheckout testBasketCheckout = basketCheckoutList.get(basketCheckoutList.size() - 1);
        assertThat(testBasketCheckout.getStreet()).isEqualTo(UPDATED_STREET);
        assertThat(testBasketCheckout.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testBasketCheckout.getTown()).isEqualTo(DEFAULT_TOWN);
        assertThat(testBasketCheckout.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testBasketCheckout.getZipcode()).isEqualTo(DEFAULT_ZIPCODE);
        assertThat(testBasketCheckout.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
        assertThat(testBasketCheckout.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
        assertThat(testBasketCheckout.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);
        assertThat(testBasketCheckout.getPayerCountryCode()).isEqualTo(DEFAULT_PAYER_COUNTRY_CODE);
        assertThat(testBasketCheckout.getPayerEmail()).isEqualTo(DEFAULT_PAYER_EMAIL);
        assertThat(testBasketCheckout.getPayerName()).isEqualTo(UPDATED_PAYER_NAME);
        assertThat(testBasketCheckout.getPayerSurname()).isEqualTo(UPDATED_PAYER_SURNAME);
        assertThat(testBasketCheckout.getPayerId()).isEqualTo(UPDATED_PAYER_ID);
        assertThat(testBasketCheckout.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testBasketCheckout.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testBasketCheckout.getPaymentId()).isEqualTo(UPDATED_PAYMENT_ID);
        assertThat(testBasketCheckout.getUserLogin()).isEqualTo(DEFAULT_USER_LOGIN);
        assertThat(testBasketCheckout.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void fullUpdateBasketCheckoutWithPatch() throws Exception {
        // Initialize the database
        basketCheckoutRepository.save(basketCheckout).block();

        int databaseSizeBeforeUpdate = basketCheckoutRepository.findAll().collectList().block().size();

        // Update the basketCheckout using partial update
        BasketCheckout partialUpdatedBasketCheckout = new BasketCheckout();
        partialUpdatedBasketCheckout.setId(basketCheckout.getId());

        partialUpdatedBasketCheckout
            .street(UPDATED_STREET)
            .city(UPDATED_CITY)
            .town(UPDATED_TOWN)
            .country(UPDATED_COUNTRY)
            .zipcode(UPDATED_ZIPCODE)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .payerCountryCode(UPDATED_PAYER_COUNTRY_CODE)
            .payerEmail(UPDATED_PAYER_EMAIL)
            .payerName(UPDATED_PAYER_NAME)
            .payerSurname(UPDATED_PAYER_SURNAME)
            .payerId(UPDATED_PAYER_ID)
            .currency(UPDATED_CURRENCY)
            .amount(UPDATED_AMOUNT)
            .paymentId(UPDATED_PAYMENT_ID)
            .userLogin(UPDATED_USER_LOGIN)
            .description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBasketCheckout.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBasketCheckout))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BasketCheckout in the database
        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeUpdate);
        BasketCheckout testBasketCheckout = basketCheckoutList.get(basketCheckoutList.size() - 1);
        assertThat(testBasketCheckout.getStreet()).isEqualTo(UPDATED_STREET);
        assertThat(testBasketCheckout.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testBasketCheckout.getTown()).isEqualTo(UPDATED_TOWN);
        assertThat(testBasketCheckout.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testBasketCheckout.getZipcode()).isEqualTo(UPDATED_ZIPCODE);
        assertThat(testBasketCheckout.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
        assertThat(testBasketCheckout.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
        assertThat(testBasketCheckout.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);
        assertThat(testBasketCheckout.getPayerCountryCode()).isEqualTo(UPDATED_PAYER_COUNTRY_CODE);
        assertThat(testBasketCheckout.getPayerEmail()).isEqualTo(UPDATED_PAYER_EMAIL);
        assertThat(testBasketCheckout.getPayerName()).isEqualTo(UPDATED_PAYER_NAME);
        assertThat(testBasketCheckout.getPayerSurname()).isEqualTo(UPDATED_PAYER_SURNAME);
        assertThat(testBasketCheckout.getPayerId()).isEqualTo(UPDATED_PAYER_ID);
        assertThat(testBasketCheckout.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testBasketCheckout.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testBasketCheckout.getPaymentId()).isEqualTo(UPDATED_PAYMENT_ID);
        assertThat(testBasketCheckout.getUserLogin()).isEqualTo(UPDATED_USER_LOGIN);
        assertThat(testBasketCheckout.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingBasketCheckout() throws Exception {
        int databaseSizeBeforeUpdate = basketCheckoutRepository.findAll().collectList().block().size();
        basketCheckout.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, basketCheckout.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BasketCheckout in the database
        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBasketCheckout() throws Exception {
        int databaseSizeBeforeUpdate = basketCheckoutRepository.findAll().collectList().block().size();
        basketCheckout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BasketCheckout in the database
        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBasketCheckout() throws Exception {
        int databaseSizeBeforeUpdate = basketCheckoutRepository.findAll().collectList().block().size();
        basketCheckout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(basketCheckout))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BasketCheckout in the database
        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBasketCheckout() {
        // Initialize the database
        basketCheckoutRepository.save(basketCheckout).block();

        int databaseSizeBeforeDelete = basketCheckoutRepository.findAll().collectList().block().size();

        // Delete the basketCheckout
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, basketCheckout.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<BasketCheckout> basketCheckoutList = basketCheckoutRepository.findAll().collectList().block();
        assertThat(basketCheckoutList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
