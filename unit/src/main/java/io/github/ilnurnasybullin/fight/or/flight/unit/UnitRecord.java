package io.github.ilnurnasybullin.fight.or.flight.unit;

import io.github.ilnurnasybullin.fight.or.flight.core.unit.ArmorType;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.AttackType;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitType;

import java.time.Duration;
import java.util.List;

public record UnitRecord(UnitType type, Force force, int hp, int armorValue, ArmorType armorType, int attackMinValue,
                         AttackType attackType, Duration cooldown, List<Force> targetForces, boolean weaknessToMagic,
                         int gold, Duration buildTime) {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UnitRecord other = (UnitRecord) obj;

        return type == other.type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
