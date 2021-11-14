package com.gachokaerick.eshop.store.repository;

import com.gachokaerick.eshop.store.domain.Address;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Address entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AddressRepository extends R2dbcRepository<Address, Long>, AddressRepositoryInternal {
    Flux<Address> findAllBy(Pageable pageable);

    @Query("SELECT * FROM address entity WHERE entity.buyer_id = :id")
    Flux<Address> findByBuyer(Long id);

    @Query("SELECT * FROM address entity WHERE entity.buyer_id IS NULL")
    Flux<Address> findAllWhereBuyerIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Address> findAll();

    @Override
    Mono<Address> findById(Long id);

    @Override
    <S extends Address> Mono<S> save(S entity);
}

interface AddressRepositoryInternal {
    <S extends Address> Mono<S> insert(S entity);
    <S extends Address> Mono<S> save(S entity);
    Mono<Integer> update(Address entity);

    Flux<Address> findAll();
    Mono<Address> findById(Long id);
    Flux<Address> findAllBy(Pageable pageable);
    Flux<Address> findAllBy(Pageable pageable, Criteria criteria);
}
