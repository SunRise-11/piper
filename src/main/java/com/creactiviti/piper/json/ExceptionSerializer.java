package com.creactiviti.piper.json;

import java.io.IOException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings("serial")
public class ExceptionSerializer extends StdSerializer<Throwable> {

  public ExceptionSerializer() {
    super(Throwable.class);
  }

  @Override
  public void serialize(Throwable aException, JsonGenerator aGen, SerializerProvider aProvider) throws IOException {
    aGen.writeStartObject();
    aGen.writeStringField("message", aException.getMessage());
    aGen.writeObjectField("stackTrace", ExceptionUtils.getStackTrace(aException));
    aGen.writeEndObject();
  }

}
