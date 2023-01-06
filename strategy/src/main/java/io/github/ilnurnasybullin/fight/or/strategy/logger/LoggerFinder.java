package io.github.ilnurnasybullin.fight.or.strategy.logger;

public class LoggerFinder extends System.LoggerFinder {
    @Override
    public System.Logger getLogger(String name, Module module) {
        return Logger.getInstance();
    }
}
