package software.bernie.shadowed.fasterxml.jackson.databind.deser;

import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;

public interface ResolvableDeserializer {
   void resolve(DeserializationContext var1) throws JsonMappingException;
}
