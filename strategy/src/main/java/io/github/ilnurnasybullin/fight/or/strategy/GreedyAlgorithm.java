package io.github.ilnurnasybullin.fight.or.strategy;

import io.github.ilnurnasybullin.fight.or.flight.core.game.Battle1on1;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.MoveStrategy;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.MoveType;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.Strategy;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.Unit;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitDamageTable;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitState;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

class GreedyAlgorithm implements Strategy {

    private final Player player;
    private final Player opponent;
    private final UnitDamageTable unitDamageTable;
    private final Map<Unit, List<Unit>> playerUnitsMostEfficientTargets;
    private final Map<Unit, List<Unit>> opponentUnitsMostEfficientTargets;
    private final ObjectiveFunctionCalculator objectiveFunctionCalculator;
    private final MoveTypeIdentifier moveTypeIdentifier;
    private final System.Logger logger;

    public GreedyAlgorithm(Player player, Player opponent, UnitDamageTable unitDamageTable,
                           ObjectiveFunctionCalculator objectiveFunctionCalculator,
                           MoveTypeIdentifier moveTypeIdentifier, System.Logger logger) {
        this.player = player;
        this.opponent = opponent;
        this.unitDamageTable = unitDamageTable;
        this.moveTypeIdentifier = moveTypeIdentifier;
        this.objectiveFunctionCalculator = objectiveFunctionCalculator;
        this.logger = logger;
        playerUnitsMostEfficientTargets = createMostEfficientTargets(player, opponent, unitDamageTable);
        opponentUnitsMostEfficientTargets = createMostEfficientTargets(opponent, player, unitDamageTable);
    }

    private Map<Unit, List<Unit>> createMostEfficientTargets(Player player, Player opponent,
                                                             UnitDamageTable unitDamageTable) {
        return player.party()
                .units()
                .stream()
                .map(unit -> createMostEfficientTargets(unit, opponent, unitDamageTable))
                .collect(Collectors.toUnmodifiableMap(
                        UnitWithMostEfficientTargets::unit, UnitWithMostEfficientTargets::targets
                ));
    }

    private UnitWithMostEfficientTargets createMostEfficientTargets(Unit unit, Player opponent,
                                                                    UnitDamageTable unitDamageTable) {
        var targetUnits = opponent.party()
                .units()
                .stream()
                .map(opponentUnit -> new UnitWithDamage(
                        opponentUnit, unitDamageTable.unit(unit).thatAttack(opponentUnit).hasDamage()
                ))
                .sorted(Comparator.comparingInt(UnitWithDamage::damage).reversed())
                .map(UnitWithDamage::targetUnit)
                .toList();

        return new UnitWithMostEfficientTargets(unit, targetUnits);
    }

    private record UnitWithMostEfficientTargets(Unit unit, List<Unit> targets) {}

    private record UnitWithDamage(Unit targetUnit, int damage) {}

    @Override
    public MoveStrategy onMove(Battle1on1.State battleState) {
        Player battleOpponent = battleState.opponent(player);
        if (battleOpponent != opponent) {
            throw new IllegalStateException(String.format("This strategy is not for this opponent %s!", battleOpponent));
        }

        if (allUnitsAreDead(player, battleState)) {
            return MoveStrategyImpl.DEFEATED;
        }

        List<MoveStrategy> someOnMoveStrategies = createSomeOnMoveStrategies(playerUnitsMostEfficientTargets, battleState);
        if (someOnMoveStrategies.isEmpty()) {
            return new MoveStrategyImpl(Map.of());
        }

        MoveStrategyEstimation moveStrategyEstimation = someOnMoveStrategies.stream()
                .map(strategy -> estimateForPlayer(player, strategy, battleState))
                .max(Comparator.comparingDouble(MoveStrategyEstimation::estimation))
                .orElseThrow();

        double estimation = moveStrategyEstimation.estimation();
        logger.log(System.Logger.Level.INFO, "Оценка целевой функции игрока {0}: {1}", player.name(), estimation);
        MoveType moveType = moveTypeIdentifier.objectiveFunction(estimation);
        if (moveType != MoveType.ATTACK) {
            return MoveStrategyImpl.RETREAT;
        }

        return moveStrategyEstimation.strategy();
    }

    private List<MoveStrategy> createSomeOnMoveStrategies(Map<Unit, List<Unit>> playerUnitsMostEfficientTargets,
                                                          Battle1on1.State battleState) {
        Optional<MoveStrategy> strategy = new MostEfficientTargetsStrategyChoicer(unitDamageTable)
                .strategy(playerUnitsMostEfficientTargets, battleState);

        if (strategy.isEmpty()) {
            return List.of();
        }

        MoveStrategy baseStrategy = strategy.get();

        var strategies = playerUnitsMostEfficientTargets.keySet()
                .stream()
                .filter(unit -> battleState.unitState(unit) == UnitState.READY_TO_ATTACK)
                .map(unit -> strategyWithShift(baseStrategy, unit, battleState, playerUnitsMostEfficientTargets))
                .flatMap(Optional::stream)
                .distinct()
                .collect(Collectors.toList());
        strategies.add(baseStrategy);
        return strategies;
    }

    private double objectionFunctionValue(Battle1on1.State state, Player player) {
        return objectiveFunctionCalculator.player(player)
                .withBattleState(state)
                .hasValue();
    }

    private Optional<MoveStrategy> strategyWithShift(MoveStrategy strategy, Unit unit, Battle1on1.State battleState,
                                                     Map<Unit, List<Unit>> playerUnitsMostEfficientTargets) {
        Optional<Unit> currentUnitOpponent = strategy.unit(unit)
                .hasTarget();

        if (currentUnitOpponent.isEmpty()) {
            return Optional.empty();
        }

        var targets = playerUnitsMostEfficientTargets.get(unit);
        if (targets == null) {
            throw new IllegalStateException(String.format("For unit %s is not found opponent units as targets!", unit));
        }

        Unit unitOpponent = currentUnitOpponent.get();
        var iterator = targets.iterator();

        Unit lastTarget = null;
        Unit newTarget = null;
        while (iterator.hasNext()) {
            Unit target = iterator.next();
            if (lastTarget == unitOpponent) {
                newTarget = target;
            }
            lastTarget = target;
        }

        if (newTarget == null) {
            return Optional.empty();
        }

        return new MostEfficientTargetsStrategyChoicer(unitDamageTable, Map.of(unit, newTarget))
                .strategy(playerUnitsMostEfficientTargets, battleState);
    }

    private boolean allUnitsAreDead(Player player, Battle1on1.State battleState) {
        return player.party()
                .units()
                .stream()
                .map(battleState::unitState)
                .allMatch(unitState -> unitState == UnitState.DEAD);
    }

    private MoveStrategyEstimation estimateForPlayer(Player player, MoveStrategy strategy, Battle1on1.State battleState) {
        Battle1on1.State newState = applyStrategy(battleState, strategy, player, unitDamageTable);
        List<MoveStrategy> opponentStrategies = createSomeOnMoveStrategies(opponentUnitsMostEfficientTargets, newState);
        if (opponentStrategies.isEmpty()) {
            return new MoveStrategyEstimation(strategy, objectionFunctionValue(newState, player));
        }

        Player opponent = battleState.opponent(player);

        MoveStrategy opponentSrtg = opponentStrategies.stream()
                .map(opponentStrategy -> estimateForOpponent(opponent, opponentStrategy, newState))
                .max(Comparator.comparingDouble(MoveStrategyEstimation::estimation))
                .map(MoveStrategyEstimation::strategy)
                .orElseThrow();

        Battle1on1.State newState1 = applyStrategy(newState, opponentSrtg, opponent, unitDamageTable);
        double estimation = objectionFunctionValue(newState1, player);
        return new MoveStrategyEstimation(strategy, estimation);
    }

    private Battle1on1.State applyStrategy(Battle1on1.State battleState, MoveStrategy strategy, Player player,
                                           UnitDamageTable unitDamageTable) {
        Map<Unit, Integer> damages = calculateDamages(player, strategy, unitDamageTable);
        return new AdaptedBattleState(battleState, damages);
    }

    private Map<Unit, Integer> calculateDamages(Player player, MoveStrategy strategy, UnitDamageTable unitDamageTable) {
        Map<Unit, Unit> targets = new HashMap<>();
        for (var unit: player.party().units()) {
            Optional<Unit> target = strategy.unit(unit).hasTarget();
            target.ifPresent(t -> targets.put(unit, t));
        }

        return calculateDamages(targets, unitDamageTable);
    }

    private static Map<Unit, Integer> calculateDamages(Map<Unit, Unit> targets, UnitDamageTable unitDamageTable) {
        return targets.entrySet()
                .stream()
                .map(entry -> new UnitWithDamage(
                        entry.getValue(), unitDamageTable.unit(entry.getKey())
                        .thatAttack(entry.getValue())
                        .hasDamage()
                ))
                .collect(Collectors.toMap(UnitWithDamage::targetUnit, UnitWithDamage::damage, Integer::sum));
    }

    private MoveStrategyEstimation estimateForOpponent(Player opponent, MoveStrategy strategy, Battle1on1.State battleState) {
        Battle1on1.State newState = applyStrategy(battleState, strategy, opponent, unitDamageTable);
        return new MoveStrategyEstimation(strategy, objectionFunctionValue(newState, opponent));
    }

    private record MoveStrategyEstimation(MoveStrategy strategy, double estimation) {}

    private static class MostEfficientTargetsStrategyChoicer {

        private final Map<Unit, Unit> targets;
        private final Map<Unit, Integer> damages;
        private final UnitDamageTable unitDamageTable;

        public MostEfficientTargetsStrategyChoicer(UnitDamageTable unitDamageTable) {
            this(unitDamageTable, Map.of());
        }

        public MostEfficientTargetsStrategyChoicer(UnitDamageTable unitDamageTable, Map<Unit, Unit> predefinedTargets) {
            this.targets = new HashMap<>(predefinedTargets);
            damages = calculateDamages(predefinedTargets, unitDamageTable);
            this.unitDamageTable = unitDamageTable;
        }

        public Optional<MoveStrategy> strategy(Map<Unit, List<Unit>> playerUnitsMostEfficientTargets, Battle1on1.State battleState) {
            Map<Unit, Integer> damages = new HashMap<>(this.damages);
            Map<Unit, Unit> targets = new HashMap<>(this.targets);
            var units = playerUnitsMostEfficientTargets.keySet()
                    .stream()
                    .filter(unit -> battleState.unitState(unit) == UnitState.READY_TO_ATTACK)
                    .filter(unit -> !targets.containsKey(unit))
                    .collect(Collectors.toList());

            Collections.shuffle(units, ThreadLocalRandom.current());

            for (var unit: units) {
                var opponentUnits = playerUnitsMostEfficientTargets.get(unit);
                if (opponentUnits == null) {
                    throw new IllegalStateException(String.format("For unit %s is not found opponent units as targets!", unit));
                }

                for (var opponentUnit: opponentUnits) {
                    if (isAlreadyDead(opponentUnit, battleState, damages)) {
                        continue;
                    }

                    targets.put(unit, opponentUnit);
                    int damage = unitDamageTable.unit(unit).thatAttack(opponentUnit).hasDamage();
                    damages.compute(opponentUnit, (u, oldDamage) -> {
                        if (oldDamage == null) {
                            return damage;
                        } else {
                            return oldDamage + damage;
                        }
                    });
                    break;
                }
            }

            if (targets.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(new MoveStrategyImpl(targets));
        }

        private boolean isAlreadyDead(Unit unit, Battle1on1.State battleState, Map<Unit, Integer> damages) {
            UnitState unitState = battleState.unitState(unit);
            if (unitState == UnitState.DEAD) {
                return true;
            }

            return damages.getOrDefault(unit, 0) >= unit.ehp();
        }
    }
}
