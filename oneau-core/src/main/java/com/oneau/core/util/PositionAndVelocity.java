package com.oneau.core.util;

import com.oneau.core.util.HeavenlyBody;

/**
 * User: EBridges
 * Created: 2010-04-15
 */
public class PositionAndVelocity {
    private Double ephemerisDate;
    private HeavenlyBody body;
    private Double[] position;
    private Double[] velocity;

    public PositionAndVelocity(Double ephemerisDate, HeavenlyBody body, Double[] position, Double[] velocity) {
        this.ephemerisDate = ephemerisDate;
        this.body = body;
        this.position = position;
        this.velocity = velocity;
    }

    public Double getEphemerisDate() {
        return ephemerisDate;
    }

    public HeavenlyBody getBody() {
        return body;
    }

    public Double[] getPosition() {
        return position;
    }

    public Double[] getVelocity() {
        return velocity;
    }
}

