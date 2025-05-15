package com.archiwork.aggregator.map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
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
        INSERT INTO map_values (command_date, map_id, map_key, map_value)
        VALUES (:commandDate, :mapId, :mapKey, :mapValue)
        ON CONFLICT (map_id, map_key)
        DO UPDATE SET
            command_date = EXCLUDED.command_date,
            map_value = EXCLUDED.map_value
        """;

        List<MapSqlParameterSource> batchParams = mapValues.stream()
                .map(mapValue -> new MapSqlParameterSource()
                        .addValue("commandDate", Timestamp.from(mapValue.commandDate()))
                        .addValue("mapId", mapValue.mapId())
                        .addValue("mapKey", mapValue.mapKey())
                        .addValue("mapValue", mapValue.mapValue()))
                .toList();

        jdbcTemplate.batchUpdate(sql, batchParams.toArray(new MapSqlParameterSource[0]));
    }

    public List<MapStats> getMapStats(List<String> mapIds) {
        String baseSql = """
        SELECT
            map_id,
            COUNT(*) AS key_count,
            MD5(STRING_AGG(map_key || COALESCE(map_value, ''), ',' ORDER BY map_key)) AS checksum
        FROM map_values
        """;

        String whereClause = "";
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (mapIds != null && !mapIds.isEmpty()) {
            whereClause = "WHERE map_id IN (:mapIds)\n";
            params.addValue("mapIds", mapIds);
        }

        String sql = baseSql + whereClause + "GROUP BY map_id";

        return jdbcTemplate.query(sql, params, (rs, rowNum) ->
                new MapStats(
                        rs.getString("map_id"),
                        rs.getLong("key_count"),
                        rs.getString("checksum")
                )
        );
    }



}
