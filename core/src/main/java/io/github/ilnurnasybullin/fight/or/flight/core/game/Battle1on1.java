package io.github.ilnurnasybullin.fight.or.flight.core.game;

import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.Unit;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitState;

public interface Battle1on1 extends Runnable {
    Battle1on1.State state();

    interface State {
        Player opponent(Player player);
        UnitState unitState(Unit unit);
    }
}
