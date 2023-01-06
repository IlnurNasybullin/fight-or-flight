package io.github.ilnurnasybullin.fight.or.flight.game;

import io.github.ilnurnasybullin.fight.or.flight.core.game.Game;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Race;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.StrategyFactory;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.Unit;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitTemplate;
import io.github.ilnurnasybullin.fight.or.flight.unit.UnitRecord;
import io.github.ilnurnasybullin.fight.or.flight.unit.UnitRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameBuilder implements Game.Builder, Game.Builder.HasTacts, Game.Builder.UnitDamageTable,
        Game.Builder.PlayerWithName, Game.Builder.AndRace, Game.Builder.AndHasStrategy, Game.Builder.HasPartyWithUnit,
        Game.Builder.AndUnit {

    private TactsContext tactsContext;
    private io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitDamageTable unitDamageTable;
    private PlayerContext playerContext;
    private ActivePlayerImpl activePlayer;
    private StrategyFactory strategyFactory;
    private List<PlayerWithMetadata> playerWithMetadata;
    private List<ActiveUnit> units;

    @Override
    public HasTacts time(Duration time) {
        GameBuilder builder = copy();
        builder.tactsContext = TactsContext.time(tactsContext, time);

        return builder;
    }

    private GameBuilder copy() {
        GameBuilder copy = new GameBuilder();
        copy.tactsContext = tactsContext == null ? null : tactsContext.copy();
        copy.unitDamageTable = unitDamageTable;
        copy.playerContext = playerContext == null ? null : playerContext.copy();
        copy.activePlayer = activePlayer == null ? null : activePlayer.copy();
        copy.playerWithMetadata = playerWithMetadata == null ? List.of() : List.copyOf(playerWithMetadata);
        copy.strategyFactory = strategyFactory;
        copy.units = units == null ? List.of() : List.copyOf(units);

        return copy;
    }

    @Override
    public UnitDamageTable hasTacts(int count) {
        GameBuilder builder = copy();
        builder.tactsContext = TactsContext.tactsCount(tactsContext, count);

        return builder;
    }

    @Override
    public PlayerWithName unitDamageTable(io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitDamageTable unitDamageTable) {
        GameBuilder builder = copy();
        builder.unitDamageTable = unitDamageTable;

        return builder;
    }

    @Override
    public AndRace playerWithName(String name) {
        GameBuilder builder = copy();
        builder.playerContext = PlayerContext.name(playerContext, name);
        return builder;
    }

    @Override
    public AndHasStrategy andRace(Race race) {
        GameBuilder builder = copy();
        builder.playerContext = PlayerContext.race(playerContext, race);
        builder.activePlayer = playerContext.player();

        return builder;
    }

    @Override
    public HasPartyWithUnit andHasStrategy(StrategyFactory strategyFactory) {
        GameBuilder builder = copy();
        builder.strategyFactory = strategyFactory;
        return builder;
    }

    @Override
    public AndUnit hasPartyWithUnit(UnitTemplate unitTemplate) {
        GameBuilder builder = copy();
        ActiveUnit unit = createUnit(unitTemplate);
        builder.activePlayer = builder.activePlayer.addUnitInParty(unit);
        builder.units = new ArrayList<>(units);
        builder.units.add(unit);

        return builder;
    }

    private ActiveUnit createUnit(UnitTemplate unitTemplate) {
        UnitRecord unitRecord = UnitRepository.getInstance().findByType(unitTemplate.type())
                .orElseThrow();
        return new ActiveUnit(unitRecord, unitTemplate.name(), null, tactsForRecharging(unitRecord));
    }

    private int tactsForRecharging(UnitRecord unitRecord) {
        Duration cooldown = unitRecord.cooldown();
        return tactsContext.tactsForRecharging(cooldown);
    }

    @Override
    public AndUnit andUnit(UnitTemplate unitTemplate) {
        return hasPartyWithUnit(unitTemplate);
    }

    @Override
    public PlayerWithName and() {
        GameBuilder builder = copy();
        addPlayerAndClear(builder);
        return builder;
    }

    private void addPlayerAndClear(GameBuilder builder) {
        builder.playerWithMetadata = new ArrayList<>(builder.playerWithMetadata);
        builder.playerWithMetadata.add(new PlayerWithMetadata(activePlayer, strategyFactory));
        builder.activePlayer = null;
        builder.playerContext = null;
        builder.strategyFactory = null;
    }

    @Override
    public Game buildGame() {
        GameBuilder builder = copy();
        addPlayerAndClear(builder);
        Map<Player, PlayerWithMetadata> players = builder.playerWithMetadata
                .stream()
                .collect(Collectors.toUnmodifiableMap(PlayerWithMetadata::player, Function.identity()));

        Map<Unit, ActiveUnit> units = builder.units
                .stream()
                .collect(Collectors.toUnmodifiableMap(Function.identity(), Function.identity()));

        return new GameImpl(players, units, unitDamageTable);
    }

    private record TactsContext(Duration time, int tactsCount) {

        public static TactsContext time(TactsContext tactsContext, Duration time) {
            if (tactsContext == null) {
                return new TactsContext(time, 1);
            }

            return new TactsContext(time, tactsContext.tactsCount);
        }

        public static TactsContext tactsCount(TactsContext tactsContext, int tactsCount) {
            if (tactsContext == null) {
                return new TactsContext(Duration.ZERO, tactsCount);
            }

            return new TactsContext(tactsContext.time, tactsCount);
        }

        // immutable
        public TactsContext copy() {
            return this;
        }

        public int tactsForRecharging(Duration cooldown) {
            return (int) (cooldown.toMillis() * tactsCount / time.toMillis());
        }
    }

    private record PlayerContext(String name, Race race) {

        public static PlayerContext name(PlayerContext playerContext, String name) {
            if (playerContext == null) {
                return new PlayerContext(name, null);
            }

            return new PlayerContext(name, playerContext.race());
        }

        public static PlayerContext race(PlayerContext playerContext, Race race) {
            if (playerContext == null) {
                return new PlayerContext(null, race);
            }

            return new PlayerContext(playerContext.name(), race);
        }

        // immutable
        public PlayerContext copy() {
            return this;
        }

        public ActivePlayerImpl player() {
            return new ActivePlayerImpl(name, new PartyImpl(new ArrayList<>()), race);
        }

    }

}
