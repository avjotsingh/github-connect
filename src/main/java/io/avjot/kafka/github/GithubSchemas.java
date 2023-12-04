package io.avjot.kafka.github;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Timestamp;

public class GithubSchemas {

    // Issue fields
    public static final String OWNER_FIELD = "owner";
    public static final String REPOSITORY_FIELD = "repository";
    public static final String CREATED_AT_FIELD = "created_at";
    public static final String UPDATED_AT_FIELD = "updated_at";
    public static final String ID_FIELD = "id";
    public static final String URL_FIELD = "url";
    public static final String HTML_URL_FIELD = "html_url";
    public static final String TITLE_FIELD = "title";
    public static final String STATE_FIELD = "state";

    // User fields
    public static final String USER_FIELD = "user";
    public static final String USER_URL_FIELD = "url";
    public static final String USER_HTML_URL_FIELD = "html_url";
    public static final String USER_ID_FIELD = "id";
    public static final String USER_LOGIN_FIELD = "login";

    // PR fields
    public static final String PR_FIELD = "pull_request";
    public static final String PR_URL_FIELD = "url";
    public static final String PR_HTML_URL_FIELD = "html_url";

    // Schema names
    public static final String KEY_SCHEMA_NAME = "io.avjot.kafka.github.IssueKey";
    public static final String VALUE_SCHEMA_NAME = "io.avjot.kafka.github.IssueValue";
    public static final String USER_SCHEMA_NAME = "io.avjot.kafka.github.UserValue";
    public static final String PR_SCHEMA_NAME = "io.avjot.kafka.github.PrValue";


    public static final Schema KEY_SCHEMA;
    static {
        KEY_SCHEMA = SchemaBuilder.struct()
                .name(KEY_SCHEMA_NAME)
                .version(1)
                .field(REPOSITORY_FIELD, Schema.STRING_SCHEMA)
                .field(OWNER_FIELD, Schema.STRING_SCHEMA)
                .field(ID_FIELD, Schema.INT32_SCHEMA)
                .build();

    }

    public static final Schema USER_SCHEMA;
    static {
        USER_SCHEMA = SchemaBuilder.struct()
                .name(USER_SCHEMA_NAME)
                .field(USER_LOGIN_FIELD, Schema.STRING_SCHEMA)
                .field(USER_ID_FIELD, Schema.INT32_SCHEMA)
                .field(USER_URL_FIELD, Schema.STRING_SCHEMA)
                .field(USER_HTML_URL_FIELD, Schema.STRING_SCHEMA)
                .build();
    }

    public static final Schema PR_SCHEMA;
    static {
        PR_SCHEMA = SchemaBuilder.struct()
                .version(1)
                .name(PR_SCHEMA_NAME)
                .field(PR_URL_FIELD, Schema.STRING_SCHEMA)
                .field(PR_HTML_URL_FIELD, Schema.STRING_SCHEMA)
                .optional()
                .build();
    }

    public static final Schema VALUE_SCHEMA;
    static {
        VALUE_SCHEMA = SchemaBuilder.struct()
                .version(1)
                .name(VALUE_SCHEMA_NAME)
                .field(URL_FIELD, Schema.STRING_SCHEMA)
                .field(HTML_URL_FIELD, Schema.STRING_SCHEMA)
                .field(ID_FIELD, Schema.INT32_SCHEMA)
                .field(TITLE_FIELD, Schema.STRING_SCHEMA)
                .field(USER_FIELD, USER_SCHEMA)
                .field(STATE_FIELD, Schema.STRING_SCHEMA)
                .field(CREATED_AT_FIELD, Timestamp.SCHEMA)
                .field(UPDATED_AT_FIELD, Timestamp.SCHEMA)
                .field(PR_FIELD, PR_SCHEMA);
    }
}
