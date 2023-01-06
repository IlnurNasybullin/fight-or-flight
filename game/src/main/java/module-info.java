import io.github.ilnurnasybullin.fight.or.flight.core.game.Game;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitDamageTable;
import io.github.ilnurnasybullin.fight.or.flight.game.GameBuilder;
import io.github.ilnurnasybullin.fight.or.flight.game.UnitDamageTableImpl;

module io.github.ilnurnasybullin.fight.or.flight.game {
    requires io.github.ilnurnasybullin.fight.or.flight.core;
    requires io.github.ilnurnasybullin.fight.or.flight.unit;
    requires io.github.ilnurnasybullin.fight.or.flight.csv;

    exports io.github.ilnurnasybullin.fight.or.flight.game;

    provides UnitDamageTable with UnitDamageTableImpl;
    provides Game.Builder with GameBuilder;
}