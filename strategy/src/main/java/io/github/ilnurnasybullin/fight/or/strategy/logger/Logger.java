package io.github.ilnurnasybullin.fight.or.strategy.logger;

import java.util.ServiceLoader;

public interface Logger extends System.Logger {

    static Logger getInstance() {
        return ServiceLoader.load(Logger.class)
                .findFirst()
                .orElseGet(() -> new NoOpsLogger("NoOpsLogger"));
    }

}
