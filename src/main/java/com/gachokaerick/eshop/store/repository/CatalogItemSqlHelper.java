package com.gachokaerick.eshop.store.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CatalogItemSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("price", table, columnPrefix + "_price"));
        columns.add(Column.aliased("picture_file_name", table, columnPrefix + "_picture_file_name"));
        columns.add(Column.aliased("picture_url", table, columnPrefix + "_picture_url"));
        columns.add(Column.aliased("available_stock", table, columnPrefix + "_available_stock"));
        columns.add(Column.aliased("restock_threshold", table, columnPrefix + "_restock_threshold"));
        columns.add(Column.aliased("max_stock_threshold", table, columnPrefix + "_max_stock_threshold"));
        columns.add(Column.aliased("on_reorder", table, columnPrefix + "_on_reorder"));

        columns.add(Column.aliased("catalog_brand_id", table, columnPrefix + "_catalog_brand_id"));
        columns.add(Column.aliased("catalog_type_id", table, columnPrefix + "_catalog_type_id"));
        return columns;
    }
}
