package com.oneau.web;

import com.oneau.core.util.Constants;
import com.oneau.web.util.MagneticDeclination;
import com.oneau.web.util.TrueNorth;
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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.oneau.core.util.Constants.RESPONSE_CONTENT_TYPE_PARAM;
import static com.oneau.core.util.Utility.convertDouble;
import static com.oneau.core.util.Utility.convertInteger;
import static com.oneau.core.util.Utility.isEmpty;

/**
 * User: ebridges
 * Date: Jul 5, 2010
 */
@SuppressWarnings("serial")
public class MagneticDeclinationServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(MagneticDeclinationServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        if (logger.isDebugEnabled()) {
            logger.debug("creating instance of magnetic declination calculator.");
        }
        TrueNorth trueNorthCalculator = new TrueNorth();
        this.getServletContext().setAttribute(Constants.DECLINATION_CALCULATOR, trueNorthCalculator);
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("doGet() called.");

        View view = ViewFactory.view(chooseView(request.getParameter(RESPONSE_CONTENT_TYPE_PARAM)));
        response.setContentType(view.getMimeType());
        TrueNorth trueNorthCalculator = (TrueNorth) this.getServletContext().getAttribute(Constants.DECLINATION_CALCULATOR);

        PrintWriter writer = response.getWriter();
        try {
            int year = thisYearIfZero(convertInteger(request.getParameter(Constants.YEAR_PARAM)));
            int month = thisMonthIfZero(convertInteger(request.getParameter(Constants.MONTH_PARAM)));
            int day = thisDayIfZero(convertInteger(request.getParameter(Constants.DAY_PARAM)));

            double lon = convertDouble(request.getParameter(Constants.LONGITUDE_PARAM));
            double lat = convertDouble(request.getParameter(Constants.LATITUDE_PARAM));
            double el = convertDouble(request.getParameter(Constants.ELEVATION_PARAM));

            MagneticDeclination md = trueNorthCalculator.calculate(year, month, day, lat, lon, el);

            view.writeModel(writer, md);
        } catch(Throwable e) {
			view.writeError(writer, e);
        } finally {
            writer.flush();
        }
    }

    private int thisDayIfZero(Integer day) {
        if(0 == day) {
            return getDateField(Calendar.DAY_OF_MONTH);
        } else {
            return day;
        }
    }

    private int thisMonthIfZero(Integer month) {
        if(0 == month) {
            return getDateField(Calendar.MONTH)+1;
        } else {
            return month;
        }
    }

    private int thisYearIfZero(Integer year) {
        if(0 == year) {
            return getDateField(Calendar.YEAR);
        } else {
            return year;
        }
    }

    private int getDateField(int field) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.setTime(new Date());
        return c.get(field);
    }

    private ViewFactory.ViewType chooseView(String parameter) {
        if (isEmpty(parameter)) {
            return ViewFactory.ViewType.TEXT;
        } else {
            return ViewFactory.ViewType.valueOf(parameter.trim().toUpperCase());
        }
    }

}
