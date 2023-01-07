package io.github.ilnurnasybullin.fight.or.flight.cli;

import io.github.ilnurnasybullin.fight.or.flight.cli.dto.GameDto;
import io.github.ilnurnasybullin.fight.or.flight.cli.logger.BattleLogging;
import io.github.ilnurnasybullin.fight.or.flight.core.game.Battle1on1;
import io.github.ilnurnasybullin.fight.or.flight.core.game.Game;
import io.github.ilnurnasybullin.fight.or.flight.core.player.Player;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Yaml yaml = new Yaml(new Constructor(GameDto.class));
        Path file = Path.of(args[0]);
        Game game;
        try(InputStream stream = Files.newInputStream(file, StandardOpenOption.READ)) {
            GameDto gameDto = yaml.load(stream);
            game = gameDto.game();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Player> players = game.players();
        Player attacker = players.get(0);
        Player defender = players.get(1);

        Battle1on1 battle1on1 = game.withAttacker(attacker)
                .andDefender(defender)
                .startBattle1on1();

        BattleLogging battleLogging = new BattleLogging(battle1on1);
        battleLogging.run();
    }
}