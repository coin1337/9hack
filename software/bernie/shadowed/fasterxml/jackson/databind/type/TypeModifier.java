package software.bernie.shadowed.fasterxml.jackson.databind.type;

import java.lang.reflect.Type;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;

public abstract class TypeModifier {
   public abstract JavaType modifyType(JavaType var1, Type var2, TypeBindings var3, TypeFactory var4);
}
