package com.geotrust;
import com.geotrust.Models.*;
import jakarta.validation.Valid;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "${cors.origins}")
public class PointController {
  private final Tracker tracker; private final JdbcTemplate db;
  public PointController(Tracker t, JdbcTemplate db) { this.tracker = t; this.db = db; }

  @PostMapping("/api/v1/points")
  public Result ingest(@Valid @RequestBody GpsPoint p) {
    Result r = tracker.process(p);
    db.update("insert into gps_points(device_id,ts,raw_lat,raw_lon,accuracy,lat,lon,trust,flags) values (?,?,?,?,?,?,?,?,?)",
        p.deviceId(), p.timestamp(), p.lat(), p.lon(), p.accuracy(), r.lat(), r.lon(), r.trust(), r.flags());
    return r;
  }

  @GetMapping("/api/v1/devices/{id}/points")
  public List<Map<String, Object>> points(@PathVariable String id, @RequestParam(defaultValue = "500") int limit) {
    return db.queryForList("select ts,raw_lat,raw_lon,lat,lon,trust,flags from (select * from gps_points where device_id=? order by ts desc limit ?) t order by ts",
        id, Math.min(limit, 2000));
  }

  @GetMapping("/health") public Map<String, String> health() { return Map.of("status", "ok"); }
}
