package com.archiwork.aggregator.map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MapValueDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public MapValueDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertBatch(List<MapValue> mapValues) {
        if (mapValues == null || mapValues.isEmpty()) {
            return;
        }

        String sql = """
                MERGE INTO map_values AS target
                USING (VALUES (:commandDate, :mapId, :mapKey, :mapValue)) AS source (command_date, map_id, map_key, map_value)
                ON target.map_id = source.map_id AND target.map_key = source.map_key
                WHEN MATCHED THEN 
                    UPDATE SET
                        target.command_date = source.command_date,
                        target.map_value = source.map_value
                WHEN NOT MATCHED THEN
                    INSERT (command_date, map_id, map_key, map_value)
                    VALUES (source.command_date, source.map_id, source.map_key, source.map_value)
                """;

        List<MapSqlParameterSource> batchParams = mapValues.stream()
                .map(mapValue -> new MapSqlParameterSource()
                        .addValue("commandDate", mapValue.commandDate())
                        .addValue("mapId", mapValue.mapId())
                        .addValue("mapKey", mapValue.mapKey())
                        .addValue("mapValue", mapValue.mapValue()))
                .toList();

        jdbcTemplate.batchUpdate(sql, batchParams.toArray(new MapSqlParameterSource[0]));
    }
}
