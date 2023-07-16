package software.bernie.geckolib3.geo.raw.pojo;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JsonDeserialize;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(
   using = LocatorValue.Deserializer.class
)
@JsonSerialize(
   using = LocatorValue.Serializer.class
)
public class LocatorValue {
   public LocatorClass locatorClassValue;
   public double[] doubleArrayValue;

   static class Serializer extends JsonSerializer<LocatorValue> {
      public void serialize(LocatorValue obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
         if (obj.locatorClassValue != null) {
            jsonGenerator.writeObject(obj.locatorClassValue);
         } else if (obj.doubleArrayValue != null) {
            jsonGenerator.writeObject(obj.doubleArrayValue);
         } else {
            throw new IOException("LocatorValue must not be null");
         }
      }
   }

   static class Deserializer extends JsonDeserializer<LocatorValue> {
      public LocatorValue deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
         LocatorValue value = new LocatorValue();
         switch(jsonParser.getCurrentToken()) {
         case START_ARRAY:
            value.doubleArrayValue = (double[])jsonParser.readValueAs(double[].class);
            break;
         case START_OBJECT:
            value.locatorClassValue = (LocatorClass)jsonParser.readValueAs(LocatorClass.class);
            break;
         default:
            throw new IOException("Cannot deserialize LocatorValue");
         }

         return value;
      }
   }
}
