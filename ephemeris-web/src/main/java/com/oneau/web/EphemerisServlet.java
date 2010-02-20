package com.oneau.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * User: ebridges
 * Date: Feb 15, 2010
 */
public class EphemerisServlet extends HttpServlet {
    private Map<Integer,String> heavenlyBodies = new HashMap<Integer,String>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String ephemerides = config.getInitParameter("ephemerides");
        if(null == ephemerides || ephemerides.length() < 1) {
            throw new ServletException("Ephemeris data file not configured properly.");
        }
        System.out.println(format("creating ephemeris with configured datafile location: %s", ephemerides));
        Ephemeris ephemeris = new Ephemeris(ephemerides);
        this.getServletContext().setAttribute("ephemeris", ephemeris);


        heavenlyBodies.put(1,"Mercury");
        heavenlyBodies.put(2,"Venus");
        heavenlyBodies.put(3,"Earth/Moon (Barycentric)");
        heavenlyBodies.put(4,"Mars");
        heavenlyBodies.put(5,"Jupiter");
        heavenlyBodies.put(6,"Saturn");
        heavenlyBodies.put(7,"Uranus");
        heavenlyBodies.put(8,"Neptune");
        heavenlyBodies.put(9,"Pluto");
        heavenlyBodies.put(10,"Moon (geocentric)");
        heavenlyBodies.put(11,"Sun");
        
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jdate = request.getParameter("julianDate");
        Double julianDate = Double.valueOf(jdate);

        Ephemeris ephemeris = (Ephemeris) this.getServletContext().getAttribute("ephemeris");

        ephemeris.planetary_ephemeris(julianDate);

        PrintWriter out = response.getWriter();

        response.setContentType("text/plain");
        /*  The following simply sends the output to the screen */
        out.println("bodyId;body;position;velocity");
        for (int i = 1; i <= 11; i++) {

            String position = toCsv(ephemeris.planet_r[i]);
            String velocity = toCsv(ephemeris.planet_rprime[i]);

            out.println(format("%d;%s;%s;%s", i, heavenlyBodies.get(i), position, velocity));
        }

    }

    private String toCsv(double[] o) {
        StringBuilder sb = new StringBuilder(256);
        for(int i=1; i<=3; i++) {
            if(i>1) {
                sb.append(',');
            }
            sb.append(o[i]);
        }
        return sb.toString();
    }
}
