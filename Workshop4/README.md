# Workshop 4w — Object-Oriented Car Dealership

A Java console application for a used-car sales manager: it loads a dealership and its vehicle
inventory from a pipe-delimited text file, lets the user search, add and remove vehicles through a
text menu, and writes the inventory file back out after every change.

Built for the *Workshop 4w* brief (`workshop-4w-car-dealership.pdf`), in five phases: domain
classes, persistence layer, user interface layer, entry point, then the search / add / remove
logic.

## Requirements

- JDK 17 or newer
- No other install: Gradle comes with the wrapper (`./gradlew`, `gradlew.bat`)

## Run it

```bash
./gradlew --console=plain -q :app:run
```

`--console=plain -q` keeps Gradle's progress bar out of the way so the menu is the only thing in
the terminal.

Or build a launcher once and run the app without Gradle:

```bash
./gradlew :app:installDist
cd app
./build/install/app/bin/app          # Windows: build\install\app\bin\app.bat
```

Run it from the `app` directory (both commands above already do): the inventory file is opened as
`data/inventory.csv` relative to the working directory, which resolves to `app/data/inventory.csv`.

First thing the app prints:

```
Loaded D & B Used Cars (10 vehicle(s)) from /…/app/data/inventory.csv
```

If the file is missing the app says so and starts with an empty lot instead of crashing.

## Run the tests

```bash
./gradlew :app:test
```

30 tests covering the searches, add/remove, the range boundaries, and reading and writing the
inventory file. Reports land in `app/build/reports/tests/test/index.html`.

## Menu

| Choice | What it does |
| --- | --- |
| 1 | Find vehicles within a price range |
| 2 | Find vehicles by make / model |
| 3 | Find vehicles by year range |
| 4 | Find vehicles by color |
| 5 | Find vehicles by mileage range |
| 6 | Find vehicles by type (car, truck, SUV, van) |
| 7 | List ALL vehicles |
| 8 | Add a vehicle |
| 9 | Remove a vehicle (by VIN) |
| 99 | Quit |

```
Choose an option: 1
Lowest price to include (for example 2000): 2000
Highest price to include (for example 15000): 15000

Vehicles priced between 2000 and 15000:

VIN      YEAR  MAKE         MODEL            TYPE   COLOR      ODOMETER        PRICE
44901    2012  Honda        Civic            CAR    Gray         103221      6995.00
55210    2008  Chevrolet    Silverado        TRUCK  Black        142980     11500.00
71345    2004  Honda        Odyssey          VAN    White        188300      8750.00
77002    1999  Ford         Taurus           CAR    Green        201450      2400.00
90456    2011  Ford         F-150            TRUCK  Red          129000     14200.00
95512    2015  Chrysler     Town & Country   VAN    Gray          97650     12300.00

6 vehicle(s).
```

## Inventory file

`app/data/inventory.csv`, pipe-delimited. The first line is the dealership, every line after it is
one vehicle. `app/data/inventory.csv.bak` is the untouched starter copy, in case a hand edit goes
wrong.

```
D & B Used Cars|111 Old Benbrook Rd|817-555-5555
10112|1993|Ford|Explorer|SUV|Red|525123|995.00
37846|2001|Ford|Ranger|truck|Yellow|172544|1995.00
```

```
name|address|phone
vin|year|make|model|type|color|odometer|price
```

Blank lines are ignored. Bad data is reported with its line number and the column that caused it:

```
Line 4 of /…/app/data/inventory.csv has vehicle type "spaceship", which is not car, truck, SUV or van: …
```

Options 8 and 9 rewrite the file, so what is on disk always matches what the lot holds.

## Classes

| Class | Responsibility |
| --- | --- |
| `Program` | `main()`: create the `UserInterface` and call `display()` |
| `UserInterface` | menu, prompts, listings; owns the `Dealership`; dispatches each menu choice; saves after every add or remove |
| `Dealership` | name, address, phone, the vehicle list, and the search / add / remove methods |
| `DealershipFileManager` | reads the inventory file into a `Dealership`, writes a `Dealership` back over it |
| `Vehicle` | one vehicle; `toString()` is the record line, `equals()` compares VINs |
| `VehicleType` | the four categories the lot stocks: `CAR`, `TRUCK`, `SUV`, `VAN` |

Package `net.tarou.dealership`, in the Maven layout Gradle expects:

```
app/
├── build.gradle.kts
├── data/inventory.csv          # the lot, plus inventory.csv.bak
└── src
    ├── main/java/net/tarou/dealership/
    │   ├── Program.java
    │   ├── UserInterface.java
    │   ├── Dealership.java
    │   ├── DealershipFileManager.java
    │   ├── Vehicle.java
    │   └── VehicleType.java
    └── test/java/net/tarou/dealership/
        ├── DealershipTest.java
        └── DealershipFileManagerTest.java
```

## Search behaviour worth knowing

- **Ranges are exclusive at both ends.** A vehicle priced exactly `2000` is not in the range
  `2000`–`15000`. Same for model years and mileage. Prices are compared with
  `BigDecimal.compareTo`, so `2000` and `2000.00` are the same price.
- **Text searches ignore case and stray spaces.** `ford`/`RANGER` matches `Ford` `Ranger`, and
  ` RED ` matches `Red`.
- **A vehicle is its VIN.** `Vehicle.equals()` compares VINs only, so removing a vehicle needs
  nothing but the VIN the sales manager typed.
- **Type input is forgiving.** `suv`, `SUV`, `pickup`, `minivan`, `auto` and `sport utility` all
  map onto the four categories; anything else is rejected and, when adding, asked for again.
- **Bad input never crashes the app.** A non-numeric menu choice, price or mileage prints a
  message and returns to the menu; reversed range bounds are swapped out loud.
