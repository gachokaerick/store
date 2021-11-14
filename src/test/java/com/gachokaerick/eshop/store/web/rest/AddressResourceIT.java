package com.gachokaerick.eshop.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.gachokaerick.eshop.store.IntegrationTest;
import com.gachokaerick.eshop.store.domain.Address;
import com.gachokaerick.eshop.store.domain.Buyer;
import com.gachokaerick.eshop.store.repository.AddressRepository;
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
 * Integration tests for the {@link AddressResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class AddressResourceIT {

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

    private static final String ENTITY_API_URL = "/api/addresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Address address;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Address createEntity(EntityManager em) {
        Address address = new Address()
            .street(DEFAULT_STREET)
            .city(DEFAULT_CITY)
            .town(DEFAULT_TOWN)
            .country(DEFAULT_COUNTRY)
            .zipcode(DEFAULT_ZIPCODE);
        // Add required entity
        Buyer buyer;
        buyer = em.insert(BuyerResourceIT.createEntity(em)).block();
        address.setBuyer(buyer);
        return address;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Address createUpdatedEntity(EntityManager em) {
        Address address = new Address()
            .street(UPDATED_STREET)
            .city(UPDATED_CITY)
            .town(UPDATED_TOWN)
            .country(UPDATED_COUNTRY)
            .zipcode(UPDATED_ZIPCODE);
        // Add required entity
        Buyer buyer;
        buyer = em.insert(BuyerResourceIT.createUpdatedEntity(em)).block();
        address.setBuyer(buyer);
        return address;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Address.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        BuyerResourceIT.deleteEntities(em);
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
        address = createEntity(em);
    }

    @Test
    void createAddress() throws Exception {
        int databaseSizeBeforeCreate = addressRepository.findAll().collectList().block().size();
        // Create the Address
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(address))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeCreate + 1);
        Address testAddress = addressList.get(addressList.size() - 1);
        assertThat(testAddress.getStreet()).isEqualTo(DEFAULT_STREET);
        assertThat(testAddress.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testAddress.getTown()).isEqualTo(DEFAULT_TOWN);
        assertThat(testAddress.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testAddress.getZipcode()).isEqualTo(DEFAULT_ZIPCODE);
    }

    @Test
    void createAddressWithExistingId() throws Exception {
        // Create the Address with an existing ID
        address.setId(1L);

        int databaseSizeBeforeCreate = addressRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(address))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = addressRepository.findAll().collectList().block().size();
        // set the field null
        address.setCity(null);

        // Create the Address, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(address))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTownIsRequired() throws Exception {
        int databaseSizeBeforeTest = addressRepository.findAll().collectList().block().size();
        // set the field null
        address.setTown(null);

        // Create the Address, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(address))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCountryIsRequired() throws Exception {
        int databaseSizeBeforeTest = addressRepository.findAll().collectList().block().size();
        // set the field null
        address.setCountry(null);

        // Create the Address, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(address))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllAddresses() {
        // Initialize the database
        addressRepository.save(address).block();

        // Get all the addressList
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
            .value(hasItem(address.getId().intValue()))
            .jsonPath("$.[*].street")
            .value(hasItem(DEFAULT_STREET))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].town")
            .value(hasItem(DEFAULT_TOWN))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY))
            .jsonPath("$.[*].zipcode")
            .value(hasItem(DEFAULT_ZIPCODE));
    }

    @Test
    void getAddress() {
        // Initialize the database
        addressRepository.save(address).block();

        // Get the address
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, address.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(address.getId().intValue()))
            .jsonPath("$.street")
            .value(is(DEFAULT_STREET))
            .jsonPath("$.city")
            .value(is(DEFAULT_CITY))
            .jsonPath("$.town")
            .value(is(DEFAULT_TOWN))
            .jsonPath("$.country")
            .value(is(DEFAULT_COUNTRY))
            .jsonPath("$.zipcode")
            .value(is(DEFAULT_ZIPCODE));
    }

    @Test
    void getNonExistingAddress() {
        // Get the address
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewAddress() throws Exception {
        // Initialize the database
        addressRepository.save(address).block();

        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();

        // Update the address
        Address updatedAddress = addressRepository.findById(address.getId()).block();
        updatedAddress.street(UPDATED_STREET).city(UPDATED_CITY).town(UPDATED_TOWN).country(UPDATED_COUNTRY).zipcode(UPDATED_ZIPCODE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAddress.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedAddress))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
        Address testAddress = addressList.get(addressList.size() - 1);
        assertThat(testAddress.getStreet()).isEqualTo(UPDATED_STREET);
        assertThat(testAddress.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testAddress.getTown()).isEqualTo(UPDATED_TOWN);
        assertThat(testAddress.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testAddress.getZipcode()).isEqualTo(UPDATED_ZIPCODE);
    }

    @Test
    void putNonExistingAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();
        address.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, address.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(address))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();
        address.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(address))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();
        address.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(address))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAddressWithPatch() throws Exception {
        // Initialize the database
        addressRepository.save(address).block();

        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();

        // Update the address using partial update
        Address partialUpdatedAddress = new Address();
        partialUpdatedAddress.setId(address.getId());

        partialUpdatedAddress.city(UPDATED_CITY).town(UPDATED_TOWN).country(UPDATED_COUNTRY).zipcode(UPDATED_ZIPCODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAddress.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAddress))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
        Address testAddress = addressList.get(addressList.size() - 1);
        assertThat(testAddress.getStreet()).isEqualTo(DEFAULT_STREET);
        assertThat(testAddress.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testAddress.getTown()).isEqualTo(UPDATED_TOWN);
        assertThat(testAddress.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testAddress.getZipcode()).isEqualTo(UPDATED_ZIPCODE);
    }

    @Test
    void fullUpdateAddressWithPatch() throws Exception {
        // Initialize the database
        addressRepository.save(address).block();

        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();

        // Update the address using partial update
        Address partialUpdatedAddress = new Address();
        partialUpdatedAddress.setId(address.getId());

        partialUpdatedAddress
            .street(UPDATED_STREET)
            .city(UPDATED_CITY)
            .town(UPDATED_TOWN)
            .country(UPDATED_COUNTRY)
            .zipcode(UPDATED_ZIPCODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAddress.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAddress))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
        Address testAddress = addressList.get(addressList.size() - 1);
        assertThat(testAddress.getStreet()).isEqualTo(UPDATED_STREET);
        assertThat(testAddress.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testAddress.getTown()).isEqualTo(UPDATED_TOWN);
        assertThat(testAddress.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testAddress.getZipcode()).isEqualTo(UPDATED_ZIPCODE);
    }

    @Test
    void patchNonExistingAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();
        address.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, address.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(address))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();
        address.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(address))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();
        address.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(address))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAddress() {
        // Initialize the database
        addressRepository.save(address).block();

        int databaseSizeBeforeDelete = addressRepository.findAll().collectList().block().size();

        // Delete the address
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, address.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
