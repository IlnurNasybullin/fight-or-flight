package io.github.ilnurnasybullin.fight.or.flight.game;

import io.github.ilnurnasybullin.fight.or.flight.core.game.Battle1on1;
import io.github.ilnurnasybullin.fight.or.flight.core.player.ActivePlayer;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.MoveStrategy;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.MoveType;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.Strategy;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.Unit;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitDamageTable;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitState;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Battle1on1Impl implements Battle1on1, Battle1on1.State {

    private final PlayerWithMetadata attacker;
    private final PlayerWithMetadata defender;
    private final UnitDamageTable unitDamageTable;
    private final Map<Unit, ActiveUnit> units;

    private Consumer<Battle1on1.PlayerMove> playerMoveHandler = playerMove -> {};
    private Consumer<AttackResult> attackResultHandler = attackResult -> {};

    public Battle1on1Impl(PlayerWithMetadata attacker, PlayerWithMetadata defender, UnitDamageTable unitDamageTable,
                          Map<Unit, ActiveUnit> units) {
        this.attacker = attacker;
        this.defender = defender;
        this.unitDamageTable = unitDamageTable;
        this.units = units;
    }

    @Override
    public Player opponent(Player player) {
        if (player == attacker.player()) {
            return defender.player();
        }

        if (player == defender.player()) {
            return attacker.player();
        }

        throw new IllegalStateException(String.format("Player %s does not participate in battle", player));
    }

    @Override
    public UnitState unitState(Unit unit) {
        return activeUnit(unit).state();
    }

    @Override
    public State state() {
        return this;
    }

    @Override
    public Battle1on1 setPlayerMoveHandler(Consumer<Battle1on1.PlayerMove> playerMoveHandler) {
        this.playerMoveHandler = playerMoveHandler;
        return this;
    }

    @Override
    public Battle1on1 setAttackResultHandler(Consumer<AttackResult> attackResultHandler) {
        this.attackResultHandler = attackResultHandler;
        return this;
    }

    @Override
    public void run() {
        initStrategiesForPlayers();
        Iterator<RoundingValue<ActivePlayer>> playersIterator =
                new RoundingIterator<>(new ActivePlayer[] {attacker.player(), defender.player()});

        while (true) {
            RoundingValue<ActivePlayer> roundingValue = playersIterator.next();

            ActivePlayer player = roundingValue.element();
            MoveStrategy strategy = player.moveStrategy(state());
            playerMoveHandler.accept(new PlayerMove(player, strategy, roundingValue.round()));

            if (strategy.type() == MoveType.DEFEATED || strategy.type() == MoveType.RETREAT) {
                break;
            }

            applyStrategy(player, strategy);
        }
    }

    private void applyStrategy(Player player, MoveStrategy strategy) {
        for(Unit unit: player.party().units()) {
            Optional<Unit> target = strategy.unit(unit)
                    .hasTarget();

            if (target.isEmpty()) {
                continue;
            }

            ActiveUnit attacker = activeUnit(unit);
            ActiveUnit defender = activeUnit(target.get());

            AttackResult attackResult = attacker.attack(defender, unitDamageTable);
            attackResultHandler.accept(attackResult);
        }

        for (Unit unit: player.party().units()) {
            ActiveUnit activeUnit = activeUnit(unit);
            activeUnit.nextState();
        }
    }

    private ActiveUnit activeUnit(Unit unit) {
        ActiveUnit activeUnit = units.get(unit);
        if (activeUnit == null) {
            throw new IllegalStateException(String.format("Unit %s is not created by game!", unit));
        }

        return activeUnit;
    }

    private void initStrategiesForPlayers() {
        Strategy attackerStrategy = attacker.strategyFactory()
                .forAttacker(attacker.player())
                .andDefender(defender.player())
                .withUnitDamageTable(unitDamageTable)
                .buildStrategyForAttacker();

        Strategy defenderStrategy = defender.strategyFactory()
                .forAttacker(attacker.player())
                .andDefender(defender.player())
                .withUnitDamageTable(unitDamageTable)
                .buildStrategyForDefender();

        attacker.player().setStrategy(attackerStrategy);
        defender.player().setStrategy(defenderStrategy);
    }

    private record RoundingValue<T>(T element, int round) {}

    private static class RoundingIterator<T> implements Iterator<RoundingValue<T>> {

        private final T[] elements;
        private final AtomicInteger round;
        private int index;

        private RoundingIterator(T[] elements) {
            this.elements = elements;
            round = new AtomicInteger(0);
            index = -1;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public RoundingValue<T> next() {
            index++;

            if (index >= elements.length) {
                index = 0;
                round.incrementAndGet();
            }

            return new RoundingValue<>(elements[index], round.get());
        }
    }

    private record PlayerMove(Player player, MoveStrategy move, int roundNumber) implements Battle1on1.PlayerMove {}
}
