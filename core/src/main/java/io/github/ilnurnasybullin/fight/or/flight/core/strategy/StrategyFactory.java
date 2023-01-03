package io.github.ilnurnasybullin.fight.or.flight.core.strategy;

import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitDamageTable;

public interface StrategyFactory {

    AndDefender forAttacker(Player attacker);

    interface BuildStrategy {
        Strategy buildStrategyForAttacker();
        Strategy buildStrategyForDefender();
    }

    interface WithUnitDamageTable {
        BuildStrategy withUnitDamageTable(UnitDamageTable unitDamageTable);
    }

    interface AndDefender {
        WithUnitDamageTable andDefender(Player defender);
    }
}
