package com.oneau.core.util;

import java.util.List;

import static com.oneau.core.util.Utility.throwIfNull;
import static java.lang.String.format;

/**
 * User: ebridges
 * Date: Jul 22, 2010
 */
public class Coordinate {
    private Double x;
    private Double y;
    private Double z;

    public Coordinate(List<Double> xyz) {
        throwIfNull("coefficientList", xyz);
        
        if(xyz.size() == 3) {
            this.x = xyz.get(0);
            this.y = xyz.get(1);
            this.z = xyz.get(2);
        }

        else if(xyz.size() == 2) {
            this.x = xyz.get(0);
            this.y = xyz.get(1);
        }

        else {
            throw new IllegalArgumentException("list of coefficients must be two or three");
        }
    }

    public Coordinate(Double x, Double y, Double z) {
        this.x = throwIfNull("x", x);
        this.y = throwIfNull("y", y);
        this.z = throwIfNull("z", z);
    }

    public Coordinate(Double x, Double y) {
        this.x = throwIfNull("x", x);
        this.y = throwIfNull("y", y);
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }

    @Override
    public String toString() {
        String fmt = "{x:%f,y:%f";
        if(null != z) {
            fmt += ",z:%f}";
        } else {
            fmt += "}";
        }
        if(null != z) {
            return format(fmt, x, y, z);
        } else {
            return format(fmt, x, y);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        if (!x.equals(that.x)) return false;
        if (!y.equals(that.y)) return false;
        if (z != null ? !z.equals(that.z) : that.z != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        result = 31 * result + (z != null ? z.hashCode() : 0);
        return result;
    }
}
