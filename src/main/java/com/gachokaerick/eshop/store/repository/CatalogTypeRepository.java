package com.gachokaerick.eshop.store.repository;

import com.gachokaerick.eshop.store.domain.CatalogType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the CatalogType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CatalogTypeRepository extends R2dbcRepository<CatalogType, Long>, CatalogTypeRepositoryInternal {
    Flux<CatalogType> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<CatalogType> findAll();

    @Override
    Mono<CatalogType> findById(Long id);

    @Override
    <S extends CatalogType> Mono<S> save(S entity);
}

interface CatalogTypeRepositoryInternal {
    <S extends CatalogType> Mono<S> insert(S entity);
    <S extends CatalogType> Mono<S> save(S entity);
    Mono<Integer> update(CatalogType entity);

    Flux<CatalogType> findAll();
    Mono<CatalogType> findById(Long id);
    Flux<CatalogType> findAllBy(Pageable pageable);
    Flux<CatalogType> findAllBy(Pageable pageable, Criteria criteria);
}
