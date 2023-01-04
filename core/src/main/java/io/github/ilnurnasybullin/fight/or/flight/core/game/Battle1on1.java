package io.github.ilnurnasybullin.fight.or.flight.core.game;

import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.MoveStrategy;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.Unit;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitState;

import java.util.function.Consumer;

public interface Battle1on1 extends Runnable {
    Battle1on1.State state();
    Battle1on1 setPlayerMoveHandler(Consumer<PlayerMove> playerMoveHandler);
    Battle1on1 setAttackResultHandler(Consumer<AttackResult> attackResultHandler);

    interface State {
        Player opponent(Player player);
        UnitState unitState(Unit unit);
    }

    interface PlayerMove {
        Player player();
        MoveStrategy move();

        /**
         * started with 0
         */
        int roundNumber();
    }

    interface AttackResult {
        Unit attacker();
        Unit defender();
        int damage();
        Status status();

        enum Status {
            SUCCESS,
            CANCELLED_CAUSE_RECHARGING,
            CANCELLED_CAUSE_DEFENDER_DEAD,
            FAILED_CAUSE_ATTACKER_DEAD
        }
    }
}
