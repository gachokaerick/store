package com.gachokaerick.eshop.store.repository;

import com.gachokaerick.eshop.store.domain.CatalogItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the CatalogItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CatalogItemRepository extends R2dbcRepository<CatalogItem, Long>, CatalogItemRepositoryInternal {
    Flux<CatalogItem> findAllBy(Pageable pageable);

    @Query("SELECT * FROM catalog_item entity WHERE entity.catalog_brand_id = :id")
    Flux<CatalogItem> findByCatalogBrand(Long id);

    @Query("SELECT * FROM catalog_item entity WHERE entity.catalog_brand_id IS NULL")
    Flux<CatalogItem> findAllWhereCatalogBrandIsNull();

    @Query("SELECT * FROM catalog_item entity WHERE entity.catalog_type_id = :id")
    Flux<CatalogItem> findByCatalogType(Long id);

    @Query("SELECT * FROM catalog_item entity WHERE entity.catalog_type_id IS NULL")
    Flux<CatalogItem> findAllWhereCatalogTypeIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<CatalogItem> findAll();

    @Override
    Mono<CatalogItem> findById(Long id);

    @Override
    <S extends CatalogItem> Mono<S> save(S entity);
}

interface CatalogItemRepositoryInternal {
    <S extends CatalogItem> Mono<S> insert(S entity);
    <S extends CatalogItem> Mono<S> save(S entity);
    Mono<Integer> update(CatalogItem entity);

    Flux<CatalogItem> findAll();
    Mono<CatalogItem> findById(Long id);
    Flux<CatalogItem> findAllBy(Pageable pageable);
    Flux<CatalogItem> findAllBy(Pageable pageable, Criteria criteria);
}
