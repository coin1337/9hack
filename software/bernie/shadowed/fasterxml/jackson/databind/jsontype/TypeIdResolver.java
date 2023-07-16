package software.bernie.shadowed.fasterxml.jackson.databind.jsontype;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonTypeInfo;
import software.bernie.shadowed.fasterxml.jackson.databind.DatabindContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;

public interface TypeIdResolver {
   void init(JavaType var1);

   String idFromValue(Object var1);

   String idFromValueAndType(Object var1, Class<?> var2);

   String idFromBaseType();

   JavaType typeFromId(DatabindContext var1, String var2) throws IOException;

   String getDescForKnownTypeIds();

   JsonTypeInfo.Id getMechanism();
}
