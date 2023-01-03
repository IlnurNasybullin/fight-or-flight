package io.github.ilnurnasybullin.fight.or.flight.unit.csv;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public interface CsvReader {
    interface Row {
        /**
         * index started with 0
         */
        Optional<String> value(int index);
        Optional<String> value(String column);
    }

    AndCharset withInputStream(InputStream stream);

    interface AndCharset {
        AndDelimiter andCharset(Charset charset);
    }

    interface AndDelimiter {
        WithHeaders andDelimiter(String regex);
    }

    interface WithHeaders extends ReadCsv {
        ReadCsv withHeaders();
    }

    interface ReadCsv {
        Stream<Row> readCsv() throws Exception;
    }

    static CsvReader getInstance() {
        return ServiceLoader.load(CsvReader.class)
                .findFirst()
                .orElseThrow();
    }
}
