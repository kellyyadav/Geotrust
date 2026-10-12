package com.geotrust;
import com.geotrust.Models.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class TrackerTest {
  @Test void teleportIsFlagged() {
    Tracker t = new Tracker();
    t.process(new GpsPoint("d", 26.9124, 75.7873, 5.0, 1000));
    Result r = t.process(new GpsPoint("d", 26.9624, 75.7873, 5.0, 2000)); // ~5.5 km in 1 s
    assertTrue(r.flags().contains("TELEPORT"));
    assertTrue(r.trust() <= 40);
    assertTrue(Math.abs(r.lat() - 26.9124) < 0.001); // corrected position ignores the jump
  }
}
