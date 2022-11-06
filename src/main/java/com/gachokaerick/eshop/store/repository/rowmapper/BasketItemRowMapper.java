package com.gachokaerick.eshop.store.repository.rowmapper;

import com.gachokaerick.eshop.store.domain.BasketItem;
import com.gachokaerick.eshop.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link BasketItem}, with proper type conversions.
 */
@Service
public class BasketItemRowMapper implements BiFunction<Row, String, BasketItem> {

    private final ColumnConverter converter;

    public BasketItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link BasketItem} stored in the database.
     */
    @Override
    public BasketItem apply(Row row, String prefix) {
        BasketItem entity = new BasketItem();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        entity.setProductName(converter.fromRow(row, prefix + "_product_name", String.class));
        entity.setUnitPrice(converter.fromRow(row, prefix + "_unit_price", BigDecimal.class));
        entity.setOldUnitPrice(converter.fromRow(row, prefix + "_old_unit_price", BigDecimal.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setPictureUrl(converter.fromRow(row, prefix + "_picture_url", String.class));
        entity.setUserLogin(converter.fromRow(row, prefix + "_user_login", String.class));
        return entity;
    }
}
