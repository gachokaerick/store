package com.gachokaerick.eshop.store.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class BasketItemSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("product_id", table, columnPrefix + "_product_id"));
        columns.add(Column.aliased("product_name", table, columnPrefix + "_product_name"));
        columns.add(Column.aliased("unit_price", table, columnPrefix + "_unit_price"));
        columns.add(Column.aliased("old_unit_price", table, columnPrefix + "_old_unit_price"));
        columns.add(Column.aliased("quantity", table, columnPrefix + "_quantity"));
        columns.add(Column.aliased("picture_url", table, columnPrefix + "_picture_url"));
        columns.add(Column.aliased("user_login", table, columnPrefix + "_user_login"));

        return columns;
    }
}
