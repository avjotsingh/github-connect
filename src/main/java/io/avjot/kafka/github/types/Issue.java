package io.avjot.kafka.github.types;

import io.avjot.kafka.github.GithubSchemas;
import org.json.JSONObject;

import java.time.Instant;

public class Issue {

    private Integer id;
    private String url;
    private String htmlUrl;
    private String title;
    private String state;
    private String createdAt;
    private String updatedAt;
    private User user;
    private PullRequest pullRequest;

    private Issue() {
        // do nothing
    }

    public static Issue fromJson(JSONObject o) {
        Issue issue = new Issue();
        issue.id = o.getInt(GithubSchemas.ID_FIELD);
        issue.url = o.getString(GithubSchemas.URL_FIELD);
        issue.htmlUrl = o.getString(GithubSchemas.HTML_URL_FIELD);
        issue.title = o.getString(GithubSchemas.TITLE_FIELD);
        issue.state = o.getString(GithubSchemas.STATE_FIELD);
        issue.createdAt = o.getString(GithubSchemas.CREATED_AT_FIELD);
        issue.updatedAt = o.getString(GithubSchemas.UPDATED_AT_FIELD);
        issue.user = User.fromJson(o.getJSONObject(GithubSchemas.USER_FIELD));

        if (o.has(GithubSchemas.PR_FIELD)) {
            issue.pullRequest = PullRequest.fromJson(o.getJSONObject(GithubSchemas.PR_FIELD));
        }

        return issue;
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getState() {
        return state;
    }

    public Instant getCreatedAt() {
        return Instant.parse(createdAt);
    }

    public Instant getUpdatedAt() {
        return Instant.parse(updatedAt);
    }

    public User getUser() {
        return user;
    }

    public PullRequest getPullRequest() {
        return pullRequest;
    }
}
