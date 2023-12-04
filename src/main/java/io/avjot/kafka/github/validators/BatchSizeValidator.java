package io.avjot.kafka.github.validators;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

public class BatchSizeValidator implements ConfigDef.Validator {
    @Override
    public void ensureValid(String s, Object o) {
        Integer batchSize = (Integer) o;
        if (batchSize < 1 || batchSize > 100) {
            throw new ConfigException(s, o, "Batch size must be a positive integer less than or equal to 100");
        }
    }
}
