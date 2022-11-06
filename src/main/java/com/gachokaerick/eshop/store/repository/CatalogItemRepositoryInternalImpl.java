package com.gachokaerick.eshop.store.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.gachokaerick.eshop.store.domain.CatalogItem;
import com.gachokaerick.eshop.store.repository.rowmapper.CatalogBrandRowMapper;
import com.gachokaerick.eshop.store.repository.rowmapper.CatalogItemRowMapper;
import com.gachokaerick.eshop.store.repository.rowmapper.CatalogTypeRowMapper;
import com.gachokaerick.eshop.store.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.math.BigDecimal;
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
 * Spring Data SQL reactive custom repository implementation for the CatalogItem entity.
 */
@SuppressWarnings("unused")
class CatalogItemRepositoryInternalImpl implements CatalogItemRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CatalogBrandRowMapper catalogbrandMapper;
    private final CatalogTypeRowMapper catalogtypeMapper;
    private final CatalogItemRowMapper catalogitemMapper;

    private static final Table entityTable = Table.aliased("catalog_item", EntityManager.ENTITY_ALIAS);
    private static final Table catalogBrandTable = Table.aliased("catalog_brand", "catalogBrand");
    private static final Table catalogTypeTable = Table.aliased("catalog_type", "catalogType");

    public CatalogItemRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CatalogBrandRowMapper catalogbrandMapper,
        CatalogTypeRowMapper catalogtypeMapper,
        CatalogItemRowMapper catalogitemMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.catalogbrandMapper = catalogbrandMapper;
        this.catalogtypeMapper = catalogtypeMapper;
        this.catalogitemMapper = catalogitemMapper;
    }

    @Override
    public Flux<CatalogItem> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<CatalogItem> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<CatalogItem> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CatalogItemSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CatalogBrandSqlHelper.getColumns(catalogBrandTable, "catalogBrand"));
        columns.addAll(CatalogTypeSqlHelper.getColumns(catalogTypeTable, "catalogType"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(catalogBrandTable)
            .on(Column.create("catalog_brand_id", entityTable))
            .equals(Column.create("id", catalogBrandTable))
            .leftOuterJoin(catalogTypeTable)
            .on(Column.create("catalog_type_id", entityTable))
            .equals(Column.create("id", catalogTypeTable));

        String select = entityManager.createSelect(selectFrom, CatalogItem.class, pageable, criteria);
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
    public Flux<CatalogItem> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<CatalogItem> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private CatalogItem process(Row row, RowMetadata metadata) {
        CatalogItem entity = catalogitemMapper.apply(row, "e");
        entity.setCatalogBrand(catalogbrandMapper.apply(row, "catalogBrand"));
        entity.setCatalogType(catalogtypeMapper.apply(row, "catalogType"));
        return entity;
    }

    @Override
    public <S extends CatalogItem> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends CatalogItem> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update CatalogItem with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(CatalogItem entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}
