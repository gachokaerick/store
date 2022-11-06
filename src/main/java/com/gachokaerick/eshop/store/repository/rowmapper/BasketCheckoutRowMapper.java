package com.gachokaerick.eshop.store.repository.rowmapper;

import com.gachokaerick.eshop.store.domain.BasketCheckout;
import com.gachokaerick.eshop.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link BasketCheckout}, with proper type conversions.
 */
@Service
public class BasketCheckoutRowMapper implements BiFunction<Row, String, BasketCheckout> {

    private final ColumnConverter converter;

    public BasketCheckoutRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link BasketCheckout} stored in the database.
     */
    @Override
    public BasketCheckout apply(Row row, String prefix) {
        BasketCheckout entity = new BasketCheckout();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setStreet(converter.fromRow(row, prefix + "_street", String.class));
        entity.setCity(converter.fromRow(row, prefix + "_city", String.class));
        entity.setTown(converter.fromRow(row, prefix + "_town", String.class));
        entity.setCountry(converter.fromRow(row, prefix + "_country", String.class));
        entity.setZipcode(converter.fromRow(row, prefix + "_zipcode", String.class));
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
        entity.setUserLogin(converter.fromRow(row, prefix + "_user_login", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
