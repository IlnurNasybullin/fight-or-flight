package io.github.ilnurnasybullin.fight.or.strategy;

import io.github.ilnurnasybullin.fight.or.flight.core.strategy.MoveType;

interface MoveTypeIdentifier {
    MoveType objectiveFunction(double objectiveFunction);
}
