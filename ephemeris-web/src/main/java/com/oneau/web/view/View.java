package com.oneau.web.view;

import com.oneau.web.util.HeavenlyBody;
import com.oneau.web.PositionAndVelocity;

import java.util.Map;
import java.io.Writer;
import java.io.IOException;

/**
 * User: EBridges
 * Created: 2010-04-15
 */
public interface View {
    String getMimeType();
    void writeModel(Writer writer, Map<HeavenlyBody, PositionAndVelocity> model) throws IOException;
}
