package com.gachokaerick.eshop.store.repository.rowmapper;

import com.gachokaerick.eshop.store.domain.CatalogType;
import com.gachokaerick.eshop.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CatalogType}, with proper type conversions.
 */
@Service
public class CatalogTypeRowMapper implements BiFunction<Row, String, CatalogType> {

    private final ColumnConverter converter;

    public CatalogTypeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CatalogType} stored in the database.
     */
    @Override
    public CatalogType apply(Row row, String prefix) {
        CatalogType entity = new CatalogType();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setType(converter.fromRow(row, prefix + "_type", String.class));
        return entity;
    }
}
