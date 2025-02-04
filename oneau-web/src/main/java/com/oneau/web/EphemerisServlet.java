package com.oneau.web;

import com.oneau.core.Ephemeris;
import com.oneau.core.util.Constants;
import com.oneau.core.util.Converter;
import com.oneau.core.util.ConverterFactory;
import com.oneau.core.util.HeavenlyBody;
import com.oneau.core.util.PositionAndVelocity;
import com.oneau.core.util.Utility;
import com.oneau.data.DAOFactory;
import com.oneau.data.EphemerisLookupError;
import com.oneau.web.view.View;
import com.oneau.web.view.ViewFactory;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.oneau.core.util.Constants.BODY_NAME_PARAM;
import static com.oneau.core.util.Constants.DATE_PARAM;
import static com.oneau.core.util.Constants.DAY_PARAM;
import static com.oneau.core.util.Constants.EPHEMERIS_DATA;
import static com.oneau.core.util.Constants.HOUR_PARAM;
import static com.oneau.core.util.Constants.JULIAN_DATE_PARAM;
import static com.oneau.core.util.Constants.MIN_PARAM;
import static com.oneau.core.util.Constants.MONTH_PARAM;
import static com.oneau.core.util.Constants.RESPONSE_CONTENT_TYPE_PARAM;
import static com.oneau.core.util.Constants.RESPONSE_UNITS_PARAM;
import static com.oneau.core.util.Constants.SEC_PARAM;
import static com.oneau.core.util.Constants.YEAR_PARAM;
import static com.oneau.core.util.Utility.isEmpty;
import static com.oneau.core.util.Utility.toHeavenlyBody;
import static com.oneau.core.util.Utility.toJulianDay;
import static java.lang.Double.valueOf;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * User: ebridges
 * Date: Feb 15, 2010
 */
@SuppressWarnings("serial")
public class EphemerisServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(EphemerisServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        //String ephemerides = config.getInitParameter(EPHEMERIS_DATA);

        String jdbcUrl = config.getInitParameter(Constants.JDBC_URL_KEY);
        String jdbcUsername = config.getInitParameter(Constants.JDBC_USERNAME_KEY);
        String jdbcPassword = config.getInitParameter(Constants.JDBC_PASSWORD_KEY);
        String jdbcDriver = config.getInitParameter(Constants.JDBC_DRIVER_KEY);

        if (logger.isDebugEnabled()) {
            logger.debug(format("building database connection to (%s)", jdbcUrl));
        }

        DAOFactory factory = DAOFactory.instance(
                jdbcUrl,
                jdbcUsername,
                jdbcPassword,
                jdbcDriver
        );

        Ephemeris ephemeris = new Ephemeris(factory.getEphemerisDAO());

        /*
        if (!isEmpty(ephemerides)) {
            try {
                String[] dataFiles = ephemerides.split(",");
                if (!isEmpty(dataFiles)) {
                    ephemeris.loadData(dataFiles);
                } else {
                    logger.warn(format("unable to parse list of dataFiles from configured value [%s]", ephemerides));
                }
            } catch (IOException e) {
                throw new ServletException(e);
            }
        }
        */

        this.getServletContext().setAttribute(EPHEMERIS_DATA, ephemeris);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Ephemeris ephemeris = (Ephemeris) this.getServletContext().getAttribute(EPHEMERIS_DATA);
        if (logger.isTraceEnabled()) {
            logger.trace("ephemeris located in servletContext.");
        }
        View view = ViewFactory.view(chooseView(request.getParameter(RESPONSE_CONTENT_TYPE_PARAM)));
        response.setContentType(view.getMimeType());
        HeavenlyBody[] bodies = toHeavenlyBody(request.getParameterValues(BODY_NAME_PARAM));
        Converter[] converters = getConvertersFromRequest(request);
        logger.debug("Loaded converters from request: " + Arrays.toString(converters));

        PrintWriter writer = response.getWriter();
        try {
            List<Double> julianDates = getJulianDateFromRequest(request);
            for (Double julianDate : julianDates) {
                Map<HeavenlyBody, PositionAndVelocity> model = ephemeris.calculatePlanetaryEphemeris(julianDate, bodies);
                if (logger.isDebugEnabled()) {
                    logger.debug(format("retrieved position and velocity for %d bodies for date %f.", model.keySet().size(), julianDate));
                }
                view.writeModel(writer, model);
            }
        } catch(Throwable e) {
			view.writeError(writer, e);
        } finally {
            writer.flush();
        }
    }

    private Converter[] getConvertersFromRequest(HttpServletRequest request) {
        String[] converterNames = request.getParameterValues(RESPONSE_UNITS_PARAM);
        if (!isEmpty(converterNames)) {
            Set<Converter> converters = new HashSet<Converter>();
 //           int i = 0;
            for (String name : converterNames) {
                Converter.TYPE type = null;
                try {
                    type = Converter.TYPE.valueOf(name);
                } catch (IllegalArgumentException e) {
                    logger.warn("got invalid converter type: [" + name + "]");
                    continue;
                }
                if (null != type)
                    converters.add(ConverterFactory.getConverter(type));
            }
            return converters.toArray(new Converter[converters.size()]);
        } else {
            Converter KM = ConverterFactory.getConverter(Converter.TYPE.KM);
            return new Converter[]{KM};
        }
    }

    private ViewFactory.ViewType chooseView(String parameter) {
        if (isEmpty(parameter)) {
            return ViewFactory.ViewType.TEXT;
        } else {
            return ViewFactory.ViewType.valueOf(parameter.trim().toUpperCase());
        }
    }

    private List<Double> getJulianDateFromRequest(HttpServletRequest request) {
        if (!isEmpty(request.getParameterValues(JULIAN_DATE_PARAM))) {
            String[] dates = request.getParameterValues(JULIAN_DATE_PARAM);
            List<Double> julianDates = new LinkedList<Double>();
            for (String date : dates) {
                Double julianDate = valueOf(date);
                if (logger.isInfoEnabled()) {
                    logger.info(format("queryDate: [%s/%s]", date, julianDate));
                }
                julianDates.add(julianDate);
            }
            return unmodifiableList(julianDates);
        } else if (!isEmpty(request.getParameterValues(DATE_PARAM))) {
            String[] isoDates = request.getParameterValues(DATE_PARAM);
            List<Double> julianDates = new LinkedList<Double>();
            for (String isoDate : isoDates) {
                Double[] dateFields = Utility.parseDate(isoDate);
                Double julianDate = toJulianDay(
                        dateFields[0],
                        dateFields[1],
                        dateFields[2],
                        dateFields[3],
                        dateFields[4],
                        dateFields[5],
                        0.0
                );
                if (logger.isInfoEnabled()) {
                    logger.info(format("queryDate: [%s/%s]", isoDate, julianDate));
                }
                julianDates.add(julianDate);
            }
            return unmodifiableList(julianDates);
        } else if (!isEmpty(request.getParameter(MONTH_PARAM))) {
            String MO = request.getParameter(MONTH_PARAM);
            String D = request.getParameter(DAY_PARAM);
            String Y = request.getParameter(YEAR_PARAM);
            String H = request.getParameter(HOUR_PARAM);
            String MI = request.getParameter(MIN_PARAM);
            String S = request.getParameter(SEC_PARAM);
            String m = "000";
            Double julianDate = toJulianDay(
                    valueOf(Y),
                    valueOf(MO),
                    valueOf(D),
                    valueOf(H),
                    valueOf(MI),
                    valueOf(S),
                    0.0
            );
            if (logger.isInfoEnabled()) {
                String date = format("%s-%s-%sT%s:%s:%s.%sZ", Y, MO, D, H, MI, S, m);
                logger.info(format("queryDate: [%s/%s]", date, julianDate));
            }
            return unmodifiableList(asList(julianDate));
        } else {
            Double julianDate = toJulianDay();
            if (logger.isInfoEnabled()) {
                logger.info(format("queryDate default to today: [%s/%s]", new Date(), julianDate));
            }
            return unmodifiableList(asList(julianDate));
        }
    }

}
