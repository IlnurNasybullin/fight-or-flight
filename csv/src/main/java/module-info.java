import io.github.ilnurnasybullin.fight.or.flight.csv.CsvReader;
import io.github.ilnurnasybullin.fight.or.flight.csv.CsvReaderImpl;

module io.github.ilnurnasybullin.fight.or.flight.csv {
    exports io.github.ilnurnasybullin.fight.or.flight.csv;

    uses CsvReader;
    provides CsvReader with CsvReaderImpl;
}