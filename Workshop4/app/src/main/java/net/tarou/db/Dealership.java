package net.tarou.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

// import org.jdbi.v3.sqlobject.statement.SqlQuery;

public record Dealership(String name, String address, String phone) {
    public class DealershipMapper implements RowMapper<Dealership> {
        @Override
        public Dealership map(ResultSet rs, StatementContext ctx) throws SQLException {
            return new Dealership(rs.getString("name"), null, null);
        }
    }

    // public Stream<Vehicle> inventoryStream() {
    //     return this.inventory.stream();
    // }

    // public class Filters {
    //     public static Predicate<Vehicle> byPrice(int min, int max) {
    //         return (e) -> {
    //             int x = e.getPrice().intValue();
    //             return x > min && x < max;
    //         };
    //     }

    //     public static Predicate<Vehicle> byMakeAndModel(String make, String model) {
    //         return (e) -> e.getMake().contains(make) && e.getModel().contains(model);
    //     }

    //     public static Predicate<Vehicle> byYear(int min, int max) {
    //         return (e) -> e.getYear() > min && e.getYear() < max;
    //     }

    //     public static Predicate<Vehicle> byYear(String color) {
    //         return (e) -> e.getColor().contains(color);
    //     }

    //     public static Predicate<Vehicle> byMileage(int min, int max) {
    //         return (e) -> e.getOdometer() > min && e.getOdometer() < max;
    //     }

    //     public static Predicate<Vehicle> byType(VehicleType vtype) {
    //         return (e) -> e.getVtype() == vtype;
    //     }
    // }
}
