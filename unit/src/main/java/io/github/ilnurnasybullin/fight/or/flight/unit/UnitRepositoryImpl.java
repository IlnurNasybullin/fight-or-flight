package io.github.ilnurnasybullin.fight.or.flight.unit;

import io.github.ilnurnasybullin.fight.or.flight.core.unit.ArmorType;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.AttackType;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitType;
import io.github.ilnurnasybullin.fight.or.flight.unit.csv.CsvReader;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UnitRepositoryImpl implements UnitRepository {

    private static final UnitRepository SINGLETON = new UnitRepositoryImpl();

    private static final String filename = "./units.csv";

    /**
     * Headers
     */

    private static final String HEADER_TYPE = "type";
    private static final String HEADER_FORCE = "force";
    private static final String HEADER_HP = "hp";
    private static final String HEADER_ARMOR_VALUE = "armor_value";
    private static final String HEADER_ARMOR_TYPE = "armor_type";
    private static final String HEADER_ATTACK_MIN_VALUE = "attack_min_value";
    private static final String HEADER_ATTACK_TYPE = "attack_type";
    private static final String HEADER_COOLDOWN = "cooldown";
    private static final String HEADER_TARGET_FORCES = "target_forces";
    private static final String HEADER_WEAKNESS_TO_MAGIC = "weakness_to_magic";
    private static final String HEADER_GOLD = "gold";
    private static final String HEADER_BUILD_TIME = "build_time";

    private final Map<UnitType, UnitRecord> units;

    private UnitRepositoryImpl() {
        units = readUnits(filename);
    }

    private Map<UnitType, UnitRecord> readUnits(String filename) {
        try {
            return CsvReader.getInstance()
                    .withInputStream(getClass().getResourceAsStream(filename))
                    .andCharset(StandardCharsets.UTF_8)
                    .andDelimiter(",")
                    .withHeaders()
                    .readCsv()
                    .map(new UnitRecordCreator())
                    .collect(Collectors.toUnmodifiableMap(UnitRecord::type, Function.identity()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<UnitRecord> units() {
        return units.values();
    }

    @Override
    public Optional<UnitRecord> findByType(UnitType unitType) {
        return Optional.ofNullable(units.get(unitType));
    }

    public static UnitRepository provider() {
        return SINGLETON;
    }

    private static class UnitRecordCreator implements Function<CsvReader.Row, UnitRecord> {

        private final Pattern PATTERN_TARGET_FORCES = Pattern.compile("^\\[(.+)]$");

        @Override
        public UnitRecord apply(CsvReader.Row row) {
            UnitType type = readType(row);
            Force force = readForce(row);
            int hp = readHp(row);
            int armorValue = readArmorValue(row);
            ArmorType armorType = readArmorType(row);
            int attackMinValue = readAttackMinValue(row);
            AttackType attackType = readAttackType(row);
            Duration cooldown = readCooldown(row);
            List<Force> targetForces = List.copyOf(readTargetForces(row));
            boolean weaknessToMagic = readWeaknessToMagic(row);
            int gold = readGold(row);
            Duration buildTime = readBuildTime(row);

            return new UnitRecord(
                    type, force, hp, armorValue, armorType, attackMinValue, attackType, cooldown, targetForces,
                    weaknessToMagic, gold, buildTime
            );
        }

        private Duration readBuildTime(CsvReader.Row row) {
            String buildTime = row.value(HEADER_BUILD_TIME).orElseThrow();
            long ms = Long.parseLong(buildTime);
            return Duration.ofMillis(ms);
        }

        private int readGold(CsvReader.Row row) {
            String gold = row.value(HEADER_GOLD).orElseThrow();
            return Integer.parseInt(gold);
        }

        private boolean readWeaknessToMagic(CsvReader.Row row) {
            String weaknessToMagic = row.value(HEADER_WEAKNESS_TO_MAGIC).orElseThrow();
            return Boolean.parseBoolean(weaknessToMagic);
        }

        private List<Force> readTargetForces(CsvReader.Row row) {
            String targetForces = row.value(HEADER_TARGET_FORCES).orElseThrow().toUpperCase(Locale.ENGLISH);
            String[] forces;
            Matcher matcher = this.PATTERN_TARGET_FORCES.matcher(targetForces);
            if (matcher.find()) {
                forces = matcher.group(1).split(";");
            } else {
                forces = new String[]{targetForces};
            }

            return Arrays.stream(forces)
                    .map(Force::valueOf)
                    .toList();
        }

        private Duration readCooldown(CsvReader.Row row) {
            String cooldown = row.value(HEADER_COOLDOWN).orElseThrow();
            long ms = Long.parseLong(cooldown);
            return Duration.ofMillis(ms);
        }

        private AttackType readAttackType(CsvReader.Row row) {
            String attackType = row.value(HEADER_ATTACK_TYPE).orElseThrow().toUpperCase(Locale.ENGLISH);
            return AttackType.valueOf(attackType);
        }

        private int readAttackMinValue(CsvReader.Row row) {
            String attackMinValue = row.value(HEADER_ATTACK_MIN_VALUE).orElseThrow();
            return Integer.parseInt(attackMinValue);
        }

        private ArmorType readArmorType(CsvReader.Row row) {
            String armorType = row.value(HEADER_ARMOR_TYPE).orElseThrow().toUpperCase(Locale.ENGLISH);
            return ArmorType.valueOf(armorType);
        }

        private int readArmorValue(CsvReader.Row row) {
            String armorValue = row.value(HEADER_ARMOR_VALUE).orElseThrow();
            return Integer.parseInt(armorValue);
        }

        private int readHp(CsvReader.Row row) {
            String hp = row.value(HEADER_HP).orElseThrow();
            return Integer.parseInt(hp);
        }

        private Force readForce(CsvReader.Row row) {
            String force = row.value(HEADER_FORCE).orElseThrow().toUpperCase(Locale.ENGLISH);
            return Force.valueOf(force);
        }

        private UnitType readType(CsvReader.Row row) {
            String type = row.value(HEADER_TYPE).orElseThrow().toUpperCase(Locale.ENGLISH);
            return UnitType.valueOf(type);
        }
    }
}
