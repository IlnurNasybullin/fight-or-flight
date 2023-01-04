package io.github.ilnurnasybullin.fight.or.flight.game;

import io.github.ilnurnasybullin.fight.or.flight.core.player.ActivePlayer;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.StrategyFactory;

public record PlayerWithMetadata(ActivePlayer player, StrategyFactory strategyFactory) {
}
