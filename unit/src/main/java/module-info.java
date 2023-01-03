import io.github.ilnurnasybullin.fight.or.flight.unit.UnitRepository;
import io.github.ilnurnasybullin.fight.or.flight.unit.UnitRepositoryImpl;
import io.github.ilnurnasybullin.fight.or.flight.unit.csv.CsvReader;
import io.github.ilnurnasybullin.fight.or.flight.unit.csv.CsvReaderImpl;

module io.github.ilnurnasybullin.fight.or.flight.unit {
    requires io.github.ilnurnasybullin.fight.or.flight.core;
    uses UnitRepository;
    provides UnitRepository with UnitRepositoryImpl;

    uses CsvReader;
    provides CsvReader with CsvReaderImpl;

    exports io.github.ilnurnasybullin.fight.or.flight.unit;
    exports io.github.ilnurnasybullin.fight.or.flight.unit.csv;
}