package io.avjot.kafka.github;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GithubSourceConnector extends SourceConnector {

    private GithubSourceConnectorConfig config;

    @Override
    public void start(Map<String, String> map) {
        config = new GithubSourceConnectorConfig(map);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return GithubSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int i) {
        List<Map<String, String>> configs = new ArrayList<>();
        configs.add(config.originalsStrings());
        return configs;
    }

    @Override
    public void stop() {
        // do nothing
    }

    @Override
    public ConfigDef config() {
        return GithubSourceConnectorConfig.defineConfig();
    }

    @Override
    public String version() {
        return Util.getVersion();
    }
}
