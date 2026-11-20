# Workshop 4w + 5w — Object-Oriented Car Dealership

A Java console application for a used-car sales manager: it loads a dealership and its vehicle
inventory from a pipe-delimited text file, lets the user search, add, remove, sell and lease
vehicles through a text menu, records every contract in a contracts file, and writes the
inventory file back out after every change.

Built for the *Workshop 4w* brief (`workshop-4w-car-dealership.pdf`) in five phases — domain
classes, persistence layer, user interface layer, entry point, then the search / add / remove
logic — and extended for *Workbook 5w* (`Workbook 5w - Workshop - Car Dealership v7.2Y.pdf`)
with sales, leases, contract persistence, an admin contract browser, and sale add-ons.

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

48 tests covering the searches, add/remove, the range boundaries, reading and writing the
inventory file, the sale / lease pricing rules, the contract file, and the add-on catalog
round-trip. Reports land in `app/build/reports/tests/test/index.html`.

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
| 10 | Sell / lease a vehicle |
| 11 | Admin — browse contracts (password: `admin`) |
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

Options 8, 9 and 10 rewrite the inventory file, so what is on disk always matches what the lot holds.

## Sell / lease a vehicle (option 10)

The manager picks a VIN off the lot, enters the customer's name and email and a contract date
(`yyyyMMdd`, empty defaults to today), and chooses sale or lease. Only vehicles at most 3 model
years old can be leased. A sale can add optional extras (nitrogen tires, floor mats, cargo tray,
window tinting, splash guards, wheel locks) and can be financed. The app prints the full pricing
breakdown before asking for confirmation, then appends the contract to the contracts file and
removes the vehicle from inventory.

```
Choose an option: 10
VIN of the vehicle to sell or lease (empty to cancel): 10112
Contracting for: 10112|1993|Ford|Explorer|SUV|Red|525123|995.00
...
Contract for Dana Wyatt on 1993 Ford Explorer (10112), dated 20261120:
  Vehicle price:      995.00
  Add-on:             Nitrogen tires  220.00
  Add-on:             Wheel locks  85.00
  Sales tax (5%):     65.00
  Recording fee:      100.00
  Processing fee:     295.00
  Total price:        1760.00
  Paid in full, no monthly payment.
Contract saved to /…/app/data/contracts.csv
10112 left the lot.
```

Pricing rules:

- **Sale:** total = vehicle price + add-ons + 5% sales tax (on the taxed amount) + $100 recording
  fee + processing fee ($295 under a $10,000 vehicle price, $495 at or above it). Financing turns
  the total into an amortized loan: 4.25% for 48 months at a $10,000+ vehicle price, 5.25% for
  24 months otherwise; paying in full means a monthly payment of `0.00`.
- **Lease:** total = expected ending value (50% of the vehicle price) + lease fee (7% of the
  vehicle price); the monthly payment finances that total at 4.0% for 36 months.
- **Admin (option 11)** asks for the password (`admin`) and then lists all contracts or the last
  ten from the contracts file.

## Contracts file

`app/data/contracts.csv`, pipe-delimited, created on the first contract. Every line is one
contract: 12 common columns, then a type-specific tail, then the sale's add-ons (empty for
leases). All contracts go in this one file.

```
SALE|20210928|Dana Wyatt|dana@texas.com|10112|1993|Ford|Explorer|SUV|Red|525123|995.00|49.75|100.00|295.00|1439.75|NO|0.00|
LEASE|20210928|Zachary Westly|zach@texas.com|37846|2021|Chevrolet|Silverado|TRUCK|Black|2750|31995.00|15997.50|2239.65|18237.15|538.43|
```

```
common:  TYPE|DATE|NAME|EMAIL|VIN|YEAR|MAKE|MODEL|VEHICLE_TYPE|COLOR|ODOMETER|VEHICLE_PRICE
SALE:    SALES_TAX|RECORDING_FEE|PROCESSING_FEE|TOTAL_PRICE|FINANCE_OPTION|MONTHLY_PAYMENT|ADDONS
LEASE:   EXPECTED_ENDING_VALUE|LEASE_FEE|TOTAL_PRICE|MONTHLY_PAYMENT|ADDONS
```

Add-ons are written as `name@price` pairs separated by semicolons:
`Nitrogen tires@220.00;Wheel locks@85.00`. The stored money columns document the deal; loading a
contract recomputes them from the vehicle price, so a hand edit of a fee column shows up as a
different number in the admin listings rather than corrupting anything.

## Classes

| Class | Responsibility |
| --- | --- |
| `Program` | `main()`: create the `UserInterface` and call `display()` |
| `UserInterface` | menu, prompts, listings; owns the `Dealership`; dispatches each menu choice; saves after every add, remove or contract |
| `AdminUserInterface` | password-gated contract browsing: list all, list last 10 |
| `Dealership` | name, address, phone, the vehicle list, and the search / add / remove methods |
| `DealershipFileManager` | reads the inventory file into a `Dealership`, writes a `Dealership` back over it |
| `ContractDataManager` | appends a `Contract` to the contracts file, reads them all back |
| `Vehicle` | one vehicle; `toString()` is the record line, `equals()` compares VINs |
| `VehicleType` | the four categories the lot stocks: `CAR`, `TRUCK`, `SUV`, `VAN` |
| `Contract` | abstract sale-or-lease agreement: date, customer, vehicle; abstract `getTotalPrice()` / `getMonthlyPayment()` |
| `SalesContract` | tax, recording and processing fees, financing tiers (4.25%/48 or 5.25%/24), add-ons |
| `LeaseContract` | expected ending value (50%), lease fee (7%), 4.0%/36-month monthly payment, 3-year age rule |
| `AddOn` | one optional sale extra with a fixed price; the catalog is hard-coded |

Package `net.tarou.dealership`, in the Maven layout Gradle expects:

```
app/
├── build.gradle.kts
├── data/inventory.csv          # the lot, plus inventory.csv.bak, plus contracts.csv
└── src
    ├── main/java/net/tarou/dealership/
    │   ├── Program.java
    │   ├── UserInterface.java
    │   ├── AdminUserInterface.java
    │   ├── Dealership.java
    │   ├── DealershipFileManager.java
    │   ├── ContractDataManager.java
    │   ├── Vehicle.java
    │   ├── VehicleType.java
    │   ├── Contract.java
    │   ├── SalesContract.java
    │   ├── LeaseContract.java
    │   └── AddOn.java
    └── test/java/net/tarou/dealership/
        ├── DealershipTest.java
        ├── DealershipFileManagerTest.java
        ├── ContractTest.java
        └── ContractDataManagerTest.java
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

## Sale / lease behaviour worth knowing

- **Money is `BigDecimal`, rounded to cents** (half-up) at every step: fees, taxes, totals and
  monthly payments.
- **Monthly payments are amortized loan payments**: a fixed payment that pays the principal off
  exactly after the term, at the yearly rate compounded monthly.
- **Both $10,000 thresholds key off the vehicle price**, not the contract total, and exactly
  `10000.00` falls in the upper tier.
- **The lease age rule uses the model year**: leasable in the year it was made plus the three
  years after. The check happens before any data is collected for the contract.
- **Contracts append, inventory rewrites.** A contract is never edited in place; the contracts
  file only grows, while `inventory.csv` is rewritten after each sale so the sold VIN leaves the lot.
