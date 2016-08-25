package com.whois.whoiswho.model;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by stasenkopavel on 8/25/16.
 */
@Migration(version = 2, database = Database.class)
public class MigrationAddIsSearchedColumn extends AlterTableMigration<Phone> {

    public MigrationAddIsSearchedColumn(Class<Phone> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();
        addColumn(SQLiteType.INTEGER, "isSearched");
    }
}
