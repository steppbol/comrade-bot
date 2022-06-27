package com.balashenka.comrade.util;

import com.balashenka.comrade.model.Person;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Log4j2
@Component
public record CsvUtil() {
    private static final String DOT_SYMBOL_REGEX = "\\.";
    private static final String DOT_SYMBOL = ".";
    private static final String DATE_HEADER = "date";
    private static final String EMAIL_HEADER = "email";
    private static final String NAME_HEADER = "name";
    private static final String MODERATOR_HEADER = "moderator";

    private static final CSVFormat CSV_PARSER_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreHeaderCase(true)
            .setTrim(true)
            .build();

    private static final CSVFormat CSV_PRINTER_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader(DATE_HEADER, EMAIL_HEADER, NAME_HEADER, MODERATOR_HEADER)
            .setQuoteMode(QuoteMode.MINIMAL)
            .build();

    @NonNull
    public Set<Person> convertFrom(InputStream data) {
        log.info("Convert persons from CSV");

        Set<Person> persons = new LinkedHashSet<>();
        try (var parser = new CSVParser(new BufferedReader(new InputStreamReader(data, StandardCharsets.UTF_8)), CSV_PARSER_FORMAT)) {
            for (var record : parser.getRecords()) {
                var date = record.get(DATE_HEADER).split(DOT_SYMBOL_REGEX);

                var person = new Person();
                person.setDay(Integer.parseInt(date[0]));
                person.setMonth(Integer.parseInt(date[1]));
                person.setEmail(record.get(EMAIL_HEADER));
                person.setName(record.get(NAME_HEADER));
                person.setModerator(Boolean.parseBoolean(record.get(MODERATOR_HEADER)));

                persons.add(person);
            }
        } catch (IOException e) {
            log.info("Error during converting persons from CSV");
            throw new RuntimeException(e);
        }

        return persons;
    }

    @NonNull
    public InputStream convertTo(List<Person> persons) {
        log.info("Convert persons to CSV. Amount: {}", persons);

        var outputStream = new ByteArrayOutputStream();
        try (var printer = new CSVPrinter(new PrintWriter(outputStream), CSV_PRINTER_FORMAT)) {
            for (var person : persons) {
                printer.printRecord(Arrays.asList(
                        person.getDay() + DOT_SYMBOL + person.getMonth(),
                        person.getEmail(),
                        person.getName(),
                        String.valueOf(person.isModerator())));
            }

            printer.flush();
        } catch (IOException e) {
            log.info("Error during converting persons to CSV");
            throw new RuntimeException(e);
        }

        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
