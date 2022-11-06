package com.gachokaerick.eshop.store.repository;

import com.gachokaerick.eshop.store.domain.BasketCheckout;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the BasketCheckout entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BasketCheckoutRepository extends R2dbcRepository<BasketCheckout, Long>, BasketCheckoutRepositoryInternal {
    Flux<BasketCheckout> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<BasketCheckout> findAll();

    @Override
    Mono<BasketCheckout> findById(Long id);

    @Override
    <S extends BasketCheckout> Mono<S> save(S entity);
}

interface BasketCheckoutRepositoryInternal {
    <S extends BasketCheckout> Mono<S> insert(S entity);
    <S extends BasketCheckout> Mono<S> save(S entity);
    Mono<Integer> update(BasketCheckout entity);

    Flux<BasketCheckout> findAll();
    Mono<BasketCheckout> findById(Long id);
    Flux<BasketCheckout> findAllBy(Pageable pageable);
    Flux<BasketCheckout> findAllBy(Pageable pageable, Criteria criteria);
}
