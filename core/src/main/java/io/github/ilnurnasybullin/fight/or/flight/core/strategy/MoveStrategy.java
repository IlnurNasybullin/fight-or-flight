package io.github.ilnurnasybullin.fight.or.flight.core.strategy;

import io.github.ilnurnasybullin.fight.or.flight.core.unit.Unit;

import java.util.Optional;

public interface MoveStrategy {

    MoveType type();
    HasTarget unit(Unit unit);

    interface HasTarget {
        Optional<Unit> hasTarget();
    }
}
