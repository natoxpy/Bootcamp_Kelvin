package net.tarou.db;

import net.tarou.db.Vehicle;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

public class Database {
    private Jdbi jdbi;

    public Database(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    // public void getDealership() {
    //     try (Handle handle = jdbi.open()) {
    //         var result = handle.createQuery("SELECT * FROM Dealership").mapTo(Vehicle.class);
    //     }
    // }
}
