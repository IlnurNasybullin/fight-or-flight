package io.github.ilnurnasybullin.fight.or.strategy;

import io.github.ilnurnasybullin.fight.or.flight.core.game.Battle1on1;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.Unit;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitState;

class ObjectiveFunctionCalculatorImpl implements ObjectiveFunctionCalculator {

    @Override
    public WithBattleState player(Player player) {
        return new Context(this, player);
    }

    private double value(Player player, Battle1on1.State battleState) {
        int playerValue = calculateValue(player, battleState);
        int opponentValue = calculateValue(battleState.opponent(player), battleState);

        return ((double) playerValue) / opponentValue;
    }

    private int calculateValue(Player player, Battle1on1.State battleState) {
        return player.party()
                .units()
                .stream()
                .filter(unit -> battleState.unitState(unit) != UnitState.DEAD)
                .mapToInt(this::calculateValue)
                .sum();
    }

    private int calculateValue(Unit unit) {
        return (int) (unit.gold() * unit.buildTime().toSeconds());
    }

    private static class Context implements WithBattleState, HasValue {

        private final ObjectiveFunctionCalculatorImpl owner;
        private final Player player;
        private Battle1on1.State battleState;

        private Context(ObjectiveFunctionCalculatorImpl owner, Player player) {
            this.owner = owner;
            this.player = player;
        }

        @Override
        public HasValue withBattleState(Battle1on1.State battleState) {
            this.battleState = battleState;
            return this;
        }

        @Override
        public double hasValue() {
            return owner.value(player, battleState);
        }
    }
}
