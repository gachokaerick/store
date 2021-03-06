package com.gachokaerick.eshop.store.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.gachokaerick.eshop.store.domain.Address;
import com.gachokaerick.eshop.store.repository.rowmapper.AddressRowMapper;
import com.gachokaerick.eshop.store.repository.rowmapper.BuyerRowMapper;
import com.gachokaerick.eshop.store.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Address entity.
 */
@SuppressWarnings("unused")
class AddressRepositoryInternalImpl implements AddressRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final BuyerRowMapper buyerMapper;
    private final AddressRowMapper addressMapper;

    private static final Table entityTable = Table.aliased("address", EntityManager.ENTITY_ALIAS);
    private static final Table buyerTable = Table.aliased("buyer", "buyer");

    public AddressRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        BuyerRowMapper buyerMapper,
        AddressRowMapper addressMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.buyerMapper = buyerMapper;
        this.addressMapper = addressMapper;
    }

    @Override
    public Flux<Address> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Address> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Address> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = AddressSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(BuyerSqlHelper.getColumns(buyerTable, "buyer"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(buyerTable)
            .on(Column.create("buyer_id", entityTable))
            .equals(Column.create("id", buyerTable));

        String select = entityManager.createSelect(selectFrom, Address.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(crit ->
                new StringBuilder(select)
                    .append(" ")
                    .append("WHERE")
                    .append(" ")
                    .append(alias)
                    .append(".")
                    .append(crit.toString())
                    .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Address> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Address> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Address process(Row row, RowMetadata metadata) {
        Address entity = addressMapper.apply(row, "e");
        entity.setBuyer(buyerMapper.apply(row, "buyer"));
        return entity;
    }

    @Override
    public <S extends Address> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Address> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update Address with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(Address entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}
