package software.bernie.shadowed.fasterxml.jackson.databind.util;

import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeFactory;

public interface Converter<IN, OUT> {
   OUT convert(IN var1);

   JavaType getInputType(TypeFactory var1);

   JavaType getOutputType(TypeFactory var1);

   public abstract static class None implements Converter<Object, Object> {
   }
}
