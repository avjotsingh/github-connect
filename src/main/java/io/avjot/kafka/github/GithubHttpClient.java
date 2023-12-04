package io.avjot.kafka.github;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import io.avjot.kafka.github.types.Issue;
import org.apache.commons.text.StringSubstitutor;
import org.apache.kafka.connect.errors.ConnectException;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GithubHttpClient {

    private static final Logger log = LoggerFactory.getLogger(GithubHttpClient.class.getName());
    private static final String githubIssuesUrl = "https://api.github.com/repos/{repo}/{owner}/issues?since={since}&per_page={batchSize}&page={pageToVisit}&sort=updated";
    public static final String X_RATELIMIT_LIMIT = "X-RateLimit-Limit";
    public static final String X_RATELIMIT_REMAINING = "X-RateLimit-Remaining";
    public static final String X_RATELIMIT_RESET = "X-RateLimit-Reset";

    private Integer batchSize;
    private Instant sinceTimestamp;
    private Instant latestUpdatedAtTimestamp;
    private Integer pageToVisit;

    private int ratelimit_limit;
    private int ratelimit_remaining;
    private int ratelimit_reset;

    private final GithubSourceConnectorConfig config;


    public GithubHttpClient(GithubSourceConnectorConfig config) {
        this.config = config;
        this.batchSize = config.getBatchSize();
        this.sinceTimestamp = config.getSince();
        this.latestUpdatedAtTimestamp = config.getSince();
        this.pageToVisit = 1;
    }

    private String constructUrl() {
        Map<String, String> values = new HashMap<>();
        values.put("owner", config.getOwner());
        values.put("repo", config.getRepo());
        values.put("since", sinceTimestamp.toString());
        values.put("batchSize", config.getBatchSize().toString());
        values.put("pageToVisit", pageToVisit.toString());

        StringSubstitutor substitutor = new StringSubstitutor(values, "{", "}");
        return substitutor.replace(githubIssuesUrl);
    }

    public List<Issue> getNextSetOfIssues() {

        try {
            sleepIfNeeded();
            HttpResponse<JsonNode> response;

            response = getNextSetOfIssuesAPI();
            Headers headers = response.getHeaders();

            Integer limit = Integer.parseInt(headers.getFirst(X_RATELIMIT_LIMIT));
            Integer remaining = Integer.parseInt(headers.getFirst(X_RATELIMIT_REMAINING));
            long reset = Long.parseLong(headers.getFirst(X_RATELIMIT_RESET));

            switch (response.getStatus()) {
                case 200:
                    JSONArray issuesArray = response.getBody().getArray();
                    List<Issue> issues = new ArrayList<>();
                    for (int i = 0; i < issuesArray.length(); i++) {
                        issues.add(Issue.fromJson(issuesArray.getJSONObject(i)));
                    }

                    if (issues.size() == 0) {
                        // sleep for a while
                        int sleepDuration = 5;
                        Thread.sleep(1000 * sleepDuration);
                        return new ArrayList<>();
                    } else {
                        latestUpdatedAtTimestamp = issues.get(issues.size() - 1).getUpdatedAt();
                        if (issues.size() == batchSize) {
                            pageToVisit++;
                        } else {
                            pageToVisit = 1;
                            sinceTimestamp = latestUpdatedAtTimestamp.plusSeconds(1);
                        }
                        return issues;
                    }
                case 401:
                    throw new ConnectException("Bad request credentials. Please edit your config.");
                case 403:
                    log.warn(response.getBody().getObject().getString("message"));
                    log.warn(String.format("Rate limit is %s", limit));
                    log.warn(String.format("Remaining rate limit is %s", remaining));
                    log.warn(String.format("Rate limit will reset at %s", ZonedDateTime.ofInstant(Instant.ofEpochSecond(reset), ZoneOffset.UTC).toLocalDateTime()));
                    long sleepDurationSeconds = reset - Instant.now().getEpochSecond();
                    log.info(String.format("Sleeping for %s seconds since rate limit has exceeded", sleepDurationSeconds));
                    Thread.sleep(1000 * sleepDurationSeconds);
                    return new ArrayList<>();
                default:
                    log.warn(response.getBody().getObject().getString("message"));
                    log.warn("Status code: " + response.getStatus());
                    log.warn("Headers: " + response.getHeaders().toString());
                    log.warn("Body: " + response.getBody().toString());
                    int sleepDuration = 5;
                    log.info(String.format("Unknown error. Sleeping for %s seconds before retrying", sleepDuration));
                    Thread.sleep(1000 * sleepDuration);
                    return new ArrayList<>();
            }
        } catch (UnirestException | InterruptedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private HttpResponse<JsonNode> getNextSetOfIssuesAPI() throws UnirestException {
        String issuesUrl = constructUrl();
        GetRequest request = Unirest.get(issuesUrl);
        if (!config.getAuthUsername().isEmpty() && !config.getAuthPassword().isEmpty()) {
            request = request.basicAuth(config.getAuthUsername(), config.getAuthPassword());
        }
        return request.asJson();
    }

    private void sleepIfNeeded() throws InterruptedException {
        if (ratelimit_remaining > 0 && ratelimit_remaining <= 10) {
            log.info("Approaching rate limit soon. Sleeping");
            long sleepDuration = (ratelimit_reset - ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond()) / ratelimit_remaining;
            Thread.sleep(sleepDuration);
        }
    }
}
