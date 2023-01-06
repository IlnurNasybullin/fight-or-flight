package io.github.ilnurnasybullin.fight.or.flight.game;

import io.github.ilnurnasybullin.fight.or.flight.core.game.Battle1on1;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.*;
import io.github.ilnurnasybullin.fight.or.flight.unit.UnitRecord;

import java.time.Duration;
import java.util.Objects;

public class ActiveUnit implements Unit {

    private final UnitRecord unitRecord;
    private final String name;
    private Player owner;
    private final Attack attack;
    private final Armor armor;
    private final UnitMoveState unitMoveState;
    private int hp;

    public ActiveUnit(UnitRecord unitRecord, String name, Player owner, int tactsForRecharging) {
        this.unitRecord = unitRecord;
        this.name = name;
        this.owner = owner;
        attack = new AttackImpl(unitRecord.attackMinValue(), unitRecord.cooldown());
        armor = new ArmorImpl(unitRecord.armorValue(), unitRecord.armorType());
        unitMoveState = new UnitMoveState(this, tactsForRecharging);
        hp = unitRecord.hp();
    }

    void changeOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    public int gold() {
        return unitRecord.gold();
    }

    @Override
    public Duration buildTime() {
        return unitRecord.buildTime();
    }

    @Override
    public int hp() {
        return hp;
    }

    @Override
    public int ehp() {
        return (int) armCoeff() * hp();
    }

    private double armCoeff() {
        return 1 + armor.value() * 0.06;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Attack attack() {
        return attack;
    }

    @Override
    public Armor armor() {
        return armor;
    }

    @Override
    public UnitType type() {
        return unitRecord.type();
    }

    @Override
    public Player owner() {
        return owner;
    }

    public UnitState state() {
        return unitMoveState.state();
    }

    public Battle1on1.AttackResult attack(ActiveUnit target, UnitDamageTable unitDamageTable) {
        Unit attacker = this;
        Unit defender = target;

        UnitState currentState = state();
        if (currentState == UnitState.DEAD) {
            return new AttackResult(attacker, defender, 0, Battle1on1.AttackResult.Status.FAILED_CAUSE_ATTACKER_DEAD);
        }

        if (currentState == UnitState.RECHARGING) {
            return new AttackResult(attacker, defender, 0, Battle1on1.AttackResult.Status.CANCELLED_CAUSE_RECHARGING);
        }

        if (target.state() == UnitState.DEAD) {
            return new AttackResult(attacker, defender, 0, Battle1on1.AttackResult.Status.CANCELLED_CAUSE_DEFENDER_DEAD);
        }

        int damage = unitDamageTable.unit(attacker)
                .thatAttack(defender)
                .hasDamage();

        int receivedDamage = target.receivedDamage(damage);
        return new AttackResult(attacker, defender, receivedDamage, Battle1on1.AttackResult.Status.SUCCESS);
    }

    private int receivedDamage(int damage) {
        int ehp = ehp();
        int dmg = Math.min(damage, ehp);
        hp = (int) ((ehp - dmg) / armCoeff());

        return dmg;
    }

    void nextState() {
        unitMoveState.incMove();
    }

    private record AttackImpl(int value, Duration cooldown) implements Attack { }

    private record ArmorImpl(int value, ArmorType type) implements Armor {}

    private static class CyclicIntValue {

        private final int minInclusive;
        private final int maxExclusive;
        private int currentValue;

        private CyclicIntValue(int minInclusive, int maxExclusive) {
            this.minInclusive = minInclusive;
            this.maxExclusive = maxExclusive;
            currentValue = minInclusive;
        }

        public int current() {
            return currentValue;
        }

        public void next() {
            currentValue++;
            if (currentValue >= maxExclusive) {
                currentValue = minInclusive;
            }
        }

    }

    private static class UnitMoveState {

        private final Unit unit;
        private final CyclicIntValue value;

        private UnitMoveState(Unit unit, int tactsForRecharging) {
            this.unit = unit;
            value = new CyclicIntValue(0, tactsForRecharging);
        }

        public UnitState state() {
            if (unit.hp() <= 0) {
                return UnitState.DEAD;
            }

            return value.current() == 0 ?
                    UnitState.READY_TO_ATTACK :
                    UnitState.RECHARGING;
        }

        public void incMove() {
            if (state() == UnitState.DEAD) {
                return;
            }

            value.next();
        }
    }

    private record AttackResult(Unit attacker, Unit defender, int damage, Status status) implements Battle1on1.AttackResult {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveUnit that = (ActiveUnit) o;
        return Objects.equals(name, that.name) && Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, owner);
    }
}
