package io.github.ilnurnasybullin.fight.or.flight.core.player;

import io.github.ilnurnasybullin.fight.or.flight.core.game.Battle1on1;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.MoveStrategy;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.Strategy;

public interface ActivePlayer extends Player {
    MoveStrategy moveStrategy(Battle1on1.State  battleState);
    void setStrategy(Strategy strategy);
}
