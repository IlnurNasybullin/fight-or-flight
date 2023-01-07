package io.github.ilnurnasybullin.fight.or.flight.cli.dto;

import io.github.ilnurnasybullin.fight.or.flight.core.game.Game;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitDamageTable;
import io.github.ilnurnasybullin.fight.or.strategy.StrategyFactoryImpl;

import java.util.Iterator;
import java.util.List;

public class GameDto {

    private SettingDto setting;
    private List<PlayerDto> players;

    public SettingDto getSetting() {
        return setting;
    }

    public void setSetting(SettingDto setting) {
        this.setting = setting;
    }

    public List<PlayerDto> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerDto> players) {
        this.players = players;
    }

    public Game game() {
        var builder = Game.Builder.getInstance()
                .time(setting.duration())
                .hasTacts(setting.getTacts())
                .unitDamageTable(UnitDamageTable.getInstance());
        return addPlayers(builder, players);
    }

    private Game addPlayers(Game.Builder.PlayerWithName builder, List<PlayerDto> players) {
        Iterator<PlayerDto> iterator = players.iterator();
        PlayerDto playerDto = iterator.next();

        var playerBuilder = addPlayer(builder, playerDto);
        while (iterator.hasNext()) {
            playerBuilder = addPlayer(playerBuilder.and(), iterator.next());
        }

        return playerBuilder.buildGame();
    }

    private Game.Builder.AndUnit addPlayer(Game.Builder.PlayerWithName builder, PlayerDto playerDto) {
        var unitBuilder = builder.playerWithName(playerDto.getName())
                .andRace(playerDto.race())
                .andHasStrategy(new StrategyFactoryImpl());

        return addUnits(unitBuilder, playerDto.getUnits());
    }

    private Game.Builder.AndUnit addUnits(Game.Builder.HasPartyWithUnit unitBuilder, List<UnitDto> units) {
        Iterator<UnitDto> iterator = units.iterator();
        UnitDto unitDto = iterator.next();
        var builder = unitBuilder.hasPartyWithUnit(unitDto.template());

        while (iterator.hasNext()) {
            builder = builder.andUnit(iterator.next().template());
        }

        return builder;
    }
}
