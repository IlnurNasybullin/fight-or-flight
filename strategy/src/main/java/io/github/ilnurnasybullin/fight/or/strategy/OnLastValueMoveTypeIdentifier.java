package io.github.ilnurnasybullin.fight.or.strategy;

import io.github.ilnurnasybullin.fight.or.flight.core.strategy.MoveType;

class OnLastValueMoveTypeIdentifier implements MoveTypeIdentifier {

    private Double lastObjectiveFunction;

    @Override
    public MoveType objectiveFunction(double objectiveFunction) {
        if (lastObjectiveFunction == null) {
            lastObjectiveFunction = objectiveFunction;
            return MoveType.ATTACK;
        }

        if (lastObjectiveFunction / objectiveFunction > 1 && objectiveFunction < 1) {
            return MoveType.RETREAT;
        }

        lastObjectiveFunction = objectiveFunction;
        return MoveType.ATTACK;
    }
}
