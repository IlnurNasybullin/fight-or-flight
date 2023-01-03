package io.github.ilnurnasybullin.fight.or.flight.unit;

import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitTemplate;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitType;

import java.util.Objects;

public record UnitTemplateRecord(String name, UnitType type) implements UnitTemplate {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UnitTemplateRecord other = (UnitTemplateRecord) obj;
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
