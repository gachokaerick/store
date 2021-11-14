package com.gachokaerick.eshop.store.service;

import com.gachokaerick.eshop.store.domain.CatalogItem;
import com.gachokaerick.eshop.store.repository.CatalogItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link CatalogItem}.
 */
@Service
@Transactional
public class CatalogItemService {

    private final Logger log = LoggerFactory.getLogger(CatalogItemService.class);

    private final CatalogItemRepository catalogItemRepository;

    public CatalogItemService(CatalogItemRepository catalogItemRepository) {
        this.catalogItemRepository = catalogItemRepository;
    }

    /**
     * Save a catalogItem.
     *
     * @param catalogItem the entity to save.
     * @return the persisted entity.
     */
    public Mono<CatalogItem> save(CatalogItem catalogItem) {
        log.debug("Request to save CatalogItem : {}", catalogItem);
        return catalogItemRepository.save(catalogItem);
    }

    /**
     * Partially update a catalogItem.
     *
     * @param catalogItem the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CatalogItem> partialUpdate(CatalogItem catalogItem) {
        log.debug("Request to partially update CatalogItem : {}", catalogItem);

        return catalogItemRepository
            .findById(catalogItem.getId())
            .map(existingCatalogItem -> {
                if (catalogItem.getName() != null) {
                    existingCatalogItem.setName(catalogItem.getName());
                }
                if (catalogItem.getDescription() != null) {
                    existingCatalogItem.setDescription(catalogItem.getDescription());
                }
                if (catalogItem.getPrice() != null) {
                    existingCatalogItem.setPrice(catalogItem.getPrice());
                }
                if (catalogItem.getPictureFileName() != null) {
                    existingCatalogItem.setPictureFileName(catalogItem.getPictureFileName());
                }
                if (catalogItem.getPictureUrl() != null) {
                    existingCatalogItem.setPictureUrl(catalogItem.getPictureUrl());
                }
                if (catalogItem.getAvailableStock() != null) {
                    existingCatalogItem.setAvailableStock(catalogItem.getAvailableStock());
                }
                if (catalogItem.getRestockThreshold() != null) {
                    existingCatalogItem.setRestockThreshold(catalogItem.getRestockThreshold());
                }
                if (catalogItem.getMaxStockThreshold() != null) {
                    existingCatalogItem.setMaxStockThreshold(catalogItem.getMaxStockThreshold());
                }
                if (catalogItem.getOnReorder() != null) {
                    existingCatalogItem.setOnReorder(catalogItem.getOnReorder());
                }

                return existingCatalogItem;
            })
            .flatMap(catalogItemRepository::save);
    }

    /**
     * Get all the catalogItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CatalogItem> findAll(Pageable pageable) {
        log.debug("Request to get all CatalogItems");
        return catalogItemRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of catalogItems available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return catalogItemRepository.count();
    }

    /**
     * Get one catalogItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CatalogItem> findOne(Long id) {
        log.debug("Request to get CatalogItem : {}", id);
        return catalogItemRepository.findById(id);
    }

    /**
     * Delete the catalogItem by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete CatalogItem : {}", id);
        return catalogItemRepository.deleteById(id);
    }
}
