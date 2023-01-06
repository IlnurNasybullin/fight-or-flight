package io.github.ilnurnasybullin.fight.or.flight.game;

import io.github.ilnurnasybullin.fight.or.flight.core.game.Battle1on1;
import io.github.ilnurnasybullin.fight.or.flight.core.party.Party;
import io.github.ilnurnasybullin.fight.or.flight.core.player.ActivePlayer;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Race;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.MoveStrategy;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.Strategy;

import java.util.Objects;

class ActivePlayerImpl implements ActivePlayer {

    private final String name;
    private final PartyImpl party;
    private final Race race;
    private Strategy strategy;

    public ActivePlayerImpl(String name, PartyImpl party, Race race) {
        this.name = name;
        this.party = party;
        this.race = race;
    }

    ActivePlayerImpl addUnitInParty(ActiveUnit unit) {
        PartyImpl newParty = party.addUnit(unit);
        ActivePlayerImpl newPlayer = new ActivePlayerImpl(name, newParty, race);
        newPlayer.setStrategy(strategy);
        unit.changeOwner(newPlayer);
        return newPlayer;
    }

    // immutable class
    ActivePlayerImpl copy() {
        return this;
    }

    @Override
    public MoveStrategy moveStrategy(Battle1on1.State battleState) {
        return strategy.onMove(battleState);
    }

    @Override
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Party party() {
        return party;
    }

    @Override
    public Race race() {
        return race;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivePlayerImpl that = (ActivePlayerImpl) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
