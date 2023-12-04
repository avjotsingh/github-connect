package io.avjot.kafka.github.types;

import io.avjot.kafka.github.GithubSchemas;
import org.json.JSONObject;

public class PullRequest {

    private String url;
    private String htmlUrl;

    private PullRequest() {
        // do nothing
    }

    public static PullRequest fromJson(JSONObject o) {
        PullRequest pr = new PullRequest();
        pr.url = o.getString(GithubSchemas.PR_URL_FIELD);
        pr.htmlUrl = o.getString(GithubSchemas.PR_HTML_URL_FIELD);
        return pr;
    }

    public String getUrl() {
        return url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }
}
