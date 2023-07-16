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
   using = UvUnion.Deserializer.class
)
@JsonSerialize(
   using = UvUnion.Serializer.class
)
public class UvUnion {
   public double[] boxUVCoords;
   public UvFaces faceUV;
   public boolean isBoxUV;

   static class Serializer extends JsonSerializer<UvUnion> {
      public void serialize(UvUnion obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
         if (obj.boxUVCoords != null) {
            jsonGenerator.writeObject(obj.boxUVCoords);
         } else if (obj.faceUV != null) {
            jsonGenerator.writeObject(obj.faceUV);
         } else {
            jsonGenerator.writeNull();
         }
      }
   }

   static class Deserializer extends JsonDeserializer<UvUnion> {
      public UvUnion deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
         UvUnion value = new UvUnion();
         switch(jsonParser.currentToken()) {
         case VALUE_NULL:
            break;
         case START_ARRAY:
            value.boxUVCoords = (double[])jsonParser.readValueAs(double[].class);
            value.isBoxUV = true;
            break;
         case START_OBJECT:
            value.faceUV = (UvFaces)jsonParser.readValueAs(UvFaces.class);
            value.isBoxUV = false;
            break;
         default:
            throw new IOException("Cannot deserialize UvUnion");
         }

         return value;
      }
   }
}
