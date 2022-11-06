package com.gachokaerick.eshop.store.service;

import com.gachokaerick.eshop.store.domain.Buyer;
import com.gachokaerick.eshop.store.repository.BuyerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Buyer}.
 */
@Service
@Transactional
public class BuyerService {

    private final Logger log = LoggerFactory.getLogger(BuyerService.class);

    private final BuyerRepository buyerRepository;

    public BuyerService(BuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    /**
     * Save a buyer.
     *
     * @param buyer the entity to save.
     * @return the persisted entity.
     */
    public Mono<Buyer> save(Buyer buyer) {
        log.debug("Request to save Buyer : {}", buyer);
        return buyerRepository.save(buyer);
    }

    /**
     * Partially update a buyer.
     *
     * @param buyer the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Buyer> partialUpdate(Buyer buyer) {
        log.debug("Request to partially update Buyer : {}", buyer);

        return buyerRepository
            .findById(buyer.getId())
            .map(existingBuyer -> {
                if (buyer.getFirstName() != null) {
                    existingBuyer.setFirstName(buyer.getFirstName());
                }
                if (buyer.getLastName() != null) {
                    existingBuyer.setLastName(buyer.getLastName());
                }
                if (buyer.getGender() != null) {
                    existingBuyer.setGender(buyer.getGender());
                }
                if (buyer.getEmail() != null) {
                    existingBuyer.setEmail(buyer.getEmail());
                }
                if (buyer.getPhone() != null) {
                    existingBuyer.setPhone(buyer.getPhone());
                }

                return existingBuyer;
            })
            .flatMap(buyerRepository::save);
    }

    /**
     * Get all the buyers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Buyer> findAll(Pageable pageable) {
        log.debug("Request to get all Buyers");
        return buyerRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of buyers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return buyerRepository.count();
    }

    /**
     * Get one buyer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Buyer> findOne(Long id) {
        log.debug("Request to get Buyer : {}", id);
        return buyerRepository.findById(id);
    }

    /**
     * Delete the buyer by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Buyer : {}", id);
        return buyerRepository.deleteById(id);
    }
}
