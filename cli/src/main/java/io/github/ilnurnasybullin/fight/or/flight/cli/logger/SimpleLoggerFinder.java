package io.github.ilnurnasybullin.fight.or.flight.cli.logger;

import io.github.ilnurnasybullin.fight.or.strategy.logger.LoggerFinder;

public class SimpleLoggerFinder extends LoggerFinder {
    @Override
    public System.Logger getLogger(String name, Module module) {
        return new ConsoleLogger(name);
    }
}
