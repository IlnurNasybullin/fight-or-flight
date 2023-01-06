package io.github.ilnurnasybullin.fight.or.flight.cli.logger;

import io.github.ilnurnasybullin.fight.or.flight.core.game.Battle1on1;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.MoveType;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.Unit;

public class BattleLogging implements Runnable {

    private final Battle1on1 battle;

    public BattleLogging(Battle1on1 battle) {
        this.battle = battle;
    }

    @Override
    public void run() {
        battle.setAttackResultHandler(this::printAttackResult);
        battle.setPlayerMoveHandler(this::printPlayerMove);
        battle.run();
    }

    private void printPlayerMove(Battle1on1.PlayerMove playerMove) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("---------------------");
        System.out.printf("Раунд №%d, игрок '%s' сделал ход:%n", playerMove.roundNumber(), playerMove.player().name());
        System.out.printf("Тип хода - %s%n", moveType(playerMove));
        if (playerMove.move().type() != MoveType.ATTACK) {
            System.out.println();
            return;
        }

        for (Unit unit: playerMove.player().party().units()) {
            playerMove.move().unit(unit)
                    .hasTarget()
                    .ifPresent(target -> System.out.printf("Юнит '%s' атакует юнита '%s'%n", unit.name(), target.name()));
        }
        System.out.println();
    }

    private String moveType(Battle1on1.PlayerMove playerMove) {
        MoveType moveType = playerMove.move().type();
        return switch (moveType) {
            case ATTACK -> "атака";
            case DEFEATED -> "поражение (нет игроков, способных вести атаку)";
            case RETREAT -> "отступление";
        };
    }

    private void printAttackResult(Battle1on1.AttackResult attackResult) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        switch (attackResult.status()) {
            case SUCCESS -> {
                System.out.printf(
                        "Юнит '%s' игрока '%s' атаковал юнита '%s' игрока '%s' - ",
                        attackResult.attacker().name(),
                        attackResult.attacker().owner().name(),
                        attackResult.defender().name(),
                        attackResult.defender().owner().name()
                );
                System.out.printf(
                        "нанёс %d единиц урона (у юнита осталось %d единиц здоровья)%n",
                        attackResult.damage(),
                        attackResult.defender().ehp()
                );
            }
            case CANCELLED_CAUSE_DEFENDER_DEAD -> {
                System.out.printf(
                        "Атака юнита '%s' игрока '%s' на юнита '%s' игрока '%s' отменена - юнит '%s' уже убит%n",
                        attackResult.attacker().name(),
                        attackResult.attacker().owner().name(),
                        attackResult.defender().name(),
                        attackResult.defender().owner().name(),
                        attackResult.defender().name()
                );
            }
            case CANCELLED_CAUSE_RECHARGING -> {
                System.out.printf(
                        "Атака юнита '%s' игрока '%s' на юнита '%s' игрока '%s' невозможна - юнит '%s' перезаряжается%n",
                        attackResult.attacker().name(),
                        attackResult.attacker().owner().name(),
                        attackResult.defender().name(),
                        attackResult.defender().owner().name(),
                        attackResult.attacker().name()
                );
            }
            case FAILED_CAUSE_ATTACKER_DEAD -> {
                System.out.printf(
                        "Атака юнита '%s' игрока '%s' на юнита '%s' игрока '%s' невозможна - юнит '%s' убит%n",
                        attackResult.attacker().name(),
                        attackResult.attacker().owner().name(),
                        attackResult.defender().name(),
                        attackResult.defender().owner().name(),
                        attackResult.attacker().name()
                );
            }
        }
    }
}
