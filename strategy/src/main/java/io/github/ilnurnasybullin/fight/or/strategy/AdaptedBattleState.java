package io.github.ilnurnasybullin.fight.or.strategy;

import io.github.ilnurnasybullin.fight.or.flight.core.game.Battle1on1;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.Unit;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitState;

import java.util.Map;

class AdaptedBattleState implements Battle1on1.State {

    private final Battle1on1.State state;
    private final Map<Unit, Integer> damages;

    AdaptedBattleState(Battle1on1.State state, Map<Unit, Integer> damages) {
        this.state = state;
        this.damages = damages;
    }

    @Override
    public Player opponent(Player player) {
        return state.opponent(player);
    }

    @Override
    public UnitState unitState(Unit unit) {
        UnitState unitState = state.unitState(unit);
        if (unitState == UnitState.DEAD) {
            return unitState;
        }

        if (damages.getOrDefault(unit, 0) >= unit.ehp()) {
            return UnitState.DEAD;
        }

        return unitState;
    }
}
