package io.github.ilnurnasybullin.fight.or.flight.cli.logger;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ConsoleLogger implements System.Logger {

    enum Color {
        GREEN("\u001B[32m", "\u001B[0m"),
        NO("", "");

        private final String start;
        private final String end;

        Color(String start, String end) {
            this.start = start;
            this.end = end;
        }

        public String start() {
            return start;
        }

        public String end() {
            return end;
        }
    }

    private final String name;

    public ConsoleLogger(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isLoggable(Level level) {
        return true;
    }

    @Override
    public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
        Color color = color(level);
        System.out.printf("%s [%s]: %s%s - %s%s%n", name, level, color.start(), msg, thrown, color.end());
    }

    private Color color(Level level) {
        if (level == Level.INFO) {
            return Color.GREEN;
        }

        return Color.NO;
    }

    @Override
    public void log(Level level, ResourceBundle bundle, String format, Object... params) {
        Color color = color(level);
        System.out.printf("%s [%s]: %s%s%s%n", name, level, color.start(),
                MessageFormat.format(format, params), color.end());
    }
}
