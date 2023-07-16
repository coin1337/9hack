package software.bernie.shadowed.fasterxml.jackson.databind.ser.std;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerationException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

public class StdJdkSerializers {
   public static Collection<Entry<Class<?>, Object>> all() {
      HashMap<Class<?>, Object> sers = new HashMap();
      sers.put(URL.class, new ToStringSerializer(URL.class));
      sers.put(URI.class, new ToStringSerializer(URI.class));
      sers.put(Currency.class, new ToStringSerializer(Currency.class));
      sers.put(UUID.class, new UUIDSerializer());
      sers.put(Pattern.class, new ToStringSerializer(Pattern.class));
      sers.put(Locale.class, new ToStringSerializer(Locale.class));
      sers.put(AtomicBoolean.class, StdJdkSerializers.AtomicBooleanSerializer.class);
      sers.put(AtomicInteger.class, StdJdkSerializers.AtomicIntegerSerializer.class);
      sers.put(AtomicLong.class, StdJdkSerializers.AtomicLongSerializer.class);
      sers.put(File.class, FileSerializer.class);
      sers.put(Class.class, ClassSerializer.class);
      sers.put(Void.class, NullSerializer.instance);
      sers.put(Void.TYPE, NullSerializer.instance);

      try {
         sers.put(Timestamp.class, DateSerializer.instance);
         sers.put(Date.class, SqlDateSerializer.class);
         sers.put(Time.class, SqlTimeSerializer.class);
      } catch (NoClassDefFoundError var2) {
      }

      return sers.entrySet();
   }

   public static class AtomicLongSerializer extends StdScalarSerializer<AtomicLong> {
      public AtomicLongSerializer() {
         super(AtomicLong.class, false);
      }

      public void serialize(AtomicLong value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonGenerationException {
         gen.writeNumber(value.get());
      }

      public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
         return this.createSchemaNode("integer", true);
      }

      public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
         this.visitIntFormat(visitor, typeHint, JsonParser.NumberType.LONG);
      }
   }

   public static class AtomicIntegerSerializer extends StdScalarSerializer<AtomicInteger> {
      public AtomicIntegerSerializer() {
         super(AtomicInteger.class, false);
      }

      public void serialize(AtomicInteger value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonGenerationException {
         gen.writeNumber(value.get());
      }

      public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
         return this.createSchemaNode("integer", true);
      }

      public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
         this.visitIntFormat(visitor, typeHint, JsonParser.NumberType.INT);
      }
   }

   public static class AtomicBooleanSerializer extends StdScalarSerializer<AtomicBoolean> {
      public AtomicBooleanSerializer() {
         super(AtomicBoolean.class, false);
      }

      public void serialize(AtomicBoolean value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonGenerationException {
         gen.writeBoolean(value.get());
      }

      public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
         return this.createSchemaNode("boolean", true);
      }

      public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
         visitor.expectBooleanFormat(typeHint);
      }
   }
}
