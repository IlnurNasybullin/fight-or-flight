package io.github.ilnurnasybullin.fight.or.strategy.logger;

import java.util.ResourceBundle;

class NoOpsLogger implements Logger {

    private final String name;

    public NoOpsLogger(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isLoggable(Level level) {
        return false;
    }

    @Override
    public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {}

    @Override
    public void log(Level level, ResourceBundle bundle, String format, Object... params) {}
}
