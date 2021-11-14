package com.gachokaerick.eshop.store.repository.rowmapper;

import com.gachokaerick.eshop.store.domain.Buyer;
import com.gachokaerick.eshop.store.domain.enumeration.Gender;
import com.gachokaerick.eshop.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Buyer}, with proper type conversions.
 */
@Service
public class BuyerRowMapper implements BiFunction<Row, String, Buyer> {

    private final ColumnConverter converter;

    public BuyerRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Buyer} stored in the database.
     */
    @Override
    public Buyer apply(Row row, String prefix) {
        Buyer entity = new Buyer();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setGender(converter.fromRow(row, prefix + "_gender", Gender.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", String.class));
        return entity;
    }
}
