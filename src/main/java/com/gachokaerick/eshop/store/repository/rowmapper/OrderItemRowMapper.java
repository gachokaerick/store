package com.gachokaerick.eshop.store.repository.rowmapper;

import com.gachokaerick.eshop.store.domain.OrderItem;
import com.gachokaerick.eshop.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OrderItem}, with proper type conversions.
 */
@Service
public class OrderItemRowMapper implements BiFunction<Row, String, OrderItem> {

    private final ColumnConverter converter;

    public OrderItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OrderItem} stored in the database.
     */
    @Override
    public OrderItem apply(Row row, String prefix) {
        OrderItem entity = new OrderItem();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setProductName(converter.fromRow(row, prefix + "_product_name", String.class));
        entity.setPictureUrl(converter.fromRow(row, prefix + "_picture_url", String.class));
        entity.setUnitPrice(converter.fromRow(row, prefix + "_unit_price", BigDecimal.class));
        entity.setDiscount(converter.fromRow(row, prefix + "_discount", BigDecimal.class));
        entity.setUnits(converter.fromRow(row, prefix + "_units", Integer.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", Long.class));
        return entity;
    }
}
