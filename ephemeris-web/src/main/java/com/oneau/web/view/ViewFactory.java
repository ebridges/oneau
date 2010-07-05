package com.oneau.web.view;

import com.oneau.web.util.HeavenlyBody;
import static com.oneau.web.util.Utility.toCsv;
import com.oneau.web.PositionAndVelocity;
import com.oneau.web.util.MagneticDeclension;
import org.apache.log4j.Logger;

import static java.lang.String.format;
import java.io.Writer;
import java.io.IOException;
import java.util.Map;

/**
 * User: EBridges
 * Created: 2010-04-15
 */
public class ViewFactory {
    private static final Logger logger = Logger.getLogger(ViewFactory.class);

    public enum ViewType {
        TEXT(com.oneau.web.view.TextView.class),
        JSON(com.oneau.web.view.JsonView.class);

        private Class<? extends View> implementation;

        ViewType(Class<? extends View> implementation) {
            this.implementation = implementation;
        }
    }

    public static View view(ViewFactory.ViewType type) {
        if (logger.isTraceEnabled()) {
            logger.trace(format("view(%s) called.", type));
        }
        try {
            return type.implementation.newInstance();
        } catch (IllegalArgumentException e) {
            logger.warn(format("invalid view type provided [%s]", type));
            return null;
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}

class JsonView implements View {
    private static final Logger logger = Logger.getLogger(JsonView.class);
    public String getMimeType() {
        return "text/json";
    }

    public void writeModel(Writer writer, MagneticDeclension decl) throws IOException {
        writer.write(format("{\"date\" : \"%d/%d/%d\", \"position\":[\"%f\",\"%f\",\"%f\"], \"declension\" : \"%f\", \"inclination\" : \"%f\", \"totalIntensity\" : \"%f\", \"horizontalIntensity\" : \"%f\" }\n",
                decl.getYear(),
                decl.getMonth(),
                decl.getDay(),
                decl.getLatitude(),
                decl.getLongitude(),
                decl.getElevation(),
                decl.getDeclension(),
                decl.getInclination(),
                decl.getTotalIntensity(),
                decl.getHorizontalIntensity()
            )
        );
    }

    public void writeModel(Writer writer, Map<HeavenlyBody, PositionAndVelocity> model) throws IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("writeModel() called.");
        }

        writer.write("[");
        boolean isFirst = true;
        for (Map.Entry<HeavenlyBody, PositionAndVelocity> e : model.entrySet()) {
            if(isFirst) {
                isFirst = false;
            } else {
                writer.write(',');
            }
            if(logger.isTraceEnabled()){
                logger.trace(format("sending result as json for body [%s]", e.getKey().getName()));
            }
            writer.write(
                format(
                    "{\"bodyId\":\"%s\", \"bodyName\":\"%s\", \"ephemerisDate\":\"%s\", \"position\":[\"%s\",\"%s\",\"%s\"], \"velocity\":[\"%s\",\"%s\",\"%s\"]}",
                    e.getKey().getId(),
                    e.getKey().getName(),
                    e.getValue().getEphemerisDate(),
                    e.getValue().getPosition()[0],
                    e.getValue().getPosition()[1],
                    e.getValue().getPosition()[2],
                    e.getValue().getVelocity()[0],
                    e.getValue().getVelocity()[1],
                    e.getValue().getVelocity()[2]
                )
            );
        }
        writer.write("]\n");
    }
}

class TextView implements View {
    private static final Logger logger = Logger.getLogger(TextView.class);
    public String getMimeType() {
        return "text/plain";
    }

    public void writeModel(Writer writer, MagneticDeclension decl) throws IOException {
        writer.write("date;position;declension;inclination;totalIntensity;horizontalIntensity\n");
        writer.write(format("%d/%d/%d;%f,%f,%f;%f;%f;%f;%f\n",
                decl.getYear(),
                decl.getMonth(),
                decl.getDay(),
                decl.getLatitude(),
                decl.getLongitude(),
                decl.getElevation(),
                decl.getDeclension(),
                decl.getInclination(),
                decl.getTotalIntensity(),
                decl.getHorizontalIntensity()
            )
        );
    }

    public void writeModel(Writer writer, Map<HeavenlyBody, PositionAndVelocity> model) throws IOException {
        writer.write("ephemerisDate;bodyId;body;position;velocity\n");
        for (Map.Entry<HeavenlyBody, PositionAndVelocity> e : model.entrySet()) {
            String position = toCsv(e.getValue().getPosition());
            String velocity = toCsv(e.getValue().getVelocity());
            if(logger.isTraceEnabled()){
                logger.trace(format("sending result as text for body [%s]", e.getKey().getName()));
            }
            writer.write(format("%f;%d;%s;%s;%s\n",
                    e.getValue().getEphemerisDate(),
                    e.getKey().getId(),
                    e.getKey().getName(),
                    position,
                    velocity
                )
            );
        }
    }
}