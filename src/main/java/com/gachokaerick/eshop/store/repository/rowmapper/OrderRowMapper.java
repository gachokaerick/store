package com.gachokaerick.eshop.store.repository.rowmapper;

import com.gachokaerick.eshop.store.domain.Order;
import com.gachokaerick.eshop.store.domain.enumeration.OrderStatus;
import com.gachokaerick.eshop.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Order}, with proper type conversions.
 */
@Service
public class OrderRowMapper implements BiFunction<Row, String, Order> {

    private final ColumnConverter converter;

    public OrderRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Order} stored in the database.
     */
    @Override
    public Order apply(Row row, String prefix) {
        Order entity = new Order();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setOrderDate(converter.fromRow(row, prefix + "_order_date", ZonedDateTime.class));
        entity.setOrderStatus(converter.fromRow(row, prefix + "_order_status", OrderStatus.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setAddressId(converter.fromRow(row, prefix + "_address_id", Long.class));
        entity.setBuyerId(converter.fromRow(row, prefix + "_buyer_id", Long.class));
        return entity;
    }
}
