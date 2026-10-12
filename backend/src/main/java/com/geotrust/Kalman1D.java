package com.geotrust;
/** Constant-velocity Kalman filter for one axis. State [pos, vel]. */
public class Kalman1D {
  private double x, v, p00 = 1000, p01, p10, p11 = 1000;
  private boolean init;
  public double step(double z, double dt, double r, double q, boolean update) {
    if (!init) { x = z; init = true; return x; }
    x += v * dt;                                   // predict
    double n00 = p00 + dt * (p10 + p01) + dt * dt * p11 + q * dt * dt * dt / 3;
    double n01 = p01 + dt * p11 + q * dt * dt / 2;
    double n10 = p10 + dt * p11 + q * dt * dt / 2;
    double n11 = p11 + q * dt;
    if (!update) { p00 = n00; p01 = n01; p10 = n10; p11 = n11; return x; }
    double s = n00 + r, k0 = n00 / s, k1 = n10 / s, y = z - x;   // update
    x += k0 * y; v += k1 * y;
    p00 = (1 - k0) * n00; p01 = (1 - k0) * n01;
    p10 = n10 - k1 * n00; p11 = n11 - k1 * n01;
    return x;
  }
}
