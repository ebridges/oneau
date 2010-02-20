package com.oneau.web;

import com.oneau.web.util.HeavenlyBody;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.oneau.web.util.Constants.EPHEMERIS_DATA;
import static com.oneau.web.util.Constants.JULIAN_DATE_PARAM;
import static com.oneau.web.util.HeavenlyBody.values;
import static com.oneau.web.util.Utility.isEmpty;
import static com.oneau.web.util.Utility.toCsv;
import static java.lang.String.format;

/**
 * User: ebridges
 * Date: Feb 15, 2010
 */
public class EphemerisServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(EphemerisServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String ephemerides = config.getInitParameter(EPHEMERIS_DATA);
        if(isEmpty(ephemerides)) {
            throw new ServletException("Ephemeris data file not configured properly.");
        }

        if(logger.isDebugEnabled()){
            logger.debug(format("creating ephemeris with configured datafile location: %s", ephemerides));
        }

        Ephemeris ephemeris = new Ephemeris(ephemerides);
        this.getServletContext().setAttribute(EPHEMERIS_DATA, ephemeris);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jdate = request.getParameter(JULIAN_DATE_PARAM);
        Double julianDate = Double.valueOf(jdate);

        Ephemeris ephemeris = (Ephemeris) this.getServletContext().getAttribute(EPHEMERIS_DATA);

        ephemeris.planetary_ephemeris(julianDate);

        PrintWriter out = response.getWriter();

        response.setContentType("text/plain");

        /*  The following simply sends the output to the screen */
        out.println("bodyId;body;position;velocity");

        for (HeavenlyBody b : values()) {
            String position = toCsv(ephemeris.planet_r[b.getIndex()]);
            String velocity = toCsv(ephemeris.planet_rprime[b.getIndex()]);

            out.println(format("%d;%s;%s;%s",
                    b.getIndex(),
                    b.getName(),
                    position,
                    velocity
                )
            );
        }
    }
}
