package io.github.ilnurnasybullin.fight.or.flight.core.player;

import io.github.ilnurnasybullin.fight.or.flight.core.party.Party;

public interface Player {
    String name();
    Party party();
    Race race();
}
