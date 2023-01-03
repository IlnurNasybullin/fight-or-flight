package io.github.ilnurnasybullin.fight.or.flight.core.party;

import io.github.ilnurnasybullin.fight.or.flight.core.unit.Unit;

import java.util.List;

public interface Party {
    List<Unit> units();
    default int size() {
        return (int) units().stream()
                .filter(unit -> unit.hp() > 0)
                .count();
    }
}
