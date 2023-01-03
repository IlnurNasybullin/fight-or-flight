import io.github.ilnurnasybullin.fight.or.flight.core.game.Game;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitDamageTable;

module io.github.ilnurnasybullin.fight.or.flight.core {
    exports io.github.ilnurnasybullin.fight.or.flight.core.game;
    exports io.github.ilnurnasybullin.fight.or.flight.core.party;
    exports io.github.ilnurnasybullin.fight.or.flight.core.player;
    exports io.github.ilnurnasybullin.fight.or.flight.core.strategy;
    exports io.github.ilnurnasybullin.fight.or.flight.core.unit;

    uses Game.Builder;
    uses UnitDamageTable;
}