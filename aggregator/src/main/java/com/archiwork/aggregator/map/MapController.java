package com.archiwork.aggregator.map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/map")
public class MapController {

    private final MapValueDao mapValueDao;

    public MapController(MapValueDao mapValueDao) {
        this.mapValueDao = mapValueDao;
    }

    @GetMapping("/stats")
    public List<MapStats> getMapStats(@RequestParam(name = "mapId", required = false) List<String> mapIds) {
        return mapValueDao.getMapStats(mapIds);
    }
}
