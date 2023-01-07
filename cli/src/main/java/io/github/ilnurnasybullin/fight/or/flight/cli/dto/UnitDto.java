package io.github.ilnurnasybullin.fight.or.flight.cli.dto;

import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitTemplate;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitType;
import io.github.ilnurnasybullin.fight.or.flight.unit.UnitTemplateRecord;

import java.util.Locale;

public class UnitDto {

    private String name;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UnitType unitType() {
        return UnitType.valueOf(type.toUpperCase(Locale.ENGLISH));
    }

    public UnitTemplate template() {
        return new UnitTemplateRecord(name, unitType());
    }
}
