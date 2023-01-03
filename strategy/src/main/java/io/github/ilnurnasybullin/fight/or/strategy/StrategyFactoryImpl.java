package io.github.ilnurnasybullin.fight.or.strategy;

import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.Strategy;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.StrategyFactory;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitDamageTable;

public class StrategyFactoryImpl implements StrategyFactory {

    @Override
    public AndDefender forAttacker(Player attacker) {
        return new Context(this, attacker);
    }

    private Strategy buildStrategyForAttacker(Player attacker, Player defender, UnitDamageTable unitDamageTable) {
        return new GreedyAlgorithm(attacker, defender, unitDamageTable, new ObjectiveFunctionCalculatorImpl(), new OnLastValueMoveTypeIdentifier());
    }

    private Strategy buildStrategyForDefender(Player attacker, Player defender, UnitDamageTable unitDamageTable) {
        return new GreedyAlgorithm(defender, attacker, unitDamageTable, new ObjectiveFunctionCalculatorImpl(), new OnLastValueMoveTypeIdentifier());
    }

    private static class Context implements AndDefender, WithUnitDamageTable, BuildStrategy {

        private final StrategyFactoryImpl strategyFactory;
        private final Player attacker;
        private Player defender;
        private UnitDamageTable unitDamageTable;

        private Context(StrategyFactoryImpl strategyFactory, Player attacker) {
            this.strategyFactory = strategyFactory;
            this.attacker = attacker;
        }

        @Override
        public WithUnitDamageTable andDefender(Player defender) {
            this.defender = defender;
            return this;
        }

        @Override
        public BuildStrategy withUnitDamageTable(UnitDamageTable unitDamageTable) {
            this.unitDamageTable = unitDamageTable;
            return this;
        }

        @Override
        public Strategy buildStrategyForAttacker() {
            return this.strategyFactory.buildStrategyForAttacker(attacker, defender, unitDamageTable);
        }

        @Override
        public Strategy buildStrategyForDefender() {
            return this.strategyFactory.buildStrategyForDefender(attacker, defender, unitDamageTable);
        }
    }
}
