package com.gachokaerick.eshop.store.service;

import com.gachokaerick.eshop.store.domain.BasketCheckout;
import com.gachokaerick.eshop.store.repository.BasketCheckoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link BasketCheckout}.
 */
@Service
@Transactional
public class BasketCheckoutService {

    private final Logger log = LoggerFactory.getLogger(BasketCheckoutService.class);

    private final BasketCheckoutRepository basketCheckoutRepository;

    public BasketCheckoutService(BasketCheckoutRepository basketCheckoutRepository) {
        this.basketCheckoutRepository = basketCheckoutRepository;
    }

    /**
     * Save a basketCheckout.
     *
     * @param basketCheckout the entity to save.
     * @return the persisted entity.
     */
    public Mono<BasketCheckout> save(BasketCheckout basketCheckout) {
        log.debug("Request to save BasketCheckout : {}", basketCheckout);
        return basketCheckoutRepository.save(basketCheckout);
    }

    /**
     * Partially update a basketCheckout.
     *
     * @param basketCheckout the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<BasketCheckout> partialUpdate(BasketCheckout basketCheckout) {
        log.debug("Request to partially update BasketCheckout : {}", basketCheckout);

        return basketCheckoutRepository
            .findById(basketCheckout.getId())
            .map(existingBasketCheckout -> {
                if (basketCheckout.getStreet() != null) {
                    existingBasketCheckout.setStreet(basketCheckout.getStreet());
                }
                if (basketCheckout.getCity() != null) {
                    existingBasketCheckout.setCity(basketCheckout.getCity());
                }
                if (basketCheckout.getTown() != null) {
                    existingBasketCheckout.setTown(basketCheckout.getTown());
                }
                if (basketCheckout.getCountry() != null) {
                    existingBasketCheckout.setCountry(basketCheckout.getCountry());
                }
                if (basketCheckout.getZipcode() != null) {
                    existingBasketCheckout.setZipcode(basketCheckout.getZipcode());
                }
                if (basketCheckout.getCreateTime() != null) {
                    existingBasketCheckout.setCreateTime(basketCheckout.getCreateTime());
                }
                if (basketCheckout.getUpdateTime() != null) {
                    existingBasketCheckout.setUpdateTime(basketCheckout.getUpdateTime());
                }
                if (basketCheckout.getPaymentStatus() != null) {
                    existingBasketCheckout.setPaymentStatus(basketCheckout.getPaymentStatus());
                }
                if (basketCheckout.getPayerCountryCode() != null) {
                    existingBasketCheckout.setPayerCountryCode(basketCheckout.getPayerCountryCode());
                }
                if (basketCheckout.getPayerEmail() != null) {
                    existingBasketCheckout.setPayerEmail(basketCheckout.getPayerEmail());
                }
                if (basketCheckout.getPayerName() != null) {
                    existingBasketCheckout.setPayerName(basketCheckout.getPayerName());
                }
                if (basketCheckout.getPayerSurname() != null) {
                    existingBasketCheckout.setPayerSurname(basketCheckout.getPayerSurname());
                }
                if (basketCheckout.getPayerId() != null) {
                    existingBasketCheckout.setPayerId(basketCheckout.getPayerId());
                }
                if (basketCheckout.getCurrency() != null) {
                    existingBasketCheckout.setCurrency(basketCheckout.getCurrency());
                }
                if (basketCheckout.getAmount() != null) {
                    existingBasketCheckout.setAmount(basketCheckout.getAmount());
                }
                if (basketCheckout.getPaymentId() != null) {
                    existingBasketCheckout.setPaymentId(basketCheckout.getPaymentId());
                }
                if (basketCheckout.getUserLogin() != null) {
                    existingBasketCheckout.setUserLogin(basketCheckout.getUserLogin());
                }
                if (basketCheckout.getDescription() != null) {
                    existingBasketCheckout.setDescription(basketCheckout.getDescription());
                }

                return existingBasketCheckout;
            })
            .flatMap(basketCheckoutRepository::save);
    }

    /**
     * Get all the basketCheckouts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BasketCheckout> findAll(Pageable pageable) {
        log.debug("Request to get all BasketCheckouts");
        return basketCheckoutRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of basketCheckouts available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return basketCheckoutRepository.count();
    }

    /**
     * Get one basketCheckout by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<BasketCheckout> findOne(Long id) {
        log.debug("Request to get BasketCheckout : {}", id);
        return basketCheckoutRepository.findById(id);
    }

    /**
     * Delete the basketCheckout by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete BasketCheckout : {}", id);
        return basketCheckoutRepository.deleteById(id);
    }
}
