package io.github.ilnurnasybullin.fight.or.flight.unit;

import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitType;

import java.util.Collection;
import java.util.Optional;
import java.util.ServiceLoader;

public interface UnitRepository {
    Collection<UnitRecord> units();
    Optional<UnitRecord> findByType(UnitType unitType);

    static UnitRepository getInstance() {
        return ServiceLoader.load(UnitRepository.class)
                .findFirst()
                .orElseThrow();
    }
}
