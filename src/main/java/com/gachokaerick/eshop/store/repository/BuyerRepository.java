package com.gachokaerick.eshop.store.repository;

import com.gachokaerick.eshop.store.domain.Buyer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Buyer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BuyerRepository extends R2dbcRepository<Buyer, Long>, BuyerRepositoryInternal {
    Flux<Buyer> findAllBy(Pageable pageable);

    @Query("SELECT * FROM buyer entity WHERE entity.user_id = :id")
    Flux<Buyer> findByUser(Long id);

    @Query("SELECT * FROM buyer entity WHERE entity.user_id IS NULL")
    Flux<Buyer> findAllWhereUserIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Buyer> findAll();

    @Override
    Mono<Buyer> findById(Long id);

    @Override
    <S extends Buyer> Mono<S> save(S entity);
}

interface BuyerRepositoryInternal {
    <S extends Buyer> Mono<S> insert(S entity);
    <S extends Buyer> Mono<S> save(S entity);
    Mono<Integer> update(Buyer entity);

    Flux<Buyer> findAll();
    Mono<Buyer> findById(Long id);
    Flux<Buyer> findAllBy(Pageable pageable);
    Flux<Buyer> findAllBy(Pageable pageable, Criteria criteria);
}
