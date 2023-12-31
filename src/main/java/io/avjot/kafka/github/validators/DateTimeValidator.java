package io.avjot.kafka.github.validators;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeValidator implements ConfigDef.Validator {
    @Override
    public void ensureValid(String s, Object o) {
        String timestamp = (String) o;
        try {
            Instant.parse(timestamp);
        } catch (DateTimeParseException e) {
            throw new ConfigException(s, o, "Wasn't able to parse the timestamp, make sure it is formatted according to ISO-8601 standards");
        }
    }
}
