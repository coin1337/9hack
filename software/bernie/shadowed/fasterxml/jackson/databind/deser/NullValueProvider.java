package software.bernie.shadowed.fasterxml.jackson.databind.deser;

import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.util.AccessPattern;

public interface NullValueProvider {
   Object getNullValue(DeserializationContext var1) throws JsonMappingException;

   AccessPattern getNullAccessPattern();
}
