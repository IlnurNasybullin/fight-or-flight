package io.github.ilnurnasybullin.fight.or.strategy;

import io.github.ilnurnasybullin.fight.or.flight.core.strategy.MoveStrategy;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.MoveType;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.Unit;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MoveStrategyImpl implements MoveStrategy {

    private final Map<Unit, Unit> unitTargetTable;
    private final MoveType type;

    public final static MoveStrategy RETREAT = new MoveStrategyImpl(Map.of(), MoveType.RETREAT);
    public final static MoveStrategy DEFEATED = new MoveStrategyImpl(Map.of(), MoveType.DEFEATED);

    public MoveStrategyImpl(Map<Unit, Unit> unitTargetTable) {
        this(unitTargetTable, MoveType.ATTACK);
    }

    private MoveStrategyImpl(Map<Unit, Unit> unitTargetTable, MoveType type) {
        this.unitTargetTable = unitTargetTable;
        this.type = type;
    }

    @Override
    public MoveType type() {
        return type;
    }

    @Override
    public HasTarget unit(Unit unit) {
        return new Context(this, unit);
    }

    private Optional<Unit> unitHasTarget(Unit unit) {
        return Optional.ofNullable(unitTargetTable.get(unit));
    }

    private static class Context implements HasTarget {

        private final MoveStrategyImpl owner;
        private final Unit unit;

        private Context(MoveStrategyImpl owner, Unit unit) {
            this.owner = owner;
            this.unit = unit;
        }

        @Override
        public Optional<Unit> hasTarget() {
            return owner.unitHasTarget(unit);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MoveStrategyImpl other = (MoveStrategyImpl) obj;
        return type.equals(other.type) &&
                Objects.equals(unitTargetTable, other.unitTargetTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, unitTargetTable);
    }
}
