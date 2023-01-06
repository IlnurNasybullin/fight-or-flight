package io.github.ilnurnasybullin.fight.or.flight.game;

import io.github.ilnurnasybullin.fight.or.flight.core.party.Party;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.Unit;

import java.util.ArrayList;
import java.util.List;

class PartyImpl implements Party {

    private final List<ActiveUnit> activeUnits;
    private final List<Unit> units;

    PartyImpl(List<ActiveUnit> activeUnits) {
        this.activeUnits = List.copyOf(activeUnits);
        this.units = activeUnits.stream()
                .map(unit -> (Unit) unit)
                .toList();
    }

    @Override
    public List<Unit> units() {
        return units;
    }

    PartyImpl addUnit(ActiveUnit unit) {
        List<ActiveUnit> activeUnits = new ArrayList<>(this.activeUnits);
        activeUnits.add(unit);
        return new PartyImpl(activeUnits);
    }
}
