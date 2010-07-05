package com.oneau.web;

import com.oneau.web.util.Constants;
import com.oneau.web.util.MagneticDeclension;
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
import static com.oneau.web.util.Utility.isEmpty;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * User: ebridges
 * Date: Jul 5, 2010
 */
public class MagneticDeclensionServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(MagneticDeclensionServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        if (logger.isDebugEnabled()) {
            logger.debug("creating instance of magnetic declension calculator.");
        }
        TrueNorth trueNorthCalculator = new TrueNorth();
        this.getServletContext().setAttribute(Constants.DECLENSION_CALCULATOR, trueNorthCalculator);
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("doGet() called.");

        View view = ViewFactory.view(chooseView(request.getParameter(RESPONSE_CONTENT_TYPE_PARAM)));
        response.setContentType(view.getMimeType());
        TrueNorth trueNorthCalculator = (TrueNorth) this.getServletContext().getAttribute(Constants.DECLENSION_CALCULATOR);

        PrintWriter writer = response.getWriter();
        try {
            int year = parseInt(request.getParameter(Constants.YEAR_PARAM));
            int month = parseInt(request.getParameter(Constants.MONTH_PARAM));
            int day = parseInt(request.getParameter(Constants.DAY_PARAM));
            double lon = parseDouble(request.getParameter(Constants.LONGITUDE_PARAM));
            double lat = parseDouble(request.getParameter(Constants.LATITUDE_PARAM));
            double el = parseDouble(request.getParameter(Constants.ELEVATION_PARAM));

            MagneticDeclension md = trueNorthCalculator.calculate(year, month, day, lat, lon, el);

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
