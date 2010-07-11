package com.oneau.web.util;

import static com.oneau.web.util.Utility.throwIfNull;
import static java.lang.String.format;

/**
 * User: ebridges
 * Date: Jul 5, 2010
 */
public class MagneticDeclination {
    private Integer year;
    private Integer month;
    private Integer day;
    private Double longitude;
    private Double latitude;
    private Double elevation;
    private Double horizontalIntensity;
    private Double totalIntensity;
    private Double inclination;
    private Double declination;

    public MagneticDeclination(Integer year, Integer month, Integer day, Double latitude, Double longitude, Double elevation) {
        this.year = throwIfNull("year", year);
        this.month = throwIfNull("month", month);
        this.day = throwIfNull("day", day);
        this.longitude = throwIfNull("longitude", longitude);
        this.latitude = throwIfNull("latitude", latitude);
        this.elevation = throwIfNull("elevation", elevation);
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getDay() {
        return day;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getElevation() {
        return elevation;
    }

    public Double getHorizontalIntensity() {
        return horizontalIntensity;
    }

    public void setHorizontalIntensity(Double horizontalIntensity) {
        this.horizontalIntensity = horizontalIntensity;
    }

    public Double getTotalIntensity() {
        return totalIntensity;
    }

    public void setTotalIntensity(Double totalIntensity) {
        this.totalIntensity = totalIntensity;
    }

    public Double getInclination() {
        return inclination;
    }

    public void setInclination(Double inclination) {
        this.inclination = inclination;
    }

    public Double getDeclination() {
        return declination;
    }

    public void setDeclination(Double declination) {
        this.declination = declination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MagneticDeclination that = (MagneticDeclination) o;

        if (!day.equals(that.day)) return false;
        if (!elevation.equals(that.elevation)) return false;
        if (!latitude.equals(that.latitude)) return false;
        if (!longitude.equals(that.longitude)) return false;
        if (!month.equals(that.month)) return false;
        if (!year.equals(that.year)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = year.hashCode();
        result = 31 * result + month.hashCode();
        result = 31 * result + day.hashCode();
        result = 31 * result + longitude.hashCode();
        result = 31 * result + latitude.hashCode();
        result = 31 * result + elevation.hashCode();
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(format("date = %d/%d/%d\n", year, month, day));
        sb.append(format("latitude = %f\n", latitude));
        sb.append(format("longitude = %f\n", longitude));
        sb.append(format("horizontal intensity = %f\n", horizontalIntensity));
        sb.append(format("total intensity = %f\n", totalIntensity));
        sb.append(format("inclination angle =  %f\n", inclination));
        sb.append(format("declination angle =  %f\n", declination));
        return sb.toString();
    }
}
