package com.gachokaerick.eshop.store.repository;

import com.gachokaerick.eshop.store.domain.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Payment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentRepository extends R2dbcRepository<Payment, Long>, PaymentRepositoryInternal {
    Flux<Payment> findAllBy(Pageable pageable);

    @Query("SELECT * FROM payment entity WHERE entity.order_id = :id")
    Flux<Payment> findByOrder(Long id);

    @Query("SELECT * FROM payment entity WHERE entity.order_id IS NULL")
    Flux<Payment> findAllWhereOrderIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Payment> findAll();

    @Override
    Mono<Payment> findById(Long id);

    @Override
    <S extends Payment> Mono<S> save(S entity);
}

interface PaymentRepositoryInternal {
    <S extends Payment> Mono<S> insert(S entity);
    <S extends Payment> Mono<S> save(S entity);
    Mono<Integer> update(Payment entity);

    Flux<Payment> findAll();
    Mono<Payment> findById(Long id);
    Flux<Payment> findAllBy(Pageable pageable);
    Flux<Payment> findAllBy(Pageable pageable, Criteria criteria);
}
