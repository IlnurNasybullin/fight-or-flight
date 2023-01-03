package io.github.ilnurnasybullin.fight.or.strategy;

import io.github.ilnurnasybullin.fight.or.flight.core.game.Battle1on1;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;

interface ObjectiveFunctionCalculator {
    WithBattleState player(Player player);

    interface WithBattleState {
        HasValue withBattleState(Battle1on1.State battleState);
    }

    interface HasValue {
        double hasValue();
    }
}
