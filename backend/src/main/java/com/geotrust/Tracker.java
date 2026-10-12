package com.geotrust;
import com.geotrust.Models.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Per-device Kalman smoothing + rule-based spoof/outlier detection + trust score. */
@Service
public class Tracker {
  private static final double MAX_SPEED = 60, HIGH_SPEED = 40; // m/s
  private static class S {
    double oLat, oLon, lastLat, lastLon, cLat, cLon; long lastTs;
    final Kalman1D kx = new Kalman1D(), ky = new Kalman1D();
  }
  private final Map<String, S> devices = new ConcurrentHashMap<>();

  public Result process(GpsPoint p) {
    S s = devices.computeIfAbsent(p.deviceId(), k -> new S());
    synchronized (s) {
      double acc = p.accuracy() == null ? 10 : p.accuracy();
      int trust = 100; List<String> flags = new ArrayList<>(); boolean reject = false;
      double dt = 1;
      if (s.lastTs == 0) { s.oLat = p.lat(); s.oLon = p.lon(); }
      else {
        dt = Math.max((p.timestamp() - s.lastTs) / 1000.0, 0.1);
        double speed = meters(s, s.lastLat, s.lastLon, p.lat(), p.lon()) / dt;
        if (speed > MAX_SPEED) { flags.add("TELEPORT"); trust -= 60; reject = true; }
        else if (speed > HIGH_SPEED) { flags.add("HIGH_SPEED"); trust -= 20; }
      }
      if (acc > 50) { flags.add("POOR_ACCURACY"); trust -= 20; }
      if (acc < 1) { flags.add("SUSPICIOUS_ACCURACY"); trust -= 15; }

      double cos = Math.cos(Math.toRadians(s.oLat));
      double mx = (p.lon() - s.oLon) * 111320 * cos, my = (p.lat() - s.oLat) * 110540;
      double r = Math.max(acc, 3) * Math.max(acc, 3);
      double fx = s.kx.step(mx, dt, r, 0.5, !reject);
      double fy = s.ky.step(my, dt, r, 0.5, !reject);
      s.cLon = s.oLon + fx / (111320 * cos); s.cLat = s.oLat + fy / 110540;
      if (!reject) { s.lastLat = p.lat(); s.lastLon = p.lon(); s.lastTs = p.timestamp(); }
      return new Result(s.cLat, s.cLon, Math.max(trust, 0), String.join(",", flags));
    }
  }

  private double meters(S s, double la1, double lo1, double la2, double lo2) {
    double cos = Math.cos(Math.toRadians(s.oLat));
    double dx = (lo2 - lo1) * 111320 * cos, dy = (la2 - la1) * 110540;
    return Math.hypot(dx, dy);
  }
}
