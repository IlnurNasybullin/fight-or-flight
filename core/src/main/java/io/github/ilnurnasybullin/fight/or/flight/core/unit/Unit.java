package io.github.ilnurnasybullin.fight.or.flight.core.unit;

import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;

public interface Unit extends Resourceable {
    int hp();
    int ehp();
    String name();
    Attack attack();
    Armor armor();
    UnitType type();
    Player owner();
}
