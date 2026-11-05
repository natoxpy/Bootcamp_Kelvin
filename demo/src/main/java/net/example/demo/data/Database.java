package net.example.demo.data;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private int cachedVersion = 0;
    private int currentCached = -1;
    private List<Transaction> cachedData;
    private List<Transaction> memoryData = new ArrayList<>();
    private File file = new File("demo/src/main/resources/transactions.csv");

    public Database() {
        this.recache();
    }

    public void forceRecache() {
        this.cachedVersion++;
    }

    public void addTransaction(Transaction t) {
        this.memoryData.add(t);
    }

    public void save() {
        this.forceRecache();
        this.write(this.memoryData);
    }

    public void discard() {
        this.memoryData.clear();
    }

    public List<Transaction> getData() {
        if (this.cachedVersion < this.currentCached)
            return this.recache();

        return this.cachedData;
    }

    private List<Transaction> recache() {
        this.currentCached = this.cachedVersion;

        var data = this.read();

        this.cachedData = data;

        return this.cachedData;
    }

    private List<Transaction> read() {
        CsvMapper mapper = new CsvMapper();
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        mapper.findAndRegisterModules();

        CsvSchema schema =
            mapper.schemaFor(Transaction.class).withHeader().withColumnSeparator('|');

        try {
            MappingIterator<Transaction> it =
                mapper.readerFor(Transaction.class).with(schema).readValues(file);

            return it.readAll(new ArrayList<>());

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    private void write(List<Transaction> transactions) {
        CsvMapper mapper = new CsvMapper();
        mapper.findAndRegisterModules();

        CsvSchema schema =
            mapper.schemaFor(Transaction.class).withHeader().withColumnSeparator('|');

        try (SequenceWriter writer =
                 mapper.writerFor(Transaction.class).with(schema).writeValues(this.file)) {
            writer.writeAll(transactions);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
