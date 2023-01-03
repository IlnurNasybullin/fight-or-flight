package io.github.ilnurnasybullin.fight.or.flight.core.unit;

import java.util.ServiceLoader;

public interface UnitDamageTable {
    ThatAttack unit(Unit unit);

    interface ThatAttack {
        HasDamage thatAttack(Unit unit);
    }

    interface HasDamage {
        int hasDamage();
    }

    static UnitDamageTable getInstance() {
        return ServiceLoader.load(UnitDamageTable.class)
                .findFirst()
                .orElseThrow();
    }
}
