package com.cryptoevent.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.text.StringEscapeUtils;


import java.io.IOException;

public class SanitizeAndSerializeString  extends StdSerializer<String> {

    public SanitizeAndSerializeString() {
        this(null);
    }

    public SanitizeAndSerializeString(Class<String> t) {
        super(t);
    }

    @Override
    public void serialize(
            String value, JsonGenerator gen, SerializerProvider arg2)
            throws IOException {
        gen.writeString(StringEscapeUtils.escapeHtml4(value));
    }
}