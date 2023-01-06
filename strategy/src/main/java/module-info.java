import io.github.ilnurnasybullin.fight.or.strategy.logger.Logger;
import io.github.ilnurnasybullin.fight.or.strategy.logger.LoggerFinder;

module io.github.ilnurnasybullin.fight.or.flight.strategy {
    requires io.github.ilnurnasybullin.fight.or.flight.core;

    exports io.github.ilnurnasybullin.fight.or.strategy;
    exports io.github.ilnurnasybullin.fight.or.strategy.logger;

    uses Logger;
    provides System.LoggerFinder with LoggerFinder;
}