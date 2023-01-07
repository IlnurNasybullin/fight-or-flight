package io.github.ilnurnasybullin.fight.or.flight.cli.dto;

import java.time.Duration;

public class SettingDto {

    private long durationMs;
    private int tacts;

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public int getTacts() {
        return tacts;
    }

    public void setTacts(int tacts) {
        this.tacts = tacts;
    }

    public Duration duration() {
        return Duration.ofMillis(durationMs);
    }
}
