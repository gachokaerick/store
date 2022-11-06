package com.gachokaerick.eshop.store.repository.rowmapper;

import com.gachokaerick.eshop.store.domain.CatalogItem;
import com.gachokaerick.eshop.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CatalogItem}, with proper type conversions.
 */
@Service
public class CatalogItemRowMapper implements BiFunction<Row, String, CatalogItem> {

    private final ColumnConverter converter;

    public CatalogItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CatalogItem} stored in the database.
     */
    @Override
    public CatalogItem apply(Row row, String prefix) {
        CatalogItem entity = new CatalogItem();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", BigDecimal.class));
        entity.setPictureFileName(converter.fromRow(row, prefix + "_picture_file_name", String.class));
        entity.setPictureUrl(converter.fromRow(row, prefix + "_picture_url", String.class));
        entity.setAvailableStock(converter.fromRow(row, prefix + "_available_stock", Integer.class));
        entity.setRestockThreshold(converter.fromRow(row, prefix + "_restock_threshold", Integer.class));
        entity.setMaxStockThreshold(converter.fromRow(row, prefix + "_max_stock_threshold", Integer.class));
        entity.setOnReorder(converter.fromRow(row, prefix + "_on_reorder", Boolean.class));
        entity.setCatalogBrandId(converter.fromRow(row, prefix + "_catalog_brand_id", Long.class));
        entity.setCatalogTypeId(converter.fromRow(row, prefix + "_catalog_type_id", Long.class));
        return entity;
    }
}
