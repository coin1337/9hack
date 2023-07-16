package software.bernie.shadowed.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

@JacksonStdImpl
public class NumberSerializer extends StdScalarSerializer<Number> {
   public static final NumberSerializer instance = new NumberSerializer(Number.class);
   protected final boolean _isInt;

   public NumberSerializer(Class<? extends Number> rawType) {
      super(rawType, false);
      this._isInt = rawType == BigInteger.class;
   }

   public void serialize(Number value, JsonGenerator g, SerializerProvider provider) throws IOException {
      if (value instanceof BigDecimal) {
         g.writeNumber((BigDecimal)value);
      } else if (value instanceof BigInteger) {
         g.writeNumber((BigInteger)value);
      } else if (value instanceof Long) {
         g.writeNumber(value.longValue());
      } else if (value instanceof Double) {
         g.writeNumber(value.doubleValue());
      } else if (value instanceof Float) {
         g.writeNumber(value.floatValue());
      } else if (!(value instanceof Integer) && !(value instanceof Byte) && !(value instanceof Short)) {
         g.writeNumber(value.toString());
      } else {
         g.writeNumber(value.intValue());
      }

   }

   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
      return this.createSchemaNode(this._isInt ? "integer" : "number", true);
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      if (this._isInt) {
         this.visitIntFormat(visitor, typeHint, JsonParser.NumberType.BIG_INTEGER);
      } else {
         Class<?> h = this.handledType();
         if (h == BigDecimal.class) {
            this.visitFloatFormat(visitor, typeHint, JsonParser.NumberType.BIG_DECIMAL);
         } else {
            visitor.expectNumberFormat(typeHint);
         }
      }

   }
}
