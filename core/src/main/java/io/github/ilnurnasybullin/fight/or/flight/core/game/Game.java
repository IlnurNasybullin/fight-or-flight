package io.github.ilnurnasybullin.fight.or.flight.core.game;

import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Race;
import io.github.ilnurnasybullin.fight.or.flight.core.strategy.StrategyFactory;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitTemplate;

import java.time.Duration;
import java.util.List;
import java.util.ServiceLoader;

public interface Game {
    List<Player> players();
    AndDefender withAttacker(Player attacker);

    interface StartBattle1on1 {
        Battle1on1 startBattle1on1();
    }

    interface AndDefender {
        StartBattle1on1 andDefender(Player defender);
    }

    interface Builder {
        HasTacts time(Duration time);

        interface HasTacts {
            UnitDamageTable hasTacts(int count);
        }

        interface UnitDamageTable {
            PlayerWithName unitDamageTable(io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitDamageTable unitDamageTable);
        }

        interface PlayerWithName {
            AndRace playerWithName(String name);
        }

        interface AndRace {
            HasPartyWithUnit andRace(Race race);
        }

        interface HasPartyWithUnit extends AndHasStrategy {
            AndUnit hasPartyWithUnit(UnitTemplate unitTemplate);
        }

        interface AndUnit extends AndHasStrategy {
            AndUnit andUnit(UnitTemplate unitTemplate);
        }

        interface AndHasStrategy {
            And andHasStrategy(StrategyFactory strategyFactory);
        }

        interface And extends BuildGame {
            PlayerWithName and();
        }

        interface BuildGame {
            Game buildGame();
        }

        static Builder getInstance() {
            return ServiceLoader.load(Builder.class)
                    .findFirst()
                    .orElseThrow();
        }
    }
}
