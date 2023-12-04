package io.avjot.kafka.github.types;

import io.avjot.kafka.github.GithubSchemas;
import org.json.JSONObject;

public class User {

    private String login;
    private Integer id;
    private String url;
    private String htmlUrl;

    private User() {
        // do nothing
    }

    public static User fromJson(JSONObject o) {
        User user = new User();
        user.login = o.getString(GithubSchemas.USER_LOGIN_FIELD);
        user.id = o.getInt(GithubSchemas.USER_ID_FIELD);
        user.url = o.getString(GithubSchemas.USER_URL_FIELD);
        user.htmlUrl = o.getString(GithubSchemas.USER_HTML_URL_FIELD);

        return user;
    }

    public String getLogin() {
        return login;
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
}
