package com.archiwork.events.commands.right;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommandDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CommandDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addCommands(List<Command> commands) {
        String sql = """
                INSERT INTO commands (command_date, map_id, map_key, map_value)
                VALUES (:commandDate, :mapId, :mapKey, :mapValue)
                """;

        MapSqlParameterSource[] params = commands.stream()
                .map(command -> new MapSqlParameterSource()
                        .addValue("commandDate", command.commandDate())
                        .addValue("mapId", command.mapId())
                        .addValue("mapKey", command.mapKey())
                        .addValue("mapValue", command.mapValue()))
                .toArray(MapSqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sql, params);
    }
}
