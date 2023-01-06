package io.github.ilnurnasybullin.fight.or.flight.game;

import io.github.ilnurnasybullin.fight.or.flight.core.game.Battle1on1;
import io.github.ilnurnasybullin.fight.or.flight.core.game.Game;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.Unit;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitDamageTable;

import java.util.List;
import java.util.Map;

class GameImpl implements Game {

    private final Map<Player, PlayerWithMetadata> players;
    private final Map<Unit, ActiveUnit> units;
    private final UnitDamageTable unitDamageTable;

    public GameImpl(Map<Player, PlayerWithMetadata> players, Map<Unit, ActiveUnit> units, UnitDamageTable unitDamageTable) {
        this.players = players;
        this.units = units;
        this.unitDamageTable = unitDamageTable;
    }

    @Override
    public List<Player> players() {
        return List.copyOf(players.keySet());
    }

    @Override
    public AndDefender withAttacker(Player attacker) {
        return new Context(this, playerWithMetadata(attacker));
    }

    private PlayerWithMetadata playerWithMetadata(Player player) {
        PlayerWithMetadata playerWithMetadata = players.get(player);
        if (playerWithMetadata == null) {
            throw new IllegalStateException(String.format("Player %s is not created by game!", player));
        }

        return playerWithMetadata;
    }

    private Battle1on1 startBattle1on1(PlayerWithMetadata attacker, PlayerWithMetadata defender) {
        return new Battle1on1Impl(attacker, defender, unitDamageTable, units);
    }

    private static class Context implements AndDefender, StartBattle1on1 {

        private final GameImpl owner;
        private final PlayerWithMetadata attacker;
        private PlayerWithMetadata defender;

        private Context(GameImpl owner, PlayerWithMetadata attacker) {
            this.owner = owner;
            this.attacker = attacker;
        }

        @Override
        public StartBattle1on1 andDefender(Player defender) {
            this.defender = owner.playerWithMetadata(defender);
            return this;
        }

        @Override
        public Battle1on1 startBattle1on1() {
            return owner.startBattle1on1(attacker, defender);
        }
    }
}
