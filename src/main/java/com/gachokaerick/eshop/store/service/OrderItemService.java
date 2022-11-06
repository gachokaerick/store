package com.gachokaerick.eshop.store.service;

import com.gachokaerick.eshop.store.domain.OrderItem;
import com.gachokaerick.eshop.store.repository.OrderItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link OrderItem}.
 */
@Service
@Transactional
public class OrderItemService {

    private final Logger log = LoggerFactory.getLogger(OrderItemService.class);

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Save a orderItem.
     *
     * @param orderItem the entity to save.
     * @return the persisted entity.
     */
    public Mono<OrderItem> save(OrderItem orderItem) {
        log.debug("Request to save OrderItem : {}", orderItem);
        return orderItemRepository.save(orderItem);
    }

    /**
     * Partially update a orderItem.
     *
     * @param orderItem the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OrderItem> partialUpdate(OrderItem orderItem) {
        log.debug("Request to partially update OrderItem : {}", orderItem);

        return orderItemRepository
            .findById(orderItem.getId())
            .map(existingOrderItem -> {
                if (orderItem.getProductName() != null) {
                    existingOrderItem.setProductName(orderItem.getProductName());
                }
                if (orderItem.getPictureUrl() != null) {
                    existingOrderItem.setPictureUrl(orderItem.getPictureUrl());
                }
                if (orderItem.getUnitPrice() != null) {
                    existingOrderItem.setUnitPrice(orderItem.getUnitPrice());
                }
                if (orderItem.getDiscount() != null) {
                    existingOrderItem.setDiscount(orderItem.getDiscount());
                }
                if (orderItem.getUnits() != null) {
                    existingOrderItem.setUnits(orderItem.getUnits());
                }
                if (orderItem.getProductId() != null) {
                    existingOrderItem.setProductId(orderItem.getProductId());
                }

                return existingOrderItem;
            })
            .flatMap(orderItemRepository::save);
    }

    /**
     * Get all the orderItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OrderItem> findAll(Pageable pageable) {
        log.debug("Request to get all OrderItems");
        return orderItemRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of orderItems available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return orderItemRepository.count();
    }

    /**
     * Get one orderItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OrderItem> findOne(Long id) {
        log.debug("Request to get OrderItem : {}", id);
        return orderItemRepository.findById(id);
    }

    /**
     * Delete the orderItem by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete OrderItem : {}", id);
        return orderItemRepository.deleteById(id);
    }
}
