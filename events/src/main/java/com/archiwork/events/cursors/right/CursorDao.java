package com.archiwork.events.cursors.right;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CursorDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CursorDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Long> getCursorIndex(String serviceName) {
        var params = new MapSqlParameterSource()
                .addValue("serviceName", serviceName);

        String sql = "SELECT cursor_index FROM cursors WHERE service_name = :serviceName";

        return jdbcTemplate.query(sql, params, rs -> {
            if (rs.next()) {
                return Optional.of(rs.getLong("cursor_index"));
            } else {
                return Optional.empty();
            }
        });
    }

    public void setCursorIndex(String serviceName, long cursorIndex) {
        var params = new MapSqlParameterSource()
                .addValue("serviceName", serviceName)
                .addValue("cursorIndex", cursorIndex);

        String sql = """
                MERGE INTO cursors (service_name, cursor_index)
                KEY (service_name)
                VALUES (:serviceName, :cursorIndex)
                """;

        jdbcTemplate.update(sql, params);
    }
}
