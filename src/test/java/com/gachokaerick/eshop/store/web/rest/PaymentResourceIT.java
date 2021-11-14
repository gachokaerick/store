package com.gachokaerick.eshop.store.web.rest;

import static com.gachokaerick.eshop.store.web.rest.TestUtil.sameInstant;
import static com.gachokaerick.eshop.store.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.gachokaerick.eshop.store.IntegrationTest;
import com.gachokaerick.eshop.store.domain.Order;
import com.gachokaerick.eshop.store.domain.Payment;
import com.gachokaerick.eshop.store.repository.PaymentRepository;
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
 * Integration tests for the {@link PaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PaymentResourceIT {

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

    private static final String ENTITY_API_URL = "/api/payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Payment payment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createEntity(EntityManager em) {
        Payment payment = new Payment()
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
            .paymentId(DEFAULT_PAYMENT_ID);
        // Add required entity
        Order order;
        order = em.insert(OrderResourceIT.createEntity(em)).block();
        payment.setOrder(order);
        return payment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createUpdatedEntity(EntityManager em) {
        Payment payment = new Payment()
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
            .paymentId(UPDATED_PAYMENT_ID);
        // Add required entity
        Order order;
        order = em.insert(OrderResourceIT.createUpdatedEntity(em)).block();
        payment.setOrder(order);
        return payment;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Payment.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrderResourceIT.deleteEntities(em);
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
        payment = createEntity(em);
    }

    @Test
    void createPayment() throws Exception {
        int databaseSizeBeforeCreate = paymentRepository.findAll().collectList().block().size();
        // Create the Payment
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(payment))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate + 1);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
        assertThat(testPayment.getUpdateTime()).isEqualTo(DEFAULT_UPDATE_TIME);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);
        assertThat(testPayment.getPayerCountryCode()).isEqualTo(DEFAULT_PAYER_COUNTRY_CODE);
        assertThat(testPayment.getPayerEmail()).isEqualTo(DEFAULT_PAYER_EMAIL);
        assertThat(testPayment.getPayerName()).isEqualTo(DEFAULT_PAYER_NAME);
        assertThat(testPayment.getPayerSurname()).isEqualTo(DEFAULT_PAYER_SURNAME);
        assertThat(testPayment.getPayerId()).isEqualTo(DEFAULT_PAYER_ID);
        assertThat(testPayment.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testPayment.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testPayment.getPaymentId()).isEqualTo(DEFAULT_PAYMENT_ID);
    }

    @Test
    void createPaymentWithExistingId() throws Exception {
        // Create the Payment with an existing ID
        payment.setId(1L);

        int databaseSizeBeforeCreate = paymentRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(payment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkCreateTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().collectList().block().size();
        // set the field null
        payment.setCreateTime(null);

        // Create the Payment, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(payment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUpdateTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().collectList().block().size();
        // set the field null
        payment.setUpdateTime(null);

        // Create the Payment, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(payment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPayerNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().collectList().block().size();
        // set the field null
        payment.setPayerName(null);

        // Create the Payment, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(payment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPayerIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().collectList().block().size();
        // set the field null
        payment.setPayerId(null);

        // Create the Payment, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(payment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCurrencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().collectList().block().size();
        // set the field null
        payment.setCurrency(null);

        // Create the Payment, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(payment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().collectList().block().size();
        // set the field null
        payment.setAmount(null);

        // Create the Payment, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(payment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPayments() {
        // Initialize the database
        paymentRepository.save(payment).block();

        // Get all the paymentList
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
            .value(hasItem(payment.getId().intValue()))
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
            .value(hasItem(DEFAULT_PAYMENT_ID));
    }

    @Test
    void getPayment() {
        // Initialize the database
        paymentRepository.save(payment).block();

        // Get the payment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, payment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(payment.getId().intValue()))
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
            .value(is(DEFAULT_PAYMENT_ID));
    }

    @Test
    void getNonExistingPayment() {
        // Get the payment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPayment() throws Exception {
        // Initialize the database
        paymentRepository.save(payment).block();

        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();

        // Update the payment
        Payment updatedPayment = paymentRepository.findById(payment.getId()).block();
        updatedPayment
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
            .paymentId(UPDATED_PAYMENT_ID);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPayment.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPayment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
        assertThat(testPayment.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);
        assertThat(testPayment.getPayerCountryCode()).isEqualTo(UPDATED_PAYER_COUNTRY_CODE);
        assertThat(testPayment.getPayerEmail()).isEqualTo(UPDATED_PAYER_EMAIL);
        assertThat(testPayment.getPayerName()).isEqualTo(UPDATED_PAYER_NAME);
        assertThat(testPayment.getPayerSurname()).isEqualTo(UPDATED_PAYER_SURNAME);
        assertThat(testPayment.getPayerId()).isEqualTo(UPDATED_PAYER_ID);
        assertThat(testPayment.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testPayment.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPayment.getPaymentId()).isEqualTo(UPDATED_PAYMENT_ID);
    }

    @Test
    void putNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, payment.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(payment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(payment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(payment))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        paymentRepository.save(payment).block();

        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
            .updateTime(UPDATED_UPDATE_TIME)
            .payerEmail(UPDATED_PAYER_EMAIL)
            .payerSurname(UPDATED_PAYER_SURNAME)
            .amount(UPDATED_AMOUNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
        assertThat(testPayment.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);
        assertThat(testPayment.getPayerCountryCode()).isEqualTo(DEFAULT_PAYER_COUNTRY_CODE);
        assertThat(testPayment.getPayerEmail()).isEqualTo(UPDATED_PAYER_EMAIL);
        assertThat(testPayment.getPayerName()).isEqualTo(DEFAULT_PAYER_NAME);
        assertThat(testPayment.getPayerSurname()).isEqualTo(UPDATED_PAYER_SURNAME);
        assertThat(testPayment.getPayerId()).isEqualTo(DEFAULT_PAYER_ID);
        assertThat(testPayment.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testPayment.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testPayment.getPaymentId()).isEqualTo(DEFAULT_PAYMENT_ID);
    }

    @Test
    void fullUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        paymentRepository.save(payment).block();

        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
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
            .paymentId(UPDATED_PAYMENT_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
        assertThat(testPayment.getUpdateTime()).isEqualTo(UPDATED_UPDATE_TIME);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);
        assertThat(testPayment.getPayerCountryCode()).isEqualTo(UPDATED_PAYER_COUNTRY_CODE);
        assertThat(testPayment.getPayerEmail()).isEqualTo(UPDATED_PAYER_EMAIL);
        assertThat(testPayment.getPayerName()).isEqualTo(UPDATED_PAYER_NAME);
        assertThat(testPayment.getPayerSurname()).isEqualTo(UPDATED_PAYER_SURNAME);
        assertThat(testPayment.getPayerId()).isEqualTo(UPDATED_PAYER_ID);
        assertThat(testPayment.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testPayment.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testPayment.getPaymentId()).isEqualTo(UPDATED_PAYMENT_ID);
    }

    @Test
    void patchNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, payment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(payment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(payment))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(payment))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePayment() {
        // Initialize the database
        paymentRepository.save(payment).block();

        int databaseSizeBeforeDelete = paymentRepository.findAll().collectList().block().size();

        // Delete the payment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, payment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
