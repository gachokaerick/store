package com.gachokaerick.eshop.store.repository;

import com.gachokaerick.eshop.store.domain.BasketItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the BasketItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BasketItemRepository extends R2dbcRepository<BasketItem, Long>, BasketItemRepositoryInternal {
    Flux<BasketItem> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<BasketItem> findAll();

    @Override
    Mono<BasketItem> findById(Long id);

    @Override
    <S extends BasketItem> Mono<S> save(S entity);
}

interface BasketItemRepositoryInternal {
    <S extends BasketItem> Mono<S> insert(S entity);
    <S extends BasketItem> Mono<S> save(S entity);
    Mono<Integer> update(BasketItem entity);

    Flux<BasketItem> findAll();
    Mono<BasketItem> findById(Long id);
    Flux<BasketItem> findAllBy(Pageable pageable);
    Flux<BasketItem> findAllBy(Pageable pageable, Criteria criteria);
}
