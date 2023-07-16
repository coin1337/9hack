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
   using = PolysUnion.Deserializer.class
)
@JsonSerialize(
   using = PolysUnion.Serializer.class
)
public class PolysUnion {
   public double[][][] doubleArrayArrayArrayValue;
   public PolysEnum enumValue;

   static class Serializer extends JsonSerializer<PolysUnion> {
      public void serialize(PolysUnion obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
         if (obj.doubleArrayArrayArrayValue != null) {
            jsonGenerator.writeObject(obj.doubleArrayArrayArrayValue);
         } else if (obj.enumValue != null) {
            jsonGenerator.writeObject(obj.enumValue);
         } else {
            throw new IOException("PolysUnion must not be null");
         }
      }
   }

   static class Deserializer extends JsonDeserializer<PolysUnion> {
      public PolysUnion deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
         PolysUnion value = new PolysUnion();
         switch(jsonParser.currentToken()) {
         case VALUE_STRING:
            String string = (String)jsonParser.readValueAs(String.class);

            try {
               value.enumValue = PolysEnum.forValue(string);
            } catch (Exception var6) {
            }
            break;
         case START_ARRAY:
            value.doubleArrayArrayArrayValue = (double[][][])jsonParser.readValueAs(double[][][].class);
            break;
         default:
            throw new IOException("Cannot deserialize PolysUnion");
         }

         return value;
      }
   }
}
