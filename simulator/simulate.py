"""Simulates a walker with GPS noise + one spoofed jump; reports RMSE raw vs corrected."""
import math, random, sys, time, requests
API = sys.argv[1] if len(sys.argv) > 1 else "http://localhost:8080"
DEVICE, LAT0, LON0, R, N = "sim-1", 26.9124, 75.7873, 300.0, 200
M_LAT, M_LON = 110540.0, 111320.0 * math.cos(math.radians(LAT0))
raw_err, cor_err = [], []
t0 = int(time.time() * 1000)
for i in range(N):
    a = i * 0.03
    tx, ty = R * math.cos(a), R * math.sin(a)             # ground truth (meters)
    nx, ny = tx + random.gauss(0, 8), ty + random.gauss(0, 8)
    if i == 100: nx, ny = nx + 5000, ny                    # spoofed teleport
    body = {"deviceId": DEVICE, "lat": LAT0 + ny / M_LAT, "lon": LON0 + nx / M_LON,
            "accuracy": 8.0, "timestamp": t0 + i * 1000}
    res = requests.post(f"{API}/api/v1/points", json=body, timeout=30).json()
    cx, cy = (res["lon"] - LON0) * M_LON, (res["lat"] - LAT0) * M_LAT
    raw_err.append(math.hypot(nx - tx, ny - ty)); cor_err.append(math.hypot(cx - tx, cy - ty))
    time.sleep(0.05)
rmse = lambda e: math.sqrt(sum(x * x for x in e) / len(e))
print(f"RMSE raw: {rmse(raw_err):.1f} m | corrected: {rmse(cor_err):.1f} m | "
      f"improvement: {100 * (1 - rmse(cor_err) / rmse(raw_err)):.0f}%")
