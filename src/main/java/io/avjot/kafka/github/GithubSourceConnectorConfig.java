package io.avjot.kafka.github;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Importance;
import io.avjot.kafka.github.validators.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


public class GithubSourceConnectorConfig extends AbstractConfig {

    public static final String TOPIC_CONFIG = "topic";
    public static final String TOPIC_DOC = "Kafka topic to write to";

    public static final String REPO_CONFIG = "repo";
    public static final String REPO_DOC = "Github repository";

    public static final String OWNER_CONFIG = "owner";
    public static final String OWNER_DOC = "Owner of the github repository";

    public static final String SINCE_CONFIG = "since.timestamp";
    public static final String SINCE_DOC = "Timestamp in ISO format." +
            "Only issues after this timestamp will be returned";

    public static final String BATCH_SIZE_CONFIG = "batch.size";
    public static final String BATCH_SIZE_DOC = "Number of issues to return at a time";

    public static final String AUTH_USERNAME_CONFIG = "auth.username";
    public static final String AUTH_USERNAME_DOC = "Optional username for authenticating calls";

    public static final String AUTH_PASSWORD_CONFIG = "auth.password";
    public static final String AUTH_PASSWORD_DOC = "Optional password for authenticating calls";


    public GithubSourceConnectorConfig(ConfigDef definition, Map<?, ?> originals) {
        super(definition, originals);
    }

    public GithubSourceConnectorConfig(Map<?, ?> originals) {
        this(defineConfig(), originals);
    }

    public static ConfigDef defineConfig() {
        ConfigDef conf = new ConfigDef();
        conf = conf.define(
                TOPIC_CONFIG,
                Type.STRING,
                Importance.HIGH,
                TOPIC_DOC
        ).define(
                OWNER_CONFIG,
                Type.STRING,
                Importance.HIGH,
                OWNER_DOC
        ).define(
                REPO_CONFIG,
                Type.STRING,
                Importance.HIGH,
                REPO_DOC
        ).define(
                SINCE_CONFIG,
                Type.STRING,
                ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT).toString(),
                new DateTimeValidator(),
                Importance.HIGH,
                SINCE_DOC
        ).define(
                BATCH_SIZE_CONFIG,
                Type.INT,
                100,
                new BatchSizeValidator(),
                Importance.LOW,
                BATCH_SIZE_DOC
        ).define(
                AUTH_USERNAME_CONFIG,
                Type.STRING,
                "",
                Importance.HIGH,
                AUTH_USERNAME_DOC
        ).define(
                AUTH_PASSWORD_CONFIG,
                Type.STRING,
                "",
                Importance.HIGH,
                AUTH_PASSWORD_DOC
        );

        return conf;
    }

    public String getTopic() {
        return this.getString(TOPIC_CONFIG);
    }

    public String getRepo() {
        return this.getString(REPO_CONFIG);
    }

    public String getOwner() {
        return this.getString(OWNER_CONFIG);
    }

    public Instant getSince() {
        return Instant.parse(this.getString(SINCE_CONFIG));
    }

    public Integer getBatchSize() {
        return this.getInt(BATCH_SIZE_CONFIG);
    }

    public String getAuthUsername() {
        return this.getString(AUTH_USERNAME_CONFIG);
    }

    public String getAuthPassword() {
        return this.getString(AUTH_PASSWORD_CONFIG);
    }
}