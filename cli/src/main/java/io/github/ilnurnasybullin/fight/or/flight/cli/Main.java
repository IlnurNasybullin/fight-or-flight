package io.github.ilnurnasybullin.fight.or.flight.cli;

import io.github.ilnurnasybullin.fight.or.flight.core.game.Game;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Race;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitDamageTable;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitType;
import io.github.ilnurnasybullin.fight.or.flight.unit.UnitTemplateRecord;
import io.github.ilnurnasybullin.fight.or.strategy.StrategyFactoryImpl;

import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        Game.Builder.getInstance()
                .time(Duration.ofMillis(500))
                .hasTacts(1)
                .unitDamageTable(UnitDamageTable.getInstance())
                .playerWithName("Игрок №1")
                .andRace(Race.HUMAN)
                .hasPartyWithUnit(new UnitTemplateRecord("Пехотинец №1", UnitType.FOOTMAN))
                .andUnit(new UnitTemplateRecord("Пехотинец №2", UnitType.FOOTMAN))
                .andUnit(new UnitTemplateRecord("Пехотинец №3", UnitType.FOOTMAN))
                .andUnit(new UnitTemplateRecord("Пехотинец №4", UnitType.FOOTMAN))
                .andUnit(new UnitTemplateRecord("Стрелок №1", UnitType.RIFLEMAN))
                .andUnit(new UnitTemplateRecord("Стрелок №2", UnitType.RIFLEMAN))
                .andUnit(new UnitTemplateRecord("Стрелок №3", UnitType.RIFLEMAN))
                .andUnit(new UnitTemplateRecord("Рыцарь №1", UnitType.KNIGHT))
                .andUnit(new UnitTemplateRecord("Рыцарь №2", UnitType.KNIGHT))
                .andUnit(new UnitTemplateRecord("Рыцарь №3", UnitType.KNIGHT))
                .andHasStrategy(new StrategyFactoryImpl())
                .and()
    }
}