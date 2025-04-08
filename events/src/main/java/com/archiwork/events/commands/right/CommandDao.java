package com.archiwork.events.commands.right;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommandDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CommandDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Number> addCommands(List<Command> commands) {
        String sql = """
                INSERT INTO commands (command_date, map_id, map_key, map_value)
                VALUES (:commandDate, :mapId, :mapKey, :mapValue)
                """;

        SqlParameterSource[] batchParams = commands.stream()
                .map(command -> new MapSqlParameterSource()
                        .addValue("commandDate", command.commandDate())
                        .addValue("mapId", command.mapId())
                        .addValue("mapKey", command.mapKey())
                        .addValue("mapValue", command.mapValue()))
                .toArray(SqlParameterSource[]::new);

        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.batchUpdate(sql, batchParams, kh);
        return kh.getKeyList().stream()
                .map(m -> (Number) m.values().stream().findFirst().get())
                .toList();
    }
}
