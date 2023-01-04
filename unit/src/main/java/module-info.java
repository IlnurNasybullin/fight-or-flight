import io.github.ilnurnasybullin.fight.or.flight.unit.UnitRepository;
import io.github.ilnurnasybullin.fight.or.flight.unit.UnitRepositoryImpl;

module io.github.ilnurnasybullin.fight.or.flight.unit {
    requires io.github.ilnurnasybullin.fight.or.flight.core;
    requires io.github.ilnurnasybullin.fight.or.flight.csv;
    uses UnitRepository;
    provides UnitRepository with UnitRepositoryImpl;

    exports io.github.ilnurnasybullin.fight.or.flight.unit;
}