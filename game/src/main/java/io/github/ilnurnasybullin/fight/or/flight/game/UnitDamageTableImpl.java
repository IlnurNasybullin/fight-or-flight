package io.github.ilnurnasybullin.fight.or.flight.game;

import io.github.ilnurnasybullin.fight.or.flight.core.unit.ArmorType;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.AttackType;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.Unit;
import io.github.ilnurnasybullin.fight.or.flight.core.unit.UnitDamageTable;
import io.github.ilnurnasybullin.fight.or.flight.csv.CsvReader;
import io.github.ilnurnasybullin.fight.or.flight.unit.UnitRecord;
import io.github.ilnurnasybullin.fight.or.flight.unit.UnitRepository;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnitDamageTableImpl implements UnitDamageTable {

    private static final UnitDamageTable SINGLETON = new UnitDamageTableImpl();

    private static final String HEADER_ATTACK_TYPE = "attack_type";

    private final Map<AttackType, Map<ArmorType, Double>> attackCoeffs;

    private UnitDamageTableImpl() {
        attackCoeffs = readCoeffs("unit_damage_table.csv");
    }

    private Map<AttackType, Map<ArmorType, Double>> readCoeffs(String filename) {
        try(InputStream inputStream = UnitDamageTable.class.getClassLoader().getResourceAsStream(filename);
            Stream<CsvReader.Row> lines = CsvReader.getInstance().withInputStream(inputStream)
                    .andCharset(StandardCharsets.UTF_8)
                    .andDelimiter("\s*\\|\s*")
                    .withHeaders()
                    .readCsv()) {
            return lines.map(new RowReader())
                    .collect(Collectors.toMap(
                            AttackTypeCoefficients::attackType, AttackTypeCoefficients::coefficients,
                            (val1, val2) -> val2, () -> new EnumMap<>(AttackType.class)
                    ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static UnitDamageTable provider() {
        return SINGLETON;
    }

    @Override
    public ThatAttack unit(Unit unit) {
        return new Context(this, unit);
    }

    private int damage(Unit attacker, Unit attacked) {
        UnitRecord attackerUnit = UnitRepository.getInstance()
                .findByType(attacker.type()).orElseThrow();

        UnitRecord attackedUnit = UnitRepository.getInstance()
                .findByType(attacked.type()).orElseThrow();

        AttackType attackType = attackerUnit.attackType();
        if (!attackedUnit.weaknessToMagic() && (
                attackType == AttackType.CHAOS || attackType == AttackType.MAGIC ||
                attackType == AttackType.SPELLS
        )) {
            return 0;
        }

        double coeff = attackCoeffs.get(attackType)
                .get(attackedUnit.armorType());

        return (int) (attacker.attack().value() * coeff);
    }

    private static class Context implements ThatAttack, HasDamage {

        private final UnitDamageTableImpl owner;
        private final Unit unit;
        private Unit attacked;

        private Context(UnitDamageTableImpl owner, Unit unit) {
            this.owner = owner;
            this.unit = unit;
        }

        @Override
        public HasDamage thatAttack(Unit unit) {
            attacked = unit;
            return this;
        }

        @Override
        public int hasDamage() {
            return owner.damage(unit, attacked);
        }
    }

    private record AttackTypeCoefficients(AttackType attackType, Map<ArmorType, Double> coefficients) {}

    private static class RowReader implements Function<CsvReader.Row, AttackTypeCoefficients> {

        @Override
        public AttackTypeCoefficients apply(CsvReader.Row row) {
            AttackType attackType = readAttackType(row);
            Map<ArmorType, Double> coefficients = new EnumMap<>(ArmorType.class);
            coefficients.put(ArmorType.LIGHT, readLightCoeff(row));
            coefficients.put(ArmorType.MEDIUM, readMediumCoeff(row));
            coefficients.put(ArmorType.HEAVY, readHeavyCoeff(row));
            coefficients.put(ArmorType.FORT, readFortCoeff(row));
            coefficients.put(ArmorType.HERO, readHeroCoeff(row));
            coefficients.put(ArmorType.UNARMORED, readUnarmoredCoeff(row));

            return new AttackTypeCoefficients(attackType, coefficients);
        }

        private double readUnarmoredCoeff(CsvReader.Row row) {
            return readDouble(row, ArmorType.UNARMORED);
        }

        private double readHeroCoeff(CsvReader.Row row) {
            return readDouble(row, ArmorType.HERO);
        }

        private double readFortCoeff(CsvReader.Row row) {
            return readDouble(row, ArmorType.FORT);
        }

        private double readHeavyCoeff(CsvReader.Row row) {
            return readDouble(row, ArmorType.HEAVY);
        }

        private double readMediumCoeff(CsvReader.Row row) {
            return readDouble(row, ArmorType.MEDIUM);
        }

        private double readLightCoeff(CsvReader.Row row) {
            return readDouble(row, ArmorType.LIGHT);
        }

        private double readDouble(CsvReader.Row row, ArmorType armorType) {
            return readDouble(row, armorType.name().toLowerCase(Locale.ENGLISH));
        }

        private double readDouble(CsvReader.Row row, String header) {
            String number = row.value(header).orElseThrow();
            return Double.parseDouble(number);
        }

        private AttackType readAttackType(CsvReader.Row row) {
            String attackType = row.value(HEADER_ATTACK_TYPE).orElseThrow().toUpperCase(Locale.ENGLISH);
            return AttackType.valueOf(attackType);
        }
    }
}
