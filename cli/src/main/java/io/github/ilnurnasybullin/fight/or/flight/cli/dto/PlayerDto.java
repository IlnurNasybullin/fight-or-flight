package io.github.ilnurnasybullin.fight.or.flight.cli.dto;

import io.github.ilnurnasybullin.fight.or.flight.core.player.Race;

import java.util.List;
import java.util.Locale;

public class PlayerDto {

    private String name;
    private String race;
    private List<UnitDto> units;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public List<UnitDto> getUnits() {
        return units;
    }

    public void setUnits(List<UnitDto> units) {
        this.units = units;
    }

    public Race race() {
        return Race.valueOf(race.toUpperCase(Locale.ENGLISH));
    }
}
