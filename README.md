# GeoTrust
Java (Spring Boot) backend that ingests noisy GPS streams, smooths them with a Kalman filter, flags spoofing/outliers, and assigns a 0-100 trust score per point. Postgres on Supabase, dashboard on Vercel.

- `backend/` Spring Boot API + Kalman + trust scoring
- `supabase/schema.sql` table definition
- `simulator/` generates noisy walk + spoofed jump and prints RMSE (raw vs corrected)
- `dashboard/` Leaflet map (red = raw, green = corrected, orange = flagged)

## Run locally
1. Run `supabase/schema.sql` in your Supabase SQL editor; copy `.env.example` values into your shell.
2. `cd backend && mvn spring-boot:run`
3. `pip install -r simulator/requirements.txt && python simulator/simulate.py`
4. Open `dashboard/index.html`.

## Results
Fill in from simulator output: RMSE raw ___ m -> corrected ___ m.
