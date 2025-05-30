package com.archiwork.events.commands.right;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

@Repository
public class CommandDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CommandDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Number> addCommands(List<AddCommand> commands) {
        String sql = """
                INSERT INTO commands (command_date, map_id, map_key, map_value)
                VALUES (:commandDate, :mapId, :mapKey, :mapValue)
                """;

        SqlParameterSource[] batchParams = commands.stream()
                .map(command -> new MapSqlParameterSource()
                        .addValue("commandDate", Timestamp.from(command.commandDate()))
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

    public List<GetCommand> findByIdGreaterThanOrderByIdAsc(long sinceId, int limit) {
        String sql = """
                SELECT id, command_date, map_id, map_key, map_value
                FROM commands
                WHERE id > :sinceId
                ORDER BY id ASC
                LIMIT :limit
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("sinceId", sinceId)
                .addValue("limit", limit);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new GetCommand(
                rs.getLong("id"),
                rs.getTimestamp("command_date").toInstant(),
                rs.getString("map_id"),
                rs.getString("map_key"),
                rs.getString("map_value")
        ));
    }
}
