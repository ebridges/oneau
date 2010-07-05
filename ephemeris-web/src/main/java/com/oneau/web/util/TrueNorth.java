package com.oneau.web.util;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.lang.System.out;

/**
 * Program to calculate the angular difference between true north and magnetic north for any location on Earth.
 *
 * @author Greg Ronan, 1AU, Inc.
 * @created 2010-05-11
 */
public class TrueNorth {
    public static final double r_E = 6371200.0;            // meters
    public static final double earth_a = 6378137.0;            // meters
    public static final double earth_b = 6356752.3142;        // 1/298.257223563 flattening

    public static void main(String[] args) {
        TrueNorth t = new TrueNorth();
        MagneticDeclension decl;
        decl = t.calculate(2010, 7, 2, 40.72, -73.96); // 11211
        out.println(decl);
        out.println("---");
        decl = t.calculate(2010, 7, 2, 33.67, 117.90); // 92626
        out.println(decl);
        out.println("---");
        decl = t.calculate(2012, 6, 0, -80.0, 240.0, 100000.0);
        out.println(decl);


        /*
        Questions:
        what are 'r_E', 'earth_a', & 'earth_b'?
        what are m, n, K, S, P, & dP?
        why does he use a '0' for the day? Are months also zero based?
        why is the year boxed between 2010 and 2015? what are the 4 arrays of numbers for?
            -- what version of the coefficients did you use (IGRF11.COF vs. WMM2010.COF)?
        the value for latitude in your example is '240', which is beyond the range of +-180.  google uses
        +/- 180. Is 240 equal to + or - 120?
http://geomag.nrcan.gc.ca/apps/mdcal-eng.php?Year=2012&Month=6&Day=1&Lat=80&Min=&LatSign=-1&Long=120&Min2=&LongSign=-1&Submit=Calculate+magnetic+declination&CityIndex=0
         */
    }

    public MagneticDeclension calculate(int year, int month, int day, double latitude, double longitude) {
        return calculate(year, month, day, latitude, longitude, 0);
    }

    public MagneticDeclension calculate(int year, int month, int day, double latitude, double longitude, double elevation) {

        int n;
        int m;
        int nMax = 12;

        double[][] K = new double[13][13];       //  K[n][m]

        double[][] P = new double[13][13];       //  P[n][m]
        double[][] dP = new double[13][13];      // dP[n][m]

        double[][] S = new double[13][13];       //  S[n][m]

        double[][] gSt = new double[13][13];      // gSt[n][m]
        double[][] hSt = new double[13][13];      // hSt[n][m]

        double lat_rad;
        double long_rad;

        double lat0_rad;
        double colat_rad;

        double X_m;
        double Y_m;
        double Z_m;

        double X_prime;
        double Y_prime;
        double Z_prime;

        double X_vector;
        double Y_vector;
        double Z_vector;

        double mag_H;
        double mag_F;
        double mag_I;
        double mag_D;

        double Qa;
        double Qb;
        double earth_R;


        double time;


////////////////////////////////////////////////

// EQB: what are these numbers for?
        double[][] g =
                {
                        {
                                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                -29496.6, -1586.3, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                -2396.6, 3026.1, 1668.6, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                1340.1, -2326.2, 1231.9, 634.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                912.6, 808.9, 166.7, -357.1, 89.4, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                -230.9, 357.2, 200.3, -141.1, -163.0, -7.8, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                72.8, 68.6, 76.0, -141.4, -22.8, 13.2, -77.9, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                80.5, -75.1, -4.7, 45.3, 13.9, 10.4, 1.7, 4.9, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                24.4, 8.1, -14.5, -5.6, -19.3, 11.5, 10.9, -14.1, -3.7, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                5.4, 9.4, 3.4, -5.2, 3.1, -12.4, -0.7, 8.4, -8.5, -10.1, 0.0, 0.0, 0.0
                        },
                        {
                                -2.0, -6.3, 0.9, -1.1, -0.2, 2.5, -0.3, 2.2, 3.1, -1.0, -2.8, 0.0, 0.0
                        },
                        {
                                3.0, -1.5, -2.1, 1.7, -0.5, 0.5, -0.8, 0.4, 1.8, 0.1, 0.7, 3.8, 0.0
                        },
                        {
                                -2.2, -0.2, 0.3, 1.0, -0.6, 0.9, -0.1, 0.5, -0.4, -0.4, 0.2, -0.8, 0.0
                        }
                };

        double[][] gdot =
                {
                        {
                                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                11.6, 16.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                -12.1, -4.4, 1.9, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.4, -4.1, -2.9, -7.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                -1.8, 2.3, -8.7, 4.6, -2.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                -1.0, 0.6, -1.8, -1.0, 0.9, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                -0.2, -0.2, -0.1, 2.0, -1.7, -0.3, 1.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.1, -0.1, -0.6, 1.3, 0.4, 0.3, -0.7, 0.6, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                -0.1, 0.1, -0.6, 0.2, -0.2, 0.3, 0.3, -0.6, 0.2, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, -0.1, 0.0, 0.3, -0.4, -0.3, 0.1, -0.1, -0.4, -0.2, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, 0.0, -0.1, 0.2, 0.0, -0.1, -0.2, 0.0, -0.1, -0.2, -0.2, 0.0, 0.0
                        },
                        {
                                0.0, 0.0, 0.0, 0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.1, 0.0, 0.0
                        },
                        {
                                0.0, 0.0, 0.1, 0.1, -0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.1, 0.1
                        }
                };

        double[][] h =
                {
                        {
                                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, 4944.4, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, -2707.7, -576.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, -160.2, 251.9, -536.6, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, 286.4, -211.2, 164.3, -309.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, 44.6, 188.9, -118.2, 0.0, 100.9, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, -20.8, 44.1, 61.5, -66.3, 3.1, 55.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, -57.9, -21.1, 6.5, 24.9, 7.0, -27.7, -3.3, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, 11.0, -20.0, 11.9, -17.4, 16.7, 7.0, -10.8, 1.7, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, -20.5, 11.5, 12.8, -7.2, -7.4, 8.0, 2.1, -6.1, 7.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, 2.8, -0.1, 4.7, 4.4, -7.2, -1.0, -3.9, -2.0, -2.0, -8.3, 0.0, 0.0
                        },
                        {
                                0.0, 0.2, 1.7, -0.6, -1.8, 0.9, -0.4, -2.5, -1.3, -2.1, -1.9, -1.8, 0.0
                        },
                        {
                                0.0, -0.9, 0.3, 2.1, -2.5, 0.5, 0.6, 0.0, 0.1, 0.3, -0.9, -0.2, 0.9
                        }
                };

        double[][] hdot =
                {
                        {
                                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, -25.9, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, -22.5, -11.8, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, 7.3, -3.9, -2.6, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, 1.1, 2.7, 3.9, -0.8, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, 0.4, 1.8, 1.2, 4.0, -0.6, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, -0.2, -2.1, -0.4, -0.6, 0.5, 0.9, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, 0.7, 0.3, -0.1, -0.1, -0.8, -0.3, 0.3, 0.0, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, -0.1, 0.2, 0.4, 0.4, 0.1, -0.1, 0.4, 0.3, 0.0, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, 0.0, -0.2, 0.0, -0.1, 0.1, 0.0, -0.2, 0.3, 0.2, 0.0, 0.0, 0.0
                        },
                        {
                                0.0, 0.1, -0.1, 0.0, -0.1, -0.1, 0.0, -0.1, -0.2, 0.0, -0.1, 0.0, 0.0
                        },
                        {
                                0.0, 0.0, 0.1, 0.0, 0.1, 0.0, 0.1, 0.0, -0.1, -0.1, 0.0, -0.1, 0.0
                        },
                        {
                                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                        }
                };


        // EQB: What is this logic about?
        if (year >= 2010 && year <= 2015) {
            time = year + month / 12.0 + day / 31.0;
        } else {
            time = 2015.0;
        }

        /* Avoid singularity at poles  */
        if (latitude == 90.0) {
            latitude = 89.99;
        } else {
            if (latitude == -90.0) {
                latitude = -89.99;
            }
        }

        /* Convert geodetic to geocentric */
        lat_rad = latitude * PI / 180.0;
        long_rad = longitude * PI / 180.0;

        Qa = earth_a * earth_a * cos(lat_rad) * cos(lat_rad);
        Qb = earth_b * earth_b * sin(lat_rad) * sin(lat_rad);

        lat0_rad = atan2((tan(lat_rad) * ((earth_b * earth_b) + (elevation * sqrt(Qa + Qb)))),
                ((earth_a * earth_a) + (elevation * sqrt(Qa + Qb))));

        earth_R = sqrt((elevation * elevation) + (2.0 * elevation * sqrt(Qa + Qb)) +
                ((earth_a * earth_a * Qa) + (earth_b * earth_b * Qb)) / (Qa + Qb));

        colat_rad = PI / 2.0 - lat0_rad;  // geocentric colatitude;


        // EQB: what are m, n, K, S, P, & dP?

        /* Set K=S=P=dP=0 when m>n */
        for (n = 0; n <= nMax; n++) {
            for (m = nMax; m > n; m--) {
                K[n][m] = 0.0;
                S[n][m] = 0.0;
                P[n][m] = 0.0;
                dP[n][m] = 0.0;
            }
        }


        /* Initialize */
        K[0][0] = 0.0;
        K[1][0] = 0.0;
        K[1][1] = 0.0;

        S[0][0] = 1.0;

        P[0][0] = 1.0;
        P[1][0] = cos(colat_rad);
        P[1][1] = sin(colat_rad);

        dP[0][0] = 0.0;
        dP[1][0] = -sin(colat_rad);
        dP[1][1] = cos(colat_rad);


        /* Calculate polynomial coefficients for m<=n  */
        for (n = 2; n <= nMax; n++) {
            for (m = 0; m <= n; m++) {
                K[n][m] = ((n - 1.0) * (n - 1.0) - (m * m)) /
                        (((2.0 * n) - 1.0) * ((2.0 * n) - 3.0));


                if (m == n) {
                    P[n][m] = sin(colat_rad) * P[n - 1][m - 1];
                    dP[n][m] = sin(colat_rad) * dP[n - 1][m - 1] + cos(colat_rad) * P[n - 1][m - 1];
                } else {
                    P[n][m] = cos(colat_rad) * P[n - 1][m] - K[n][m] * P[n - 2][m];
                    dP[n][m] = cos(colat_rad) * dP[n - 1][m] - sin(colat_rad) * P[n - 1][m] - K[n][m] * dP[n - 2][m];
                }
            }
        }


        /* Calculate normalization factors  */
        for (n = 1; n <= nMax; n++) {
            S[n][0] = S[n - 1][0] * ((2.0 * (double) n) - 1.0) / (double) n;
        }


        for (n = 1; n <= nMax; n++) {
            for (m = 1; m <= n; m++) {
                if (m == 1) {
                    S[n][m] = S[n][m - 1] * sqrt(2.0 * ((double) n - (double) m + 1.0) / ((double) n + (double) m));
                } else {
                    S[n][m] = S[n][m - 1] * sqrt(((double) n - (double) m + 1.0) / ((double) n + (double) m));
                }
            }
        }


        /* normalize coefficients  */
        for (n = 1; n <= nMax; n++) {
            for (m = 0; m <= n; m++) {
                gSt[n][m] = S[n][m] * (g[n][m] + gdot[n][m] * (time - 2010.0));
                hSt[n][m] = S[n][m] * (h[n][m] + hdot[n][m] * (time - 2010.0));
            }
        }


        /* Calculate field vector components  */

        X_prime = 0.0;
        Y_prime = 0.0;
        Z_prime = 0.0;

        for (n = 1; n <= nMax; n++) {
            X_m = 0.0;
            Y_m = 0.0;
            Z_m = 0.0;

            for (m = 0; m <= n; m++) {
                X_m += (gSt[n][m] * cos(m * long_rad) + hSt[n][m] * sin(m * long_rad)) * dP[n][m];
                Y_m += (gSt[n][m] * sin(m * long_rad) - hSt[n][m] * cos(m * long_rad)) * m * P[n][m];
                Z_m += (gSt[n][m] * cos(m * long_rad) + hSt[n][m] * sin(m * long_rad)) * P[n][m];
            }

            X_prime += -pow((r_E / earth_R), ((double) n + 2.0)) * X_m;
            Y_prime += pow((r_E / earth_R), ((double) n + 2.0)) * Y_m / sin(colat_rad);
            Z_prime += pow((r_E / earth_R), ((double) n + 2.0)) * Z_m * ((double) n + 1.0);
        }

        /* Transform and calculate parameters */
        X_vector = -cos(lat_rad - lat0_rad) * X_prime - sin(lat_rad - lat0_rad) * Z_prime;
        Y_vector = Y_prime;
        Z_vector = sin(lat_rad - lat0_rad) * X_prime - cos(lat_rad - lat0_rad) * Z_prime;

        mag_H = sqrt(X_vector * X_vector + Y_vector * Y_vector);    // horizontal intensity
        mag_F = sqrt(mag_H * mag_H + Z_vector * Z_vector);          // total intensity
        mag_I = atan2(Z_vector, mag_H) * 180.0 / PI;                // inclination (dip) angle, measured from horizontal plane, + downwards
        mag_D = atan2(Y_vector, X_vector) * 180.0 / PI;              // declination angle (magnetic variation), measured CW from true north


        /*
        out.println(format("latitude = %f", latitude));
        out.println(format("latitude_rad = %f", lat_rad));
        out.println(format("latitude0_rad = %f", lat0_rad));
        out.println(format("colatitude_rad = %f\n", colat_rad));
        out.println(format("lat(rad): %f", lat_rad));
        out.println(format("horizontal intensity = %f", mag_H));
        out.println(format("total intensity = %f", mag_F));
        out.println(format("inclination angle =  %f", mag_I));
        out.println(format("declination angle =  %f", mag_D));
        */

        MagneticDeclension info = new MagneticDeclension(year, month, day, latitude, longitude, elevation);
        info.setDeclension(mag_D);
        info.setInclination(mag_I);
        info.setHorizontalIntensity(mag_H);
        info.setTotalIntensity(mag_F);

        return info;
    }
}