package com.oneau.web;

import com.oneau.web.util.Constants;
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

import static com.oneau.web.util.Constants.RESPONSE_CONTENT_TYPE_PARAM;
import static com.oneau.web.util.Utility.convertInteger;
import static com.oneau.web.util.Utility.convertDouble;
import static com.oneau.web.util.Utility.isEmpty;

/**
 * User: ebridges
 * Date: Jul 5, 2010
 */
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
            int year = convertInteger(request.getParameter(Constants.YEAR_PARAM));
            int month = convertInteger(request.getParameter(Constants.MONTH_PARAM));
            int day = convertInteger(request.getParameter(Constants.DAY_PARAM));
            double lon = convertDouble(request.getParameter(Constants.LONGITUDE_PARAM));
            double lat = convertDouble(request.getParameter(Constants.LATITUDE_PARAM));
            double el = convertDouble(request.getParameter(Constants.ELEVATION_PARAM));

            MagneticDeclination md = trueNorthCalculator.calculate(year, month, day, lat, lon, el);

            view.writeModel(writer, md);
        } finally {
            writer.flush();
        }
    }

    private ViewFactory.ViewType chooseView(String parameter) {
        if (isEmpty(parameter)) {
            return ViewFactory.ViewType.TEXT;
        } else {
            return ViewFactory.ViewType.valueOf(parameter.trim().toUpperCase());
        }
    }

}
