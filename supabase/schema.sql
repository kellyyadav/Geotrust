create table if not exists gps_points (
  id bigserial primary key,
  device_id text not null,
  ts bigint not null,
  raw_lat double precision not null,
  raw_lon double precision not null,
  accuracy double precision,
  lat double precision not null,
  lon double precision not null,
  trust int not null,
  flags text default ''
);
create index if not exists idx_points_device_ts on gps_points (device_id, ts desc);
alter table gps_points enable row level security; -- backend connects with DB credentials, not the public API
