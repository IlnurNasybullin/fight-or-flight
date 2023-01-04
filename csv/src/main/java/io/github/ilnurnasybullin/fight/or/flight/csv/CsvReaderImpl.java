package io.github.ilnurnasybullin.fight.or.flight.csv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class CsvReaderImpl implements CsvReader, CsvReader.AndCharset, CsvReader.AndDelimiter, CsvReader.WithHeaders {

    private InputStream inputStream;
    private Charset charset;
    private String delimiter;
    private boolean withHeaders;

    @Override
    public AndCharset withInputStream(InputStream stream) {
        inputStream = stream;
        return this;
    }

    @Override
    public AndDelimiter andCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    @Override
    public WithHeaders andDelimiter(String regex) {
        delimiter = regex;
        return this;
    }

    @Override
    public ReadCsv withHeaders() {
        withHeaders = true;
        return this;
    }

    @Override
    public Stream<Row> readCsv() throws Exception {
        try(InputStream stream = inputStream;
            InputStreamReader reader = new InputStreamReader(stream, charset);
            BufferedReader bufferedReader = new BufferedReader(reader);
            Stream<String> lines = bufferedReader.lines()) {
            return lines.map(new RowReader(!withHeaders, delimiter))
                    .flatMap(Optional::stream);
        }
    }

    private static class RowImpl implements Row {

        private final String[] values;
        private final Map<String, String> headers;

        private RowImpl(String[] values, Map<String, String> headers) {
            this.values = values;
            this.headers = headers;
        }

        @Override
        public Optional<String> value(int index) {
            if (index >= values.length) {
                return Optional.empty();
            }

            return Optional.of(values[index]);
        }

        @Override
        public Optional<String> value(String column) {
            String value = headers.get(column);
            return Optional.ofNullable(value);
        }
    }

    private static class RowReader implements Function<String, Optional<Row>> {

        private boolean headerIsRead;
        private final String delimiter;
        private Map<Integer, String> headers;

        public RowReader(boolean headerIsRead, String delimiter) {
            this.headerIsRead = headerIsRead;
            this.delimiter = delimiter;
            headers = Map.of();
        }

        @Override
        public Optional<Row> apply(String line) {
            if (headerIsRead) {
                return Optional.of(readRow(line));
            }

            headers = readHeaders(line);
            headerIsRead = true;
            return Optional.empty();
        }

        private Map<Integer, String> readHeaders(String line) {
            String[] headers = line.split(delimiter);
            Map<Integer, String> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(i, headers[i]);
            }

            return headerMap;
        }

        private Row readRow(String line) {
            String[] values = line.split(delimiter);
            Map<String, String> headers = new HashMap<>();
            this.headers.forEach((index, header) -> {
                headers.put(header, values[index]);
            });

            return new RowImpl(values, headers);
        }

    }
}
