package com.gachokaerick.eshop.store.repository;

import com.gachokaerick.eshop.store.domain.CatalogBrand;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the CatalogBrand entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CatalogBrandRepository extends R2dbcRepository<CatalogBrand, Long>, CatalogBrandRepositoryInternal {
    Flux<CatalogBrand> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<CatalogBrand> findAll();

    @Override
    Mono<CatalogBrand> findById(Long id);

    @Override
    <S extends CatalogBrand> Mono<S> save(S entity);
}

interface CatalogBrandRepositoryInternal {
    <S extends CatalogBrand> Mono<S> insert(S entity);
    <S extends CatalogBrand> Mono<S> save(S entity);
    Mono<Integer> update(CatalogBrand entity);

    Flux<CatalogBrand> findAll();
    Mono<CatalogBrand> findById(Long id);
    Flux<CatalogBrand> findAllBy(Pageable pageable);
    Flux<CatalogBrand> findAllBy(Pageable pageable, Criteria criteria);
}
