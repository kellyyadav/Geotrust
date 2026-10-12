# GeoTrust
Java (Spring Boot) backend that ingests noisy GPS streams, smooths them with a Kalman filter, flags spoofing/outliers, and assigns a 0-100 trust score per point. Postgres on Supabase, dashboard on Vercel.

- `backend/` Spring Boot API + Kalman + trust scoring
- `supabase/schema.sql` table definition
- `simulator/` generates noisy walk + spoofed jump and prints RMSE (raw vs corrected)
- `dashboard/` Leaflet map (red = raw, green = corrected, orange = flagged)


