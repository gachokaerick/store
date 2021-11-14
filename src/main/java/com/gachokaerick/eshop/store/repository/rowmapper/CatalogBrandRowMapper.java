package com.gachokaerick.eshop.store.repository.rowmapper;

import com.gachokaerick.eshop.store.domain.CatalogBrand;
import com.gachokaerick.eshop.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CatalogBrand}, with proper type conversions.
 */
@Service
public class CatalogBrandRowMapper implements BiFunction<Row, String, CatalogBrand> {

    private final ColumnConverter converter;

    public CatalogBrandRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CatalogBrand} stored in the database.
     */
    @Override
    public CatalogBrand apply(Row row, String prefix) {
        CatalogBrand entity = new CatalogBrand();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setBrand(converter.fromRow(row, prefix + "_brand", String.class));
        return entity;
    }
}
