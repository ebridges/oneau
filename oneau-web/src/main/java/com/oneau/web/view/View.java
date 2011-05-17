package com.oneau.web.view;

import com.oneau.core.util.HeavenlyBody;
import com.oneau.core.util.PositionAndVelocity;
import com.oneau.web.util.MagneticDeclination;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * User: EBridges
 * Created: 2010-04-15
 */
public interface View {
    String getMimeType();

    void writeModel(Writer writer, Map<HeavenlyBody, PositionAndVelocity> model) throws IOException;

    void writeModel(Writer writer, MagneticDeclination decl) throws IOException;

    void writeError(Writer writer, Throwable error);
}
