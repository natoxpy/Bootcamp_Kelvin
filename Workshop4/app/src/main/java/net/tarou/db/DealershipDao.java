package net.tarou.db;

import org.jdbi.v3.sqlobject.statement.SqlPreflight;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;

public interface DealershipDao {
    @SqlPreflight("PRAGMA foreign_keys = ON;")
    @SqlScript("CREATE TABLE dealerships (dealership_id INTEGER PRIMARY KEY, name TEXT NOT NULL);")
    // @SqlScript("CREATE TABLE vehicle (id INTEGER PRIMARY KEY, dealership_id INT NOT NULL "
    //     + "REFERENCES dealerships(dealership_id), vin TEXT NOT NULL);")
    public void
    createTable();

    @SqlQuery("SELECT * FROM Dealership")
    public Dealership getDealership();
}
