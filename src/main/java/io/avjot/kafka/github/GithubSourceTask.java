package io.avjot.kafka.github;

import io.avjot.kafka.github.types.Issue;
import io.avjot.kafka.github.types.PullRequest;
import io.avjot.kafka.github.types.User;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GithubSourceTask extends SourceTask {

    private static final Logger log = LoggerFactory.getLogger(GithubSourceTask.class.getName());
    private GithubSourceConnectorConfig config;

    private String repository;
    private String owner;
    private Integer batchSize;
    private Integer pageToVisit;
    private ZonedDateTime querySince;

    private GithubHttpClient githubHttpClient;

    @Override
    public String version() {
        return Util.getVersion();
    }

    @Override
    public void start(Map<String, String> map) {
        log.info("Starting task: " + this.hashCode());

        config = new GithubSourceConnectorConfig(map);
        this.repository = config.getString(GithubSourceConnectorConfig.TOPIC_CONFIG);
        this.owner = config.getString(GithubSourceConnectorConfig.OWNER_CONFIG);
        this.batchSize = config.getInt(GithubSourceConnectorConfig.BATCH_SIZE_CONFIG);
        this.pageToVisit = 1;
        this.querySince = ZonedDateTime.parse(config.getString(GithubSourceConnectorConfig.SINCE_CONFIG));

        this.githubHttpClient = new GithubHttpClient(config);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {

        List<SourceRecord> records = new ArrayList<>();
        List<Issue> issues = githubHttpClient.getNextSetOfIssues();
        log.info(String.format("Fetched %d issues", issues.size()));
        for (Issue issue: issues) {
            records.add(getSourceRecordFromJson(issue));
        }

        return records;
    }

    @Override
    public void stop() {
        log.info("Stopping task: " + this.hashCode());
    }

    private SourceRecord getSourceRecordFromJson(Issue issue) {
        return new SourceRecord(
                getSourcePartition(),
                getSourceOffset(issue),
                config.getTopic(),
                null,    // let the partition be inferred by framework
                GithubSchemas.KEY_SCHEMA,
                getKey(issue),
                GithubSchemas.VALUE_SCHEMA,
                getValue(issue)
        );
    }

    private Map<String, String> getSourcePartition() {
        Map<String, String> sourcePartition = new HashMap<>();
        sourcePartition.put(GithubSchemas.OWNER_FIELD, config.getOwner());
        sourcePartition.put(GithubSchemas.REPOSITORY_FIELD, config.getRepo());

        return sourcePartition;
    }

    private Map<String, String> getSourceOffset(Issue issue) {
        Map<String, String> sourceOffset = new HashMap<>();
        sourceOffset.put(GithubSchemas.UPDATED_AT_FIELD, issue.getUpdatedAt().toString());

        return sourceOffset;
    }

    private Struct getKey(Issue issue) {
        Struct key = new Struct(GithubSchemas.KEY_SCHEMA)
                .put(GithubSchemas.REPOSITORY_FIELD, config.getRepo())
                .put(GithubSchemas.OWNER_FIELD, config.getOwner())
                .put(GithubSchemas.ID_FIELD, issue.getId());

        return key;
    }

    private Struct getValue(Issue issue) {
        Struct value = new Struct(GithubSchemas.VALUE_SCHEMA)
                .put(GithubSchemas.URL_FIELD, issue.getUrl())
                .put(GithubSchemas.HTML_URL_FIELD, issue.getHtmlUrl())
                .put(GithubSchemas.ID_FIELD, issue.getId())
                .put(GithubSchemas.TITLE_FIELD, issue.getTitle())
                .put(GithubSchemas.STATE_FIELD, issue.getState())
                .put(GithubSchemas.CREATED_AT_FIELD, Date.from(issue.getCreatedAt()))
                .put(GithubSchemas.UPDATED_AT_FIELD, Date.from(issue.getUpdatedAt()));

        User user = issue.getUser();
        Struct userStruct = new Struct(GithubSchemas.USER_SCHEMA)
                .put(GithubSchemas.USER_ID_FIELD, user.getId())
                .put(GithubSchemas.USER_LOGIN_FIELD, user.getLogin())
                .put(GithubSchemas.USER_URL_FIELD, user.getUrl())
                .put(GithubSchemas.USER_HTML_URL_FIELD, user.getHtmlUrl());

        value = value.put(GithubSchemas.USER_FIELD, userStruct);

        if (issue.getPullRequest() != null) {
            PullRequest pr = issue.getPullRequest();
            Struct prStruct = new Struct(GithubSchemas.PR_SCHEMA)
                    .put(GithubSchemas.PR_URL_FIELD, pr.getUrl())
                    .put(GithubSchemas.PR_HTML_URL_FIELD, pr.getHtmlUrl());

            value.put(GithubSchemas.PR_FIELD, prStruct);
        }

        return value;
    }
}
