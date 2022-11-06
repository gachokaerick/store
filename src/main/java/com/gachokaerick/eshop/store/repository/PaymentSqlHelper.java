package com.gachokaerick.eshop.store.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PaymentSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("create_time", table, columnPrefix + "_create_time"));
        columns.add(Column.aliased("update_time", table, columnPrefix + "_update_time"));
        columns.add(Column.aliased("payment_status", table, columnPrefix + "_payment_status"));
        columns.add(Column.aliased("payer_country_code", table, columnPrefix + "_payer_country_code"));
        columns.add(Column.aliased("payer_email", table, columnPrefix + "_payer_email"));
        columns.add(Column.aliased("payer_name", table, columnPrefix + "_payer_name"));
        columns.add(Column.aliased("payer_surname", table, columnPrefix + "_payer_surname"));
        columns.add(Column.aliased("payer_id", table, columnPrefix + "_payer_id"));
        columns.add(Column.aliased("currency", table, columnPrefix + "_currency"));
        columns.add(Column.aliased("amount", table, columnPrefix + "_amount"));
        columns.add(Column.aliased("payment_id", table, columnPrefix + "_payment_id"));

        columns.add(Column.aliased("order_id", table, columnPrefix + "_order_id"));
        return columns;
    }
}
