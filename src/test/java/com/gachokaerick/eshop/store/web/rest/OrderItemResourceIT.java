package com.gachokaerick.eshop.store.web.rest;

import static com.gachokaerick.eshop.store.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.gachokaerick.eshop.store.IntegrationTest;
import com.gachokaerick.eshop.store.domain.Order;
import com.gachokaerick.eshop.store.domain.OrderItem;
import com.gachokaerick.eshop.store.repository.OrderItemRepository;
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
 * Integration tests for the {@link OrderItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class OrderItemResourceIT {

    private static final String DEFAULT_PRODUCT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PICTURE_URL = "AAAAAAAAAA";
    private static final String UPDATED_PICTURE_URL = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_DISCOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT = new BigDecimal(2);

    private static final Integer DEFAULT_UNITS = 1;
    private static final Integer UPDATED_UNITS = 2;

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final String ENTITY_API_URL = "/api/order-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private OrderItem orderItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItem createEntity(EntityManager em) {
        OrderItem orderItem = new OrderItem()
            .productName(DEFAULT_PRODUCT_NAME)
            .pictureUrl(DEFAULT_PICTURE_URL)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .discount(DEFAULT_DISCOUNT)
            .units(DEFAULT_UNITS)
            .productId(DEFAULT_PRODUCT_ID);
        // Add required entity
        Order order;
        order = em.insert(OrderResourceIT.createEntity(em)).block();
        orderItem.setOrder(order);
        return orderItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItem createUpdatedEntity(EntityManager em) {
        OrderItem orderItem = new OrderItem()
            .productName(UPDATED_PRODUCT_NAME)
            .pictureUrl(UPDATED_PICTURE_URL)
            .unitPrice(UPDATED_UNIT_PRICE)
            .discount(UPDATED_DISCOUNT)
            .units(UPDATED_UNITS)
            .productId(UPDATED_PRODUCT_ID);
        // Add required entity
        Order order;
        order = em.insert(OrderResourceIT.createUpdatedEntity(em)).block();
        orderItem.setOrder(order);
        return orderItem;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(OrderItem.class).block();
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
        orderItem = createEntity(em);
    }

    @Test
    void createOrderItem() throws Exception {
        int databaseSizeBeforeCreate = orderItemRepository.findAll().collectList().block().size();
        // Create the OrderItem
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderItem))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeCreate + 1);
        OrderItem testOrderItem = orderItemList.get(orderItemList.size() - 1);
        assertThat(testOrderItem.getProductName()).isEqualTo(DEFAULT_PRODUCT_NAME);
        assertThat(testOrderItem.getPictureUrl()).isEqualTo(DEFAULT_PICTURE_URL);
        assertThat(testOrderItem.getUnitPrice()).isEqualByComparingTo(DEFAULT_UNIT_PRICE);
        assertThat(testOrderItem.getDiscount()).isEqualByComparingTo(DEFAULT_DISCOUNT);
        assertThat(testOrderItem.getUnits()).isEqualTo(DEFAULT_UNITS);
        assertThat(testOrderItem.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
    }

    @Test
    void createOrderItemWithExistingId() throws Exception {
        // Create the OrderItem with an existing ID
        orderItem.setId(1L);

        int databaseSizeBeforeCreate = orderItemRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkProductNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderItemRepository.findAll().collectList().block().size();
        // set the field null
        orderItem.setProductName(null);

        // Create the OrderItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPictureUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderItemRepository.findAll().collectList().block().size();
        // set the field null
        orderItem.setPictureUrl(null);

        // Create the OrderItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUnitPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderItemRepository.findAll().collectList().block().size();
        // set the field null
        orderItem.setUnitPrice(null);

        // Create the OrderItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDiscountIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderItemRepository.findAll().collectList().block().size();
        // set the field null
        orderItem.setDiscount(null);

        // Create the OrderItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUnitsIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderItemRepository.findAll().collectList().block().size();
        // set the field null
        orderItem.setUnits(null);

        // Create the OrderItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkProductIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderItemRepository.findAll().collectList().block().size();
        // set the field null
        orderItem.setProductId(null);

        // Create the OrderItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllOrderItems() {
        // Initialize the database
        orderItemRepository.save(orderItem).block();

        // Get all the orderItemList
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
            .value(hasItem(orderItem.getId().intValue()))
            .jsonPath("$.[*].productName")
            .value(hasItem(DEFAULT_PRODUCT_NAME))
            .jsonPath("$.[*].pictureUrl")
            .value(hasItem(DEFAULT_PICTURE_URL))
            .jsonPath("$.[*].unitPrice")
            .value(hasItem(sameNumber(DEFAULT_UNIT_PRICE)))
            .jsonPath("$.[*].discount")
            .value(hasItem(sameNumber(DEFAULT_DISCOUNT)))
            .jsonPath("$.[*].units")
            .value(hasItem(DEFAULT_UNITS))
            .jsonPath("$.[*].productId")
            .value(hasItem(DEFAULT_PRODUCT_ID.intValue()));
    }

    @Test
    void getOrderItem() {
        // Initialize the database
        orderItemRepository.save(orderItem).block();

        // Get the orderItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, orderItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(orderItem.getId().intValue()))
            .jsonPath("$.productName")
            .value(is(DEFAULT_PRODUCT_NAME))
            .jsonPath("$.pictureUrl")
            .value(is(DEFAULT_PICTURE_URL))
            .jsonPath("$.unitPrice")
            .value(is(sameNumber(DEFAULT_UNIT_PRICE)))
            .jsonPath("$.discount")
            .value(is(sameNumber(DEFAULT_DISCOUNT)))
            .jsonPath("$.units")
            .value(is(DEFAULT_UNITS))
            .jsonPath("$.productId")
            .value(is(DEFAULT_PRODUCT_ID.intValue()));
    }

    @Test
    void getNonExistingOrderItem() {
        // Get the orderItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewOrderItem() throws Exception {
        // Initialize the database
        orderItemRepository.save(orderItem).block();

        int databaseSizeBeforeUpdate = orderItemRepository.findAll().collectList().block().size();

        // Update the orderItem
        OrderItem updatedOrderItem = orderItemRepository.findById(orderItem.getId()).block();
        updatedOrderItem
            .productName(UPDATED_PRODUCT_NAME)
            .pictureUrl(UPDATED_PICTURE_URL)
            .unitPrice(UPDATED_UNIT_PRICE)
            .discount(UPDATED_DISCOUNT)
            .units(UPDATED_UNITS)
            .productId(UPDATED_PRODUCT_ID);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedOrderItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedOrderItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeUpdate);
        OrderItem testOrderItem = orderItemList.get(orderItemList.size() - 1);
        assertThat(testOrderItem.getProductName()).isEqualTo(UPDATED_PRODUCT_NAME);
        assertThat(testOrderItem.getPictureUrl()).isEqualTo(UPDATED_PICTURE_URL);
        assertThat(testOrderItem.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
        assertThat(testOrderItem.getDiscount()).isEqualTo(UPDATED_DISCOUNT);
        assertThat(testOrderItem.getUnits()).isEqualTo(UPDATED_UNITS);
        assertThat(testOrderItem.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
    }

    @Test
    void putNonExistingOrderItem() throws Exception {
        int databaseSizeBeforeUpdate = orderItemRepository.findAll().collectList().block().size();
        orderItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orderItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOrderItem() throws Exception {
        int databaseSizeBeforeUpdate = orderItemRepository.findAll().collectList().block().size();
        orderItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOrderItem() throws Exception {
        int databaseSizeBeforeUpdate = orderItemRepository.findAll().collectList().block().size();
        orderItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderItem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOrderItemWithPatch() throws Exception {
        // Initialize the database
        orderItemRepository.save(orderItem).block();

        int databaseSizeBeforeUpdate = orderItemRepository.findAll().collectList().block().size();

        // Update the orderItem using partial update
        OrderItem partialUpdatedOrderItem = new OrderItem();
        partialUpdatedOrderItem.setId(orderItem.getId());

        partialUpdatedOrderItem
            .productName(UPDATED_PRODUCT_NAME)
            .unitPrice(UPDATED_UNIT_PRICE)
            .discount(UPDATED_DISCOUNT)
            .units(UPDATED_UNITS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrderItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeUpdate);
        OrderItem testOrderItem = orderItemList.get(orderItemList.size() - 1);
        assertThat(testOrderItem.getProductName()).isEqualTo(UPDATED_PRODUCT_NAME);
        assertThat(testOrderItem.getPictureUrl()).isEqualTo(DEFAULT_PICTURE_URL);
        assertThat(testOrderItem.getUnitPrice()).isEqualByComparingTo(UPDATED_UNIT_PRICE);
        assertThat(testOrderItem.getDiscount()).isEqualByComparingTo(UPDATED_DISCOUNT);
        assertThat(testOrderItem.getUnits()).isEqualTo(UPDATED_UNITS);
        assertThat(testOrderItem.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
    }

    @Test
    void fullUpdateOrderItemWithPatch() throws Exception {
        // Initialize the database
        orderItemRepository.save(orderItem).block();

        int databaseSizeBeforeUpdate = orderItemRepository.findAll().collectList().block().size();

        // Update the orderItem using partial update
        OrderItem partialUpdatedOrderItem = new OrderItem();
        partialUpdatedOrderItem.setId(orderItem.getId());

        partialUpdatedOrderItem
            .productName(UPDATED_PRODUCT_NAME)
            .pictureUrl(UPDATED_PICTURE_URL)
            .unitPrice(UPDATED_UNIT_PRICE)
            .discount(UPDATED_DISCOUNT)
            .units(UPDATED_UNITS)
            .productId(UPDATED_PRODUCT_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrderItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeUpdate);
        OrderItem testOrderItem = orderItemList.get(orderItemList.size() - 1);
        assertThat(testOrderItem.getProductName()).isEqualTo(UPDATED_PRODUCT_NAME);
        assertThat(testOrderItem.getPictureUrl()).isEqualTo(UPDATED_PICTURE_URL);
        assertThat(testOrderItem.getUnitPrice()).isEqualByComparingTo(UPDATED_UNIT_PRICE);
        assertThat(testOrderItem.getDiscount()).isEqualByComparingTo(UPDATED_DISCOUNT);
        assertThat(testOrderItem.getUnits()).isEqualTo(UPDATED_UNITS);
        assertThat(testOrderItem.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
    }

    @Test
    void patchNonExistingOrderItem() throws Exception {
        int databaseSizeBeforeUpdate = orderItemRepository.findAll().collectList().block().size();
        orderItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, orderItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOrderItem() throws Exception {
        int databaseSizeBeforeUpdate = orderItemRepository.findAll().collectList().block().size();
        orderItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOrderItem() throws Exception {
        int databaseSizeBeforeUpdate = orderItemRepository.findAll().collectList().block().size();
        orderItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderItem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrderItem in the database
        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOrderItem() {
        // Initialize the database
        orderItemRepository.save(orderItem).block();

        int databaseSizeBeforeDelete = orderItemRepository.findAll().collectList().block().size();

        // Delete the orderItem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, orderItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<OrderItem> orderItemList = orderItemRepository.findAll().collectList().block();
        assertThat(orderItemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
