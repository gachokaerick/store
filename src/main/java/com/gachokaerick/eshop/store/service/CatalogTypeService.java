package com.gachokaerick.eshop.store.service;

import com.gachokaerick.eshop.store.domain.CatalogType;
import com.gachokaerick.eshop.store.repository.CatalogTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link CatalogType}.
 */
@Service
@Transactional
public class CatalogTypeService {

    private final Logger log = LoggerFactory.getLogger(CatalogTypeService.class);

    private final CatalogTypeRepository catalogTypeRepository;

    public CatalogTypeService(CatalogTypeRepository catalogTypeRepository) {
        this.catalogTypeRepository = catalogTypeRepository;
    }

    /**
     * Save a catalogType.
     *
     * @param catalogType the entity to save.
     * @return the persisted entity.
     */
    public Mono<CatalogType> save(CatalogType catalogType) {
        log.debug("Request to save CatalogType : {}", catalogType);
        return catalogTypeRepository.save(catalogType);
    }

    /**
     * Partially update a catalogType.
     *
     * @param catalogType the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CatalogType> partialUpdate(CatalogType catalogType) {
        log.debug("Request to partially update CatalogType : {}", catalogType);

        return catalogTypeRepository
            .findById(catalogType.getId())
            .map(existingCatalogType -> {
                if (catalogType.getType() != null) {
                    existingCatalogType.setType(catalogType.getType());
                }

                return existingCatalogType;
            })
            .flatMap(catalogTypeRepository::save);
    }

    /**
     * Get all the catalogTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CatalogType> findAll(Pageable pageable) {
        log.debug("Request to get all CatalogTypes");
        return catalogTypeRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of catalogTypes available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return catalogTypeRepository.count();
    }

    /**
     * Get one catalogType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CatalogType> findOne(Long id) {
        log.debug("Request to get CatalogType : {}", id);
        return catalogTypeRepository.findById(id);
    }

    /**
     * Delete the catalogType by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete CatalogType : {}", id);
        return catalogTypeRepository.deleteById(id);
    }
}
