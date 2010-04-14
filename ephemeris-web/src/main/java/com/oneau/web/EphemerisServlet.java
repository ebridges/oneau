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
import java.util.Map;

import static com.oneau.web.util.Constants.EPHEMERIS_DATA;
import static com.oneau.web.util.Constants.JULIAN_DATE_PARAM;
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

        if(logger.isDebugEnabled()){
            logger.debug(format("creating ephemeris with configured datafile location(s) [%s]", ephemerides));
        }

        Ephemeris ephemeris = new Ephemeris();

        if(!isEmpty(ephemerides)) {
            try {
                String[] dataFiles = ephemerides.split(",");
                if(!isEmpty(dataFiles)) {
                    ephemeris.loadData(dataFiles);
                } else {
                    logger.warn(format("unable to parse list of dataFiles from configured value [%s]", ephemerides));
                }
            } catch (IOException e) {
                throw new ServletException(e);
            }
        }

        this.getServletContext().setAttribute(EPHEMERIS_DATA, ephemeris);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jdate = request.getParameter(JULIAN_DATE_PARAM);
        Double julianDate = Double.valueOf(jdate);
        if(logger.isDebugEnabled()) {
            logger.debug(format("doGet(%s=%s) called.",JULIAN_DATE_PARAM, julianDate));
        }

        Ephemeris ephemeris = (Ephemeris) this.getServletContext().getAttribute(EPHEMERIS_DATA);
        if(logger.isTraceEnabled()) {
            logger.trace("ephemeris located in servletContext.");
        }

        Map<HeavenlyBody, PositionAndVelocity> results = ephemeris.calculatePlanetaryEphemeris(julianDate);
        if(logger.isDebugEnabled()){
            logger.debug(format("retrieved position and velocity for %d bodies.", results.keySet().size()));
        }

        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");

        out.println("bodyId;body;position;velocity");

        for (Map.Entry<HeavenlyBody, PositionAndVelocity> e : results.entrySet()) {
            String position = toCsv(e.getValue().getPosition());
            String velocity = toCsv(e.getValue().getVelocity());
            if(logger.isTraceEnabled()){
                logger.trace(format("sending result for body [%s]", e.getKey().getName()));
            }
            out.println(format("%d;%s;%s;%s",
                    e.getKey().getIndex(),
                    e.getKey().getName(),
                    position,
                    velocity
                )
            );
        }
    }
}
