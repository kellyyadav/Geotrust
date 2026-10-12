package com.geotrust;
import jakarta.validation.constraints.*;
public class Models {
  public record GpsPoint(@NotBlank String deviceId,
      @DecimalMin("-90") @DecimalMax("90") double lat,
      @DecimalMin("-180") @DecimalMax("180") double lon,
      Double accuracy, long timestamp) {}
  public record Result(double lat, double lon, int trust, String flags) {}
}
