package io.github.ilnurnasybullin.fight.or.flight.core.unit;

import java.time.Duration;

public interface Attack {
    int value();
    Duration cooldown();
}
