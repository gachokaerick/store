package com.gachokaerick.eshop.store.repository.rowmapper;

import com.gachokaerick.eshop.store.domain.Payment;
import com.gachokaerick.eshop.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Payment}, with proper type conversions.
 */
@Service
public class PaymentRowMapper implements BiFunction<Row, String, Payment> {

    private final ColumnConverter converter;

    public PaymentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Payment} stored in the database.
     */
    @Override
    public Payment apply(Row row, String prefix) {
        Payment entity = new Payment();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCreateTime(converter.fromRow(row, prefix + "_create_time", ZonedDateTime.class));
        entity.setUpdateTime(converter.fromRow(row, prefix + "_update_time", ZonedDateTime.class));
        entity.setPaymentStatus(converter.fromRow(row, prefix + "_payment_status", String.class));
        entity.setPayerCountryCode(converter.fromRow(row, prefix + "_payer_country_code", String.class));
        entity.setPayerEmail(converter.fromRow(row, prefix + "_payer_email", String.class));
        entity.setPayerName(converter.fromRow(row, prefix + "_payer_name", String.class));
        entity.setPayerSurname(converter.fromRow(row, prefix + "_payer_surname", String.class));
        entity.setPayerId(converter.fromRow(row, prefix + "_payer_id", String.class));
        entity.setCurrency(converter.fromRow(row, prefix + "_currency", String.class));
        entity.setAmount(converter.fromRow(row, prefix + "_amount", BigDecimal.class));
        entity.setPaymentId(converter.fromRow(row, prefix + "_payment_id", String.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", Long.class));
        return entity;
    }
}
