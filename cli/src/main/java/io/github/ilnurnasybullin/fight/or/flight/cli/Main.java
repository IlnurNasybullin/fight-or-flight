package io.github.ilnurnasybullin.fight.or.flight.cli;

import io.github.ilnurnasybullin.fight.or.flight.cli.logger.BattleLogging;
import io.github.ilnurnasybullin.fight.or.flight.core.game.Battle1on1;
import io.github.ilnurnasybullin.fight.or.flight.core.game.Game;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Race;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitDamageTable;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitType;
import io.github.ilnurnasybullin.fight.or.flight.unit.UnitTemplateRecord;
import io.github.ilnurnasybullin.fight.or.strategy.StrategyFactoryImpl;

import java.time.Duration;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Game game = Game.Builder.getInstance()
                .time(Duration.ofMillis(500))
                .hasTacts(1)
                .unitDamageTable(UnitDamageTable.getInstance())
                .playerWithName("Игрок №1")
                .andRace(Race.HUMAN)
                .andHasStrategy(new StrategyFactoryImpl())
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
                .and()
                .playerWithName("Игрок №2")
                .andRace(Race.ORC)
                .andHasStrategy(new StrategyFactoryImpl())
                .hasPartyWithUnit(new UnitTemplateRecord("Бугай №1", UnitType.GRUNT))
                .andUnit(new UnitTemplateRecord("Бугай №2", UnitType.GRUNT))
                .andUnit(new UnitTemplateRecord("Бугай №3", UnitType.GRUNT))
                .andUnit(new UnitTemplateRecord("Охотник за головами №1", UnitType.TROLL_HEADHUNTER))
                .andUnit(new UnitTemplateRecord("Охотник за головами №2", UnitType.TROLL_HEADHUNTER))
                .andUnit(new UnitTemplateRecord("Охотник за головами №3", UnitType.TROLL_HEADHUNTER))
                .andUnit(new UnitTemplateRecord("Волчий всадник №1", UnitType.RAIDER))
                .andUnit(new UnitTemplateRecord("Волчий всадник №2", UnitType.RAIDER))
                .andUnit(new UnitTemplateRecord("Волчий всадник №3", UnitType.RAIDER))
                .andUnit(new UnitTemplateRecord("Волчий всадник №4", UnitType.RAIDER))
                .buildGame();

        List<Player> players = game.players();
        Player attacker = players.get(0);
        Player defender = players.get(1);

        Battle1on1 battle1on1 = game.withAttacker(attacker)
                .andDefender(defender)
                .startBattle1on1();

        BattleLogging battleLogging = new BattleLogging(battle1on1);
        battleLogging.run();
    }
}