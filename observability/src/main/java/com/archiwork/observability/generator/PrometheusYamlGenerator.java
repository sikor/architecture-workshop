package com.archiwork.observability.generator;

import com.archiwork.observability.alerts.AlertDefinition;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.common.FlowStyle;

import java.util.*;

public class PrometheusYamlGenerator {

    public String generate(List<AlertDefinition> alerts) {
        Map<String, Object> ruleGroup = new LinkedHashMap<>();
        ruleGroup.put("name", "app-alerts");

        List<Map<String, Object>> rules = new ArrayList<>();
        for (AlertDefinition alert : alerts) {
            Map<String, Object> rule = alertToMap(alert);
            rules.add(rule);
        }

        ruleGroup.put("rules", rules);
        List<Map<String, Object>> groups = List.of(ruleGroup);
        Map<String, Object> root = Map.of("groups", groups);

        DumpSettings settings = DumpSettings.builder()
                .setIndent(2)
                .setDefaultFlowStyle(FlowStyle.BLOCK)
                .build();

        Dump yamlDump = new Dump(settings);
        return yamlDump.dumpToString(root);
    }

    private static Map<String, Object> alertToMap(AlertDefinition alert) {
        Map<String, Object> rule = new LinkedHashMap<>();
        rule.put("alert", alert.name());
        rule.put("expr", alert.metric() + " > " + alert.threshold());
        rule.put("for", alert.duration().toMinutes() + "m");

        Map<String, String> labels = Map.of("severity", alert.severity().name().toLowerCase());
        rule.put("labels", labels);

        Map<String, String> annotations = Map.of("description", alert.description());
        rule.put("annotations", annotations);
        return rule;
    }
}
