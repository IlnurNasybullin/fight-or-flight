package io.github.ilnurnasybullin.fight.or.flight.core.strategy;

import io.github.ilnurnasybullin.fight.or.flight.core.game.Battle1on1;

public interface Strategy {
    MoveStrategy onMove(Battle1on1.State battleState);
}
