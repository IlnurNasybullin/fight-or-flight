import io.github.ilnurnasybullin.fight.or.flight.cli.logger.SimpleLoggerFinder;

module io.github.ilnurnasybullin.fight.or.flight.cli {
    requires io.github.ilnurnasybullin.fight.or.flight.core;
    requires io.github.ilnurnasybullin.fight.or.flight.unit;
    requires io.github.ilnurnasybullin.fight.or.flight.strategy;
    requires io.github.ilnurnasybullin.fight.or.flight.game;

    exports io.github.ilnurnasybullin.fight.or.flight.cli;
    exports io.github.ilnurnasybullin.fight.or.flight.cli.logger;

    provides System.LoggerFinder with SimpleLoggerFinder;
}