package com.gachokaerick.eshop.store.repository;

import com.gachokaerick.eshop.store.domain.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends R2dbcRepository<Notification, Long>, NotificationRepositoryInternal {
    Flux<Notification> findAllBy(Pageable pageable);

    @Query("SELECT * FROM notification entity WHERE entity.user_id = :id")
    Flux<Notification> findByUser(Long id);

    @Query("SELECT * FROM notification entity WHERE entity.user_id IS NULL")
    Flux<Notification> findAllWhereUserIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Notification> findAll();

    @Override
    Mono<Notification> findById(Long id);

    @Override
    <S extends Notification> Mono<S> save(S entity);
}

interface NotificationRepositoryInternal {
    <S extends Notification> Mono<S> insert(S entity);
    <S extends Notification> Mono<S> save(S entity);
    Mono<Integer> update(Notification entity);

    Flux<Notification> findAll();
    Mono<Notification> findById(Long id);
    Flux<Notification> findAllBy(Pageable pageable);
    Flux<Notification> findAllBy(Pageable pageable, Criteria criteria);
}
