package me.cobeine.holograms.spigot.storage.mysql;

import com.google.gson.JsonArray;
import me.cobeine.sqlava.connection.database.table.Table;
import me.cobeine.sqlava.connection.database.table.column.Column;
import me.cobeine.sqlava.connection.database.table.column.ColumnSettings;
import me.cobeine.sqlava.connection.database.table.column.ColumnType;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public class HologramsTable extends Table {

    public HologramsTable(String name) {
        super(name);
        addColumns(
                Column.of("id", ColumnType.VARCHAR).size(64).settings(ColumnSettings.NOT_NULL, ColumnSettings.UNIQUE),
                Column.of("lines",ColumnType.LONGTEXT).defaultValue(new JsonArray().toString()).settings(ColumnSettings.NOT_NULL)
        );
        setPrimaryKey("id");
    }
}
