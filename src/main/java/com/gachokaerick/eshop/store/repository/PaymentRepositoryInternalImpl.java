package com.gachokaerick.eshop.store.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.gachokaerick.eshop.store.domain.Payment;
import com.gachokaerick.eshop.store.repository.rowmapper.OrderRowMapper;
import com.gachokaerick.eshop.store.repository.rowmapper.PaymentRowMapper;
import com.gachokaerick.eshop.store.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
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
 * Spring Data SQL reactive custom repository implementation for the Payment entity.
 */
@SuppressWarnings("unused")
class PaymentRepositoryInternalImpl implements PaymentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final OrderRowMapper orderMapper;
    private final PaymentRowMapper paymentMapper;

    private static final Table entityTable = Table.aliased("payment", EntityManager.ENTITY_ALIAS);
    private static final Table orderTable = Table.aliased("jhi_order", "e_order");

    public PaymentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OrderRowMapper orderMapper,
        PaymentRowMapper paymentMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.orderMapper = orderMapper;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public Flux<Payment> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Payment> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Payment> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = PaymentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OrderSqlHelper.getColumns(orderTable, "order"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(orderTable)
            .on(Column.create("order_id", entityTable))
            .equals(Column.create("id", orderTable));

        String select = entityManager.createSelect(selectFrom, Payment.class, pageable, criteria);
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
    public Flux<Payment> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Payment> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Payment process(Row row, RowMetadata metadata) {
        Payment entity = paymentMapper.apply(row, "e");
        entity.setOrder(orderMapper.apply(row, "order"));
        return entity;
    }

    @Override
    public <S extends Payment> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Payment> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update Payment with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(Payment entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}
