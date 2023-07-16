package software.bernie.shadowed.fasterxml.jackson.databind.jsontype.impl;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.databind.DatabindContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeFactory;

public abstract class TypeIdResolverBase implements TypeIdResolver {
   protected final TypeFactory _typeFactory;
   protected final JavaType _baseType;

   protected TypeIdResolverBase() {
      this((JavaType)null, (TypeFactory)null);
   }

   protected TypeIdResolverBase(JavaType baseType, TypeFactory typeFactory) {
      this._baseType = baseType;
      this._typeFactory = typeFactory;
   }

   public void init(JavaType bt) {
   }

   public String idFromBaseType() {
      return this.idFromValueAndType((Object)null, this._baseType.getRawClass());
   }

   public JavaType typeFromId(DatabindContext context, String id) throws IOException {
      throw new IllegalStateException("Sub-class " + this.getClass().getName() + " MUST implement " + "`typeFromId(DatabindContext,String)");
   }

   public String getDescForKnownTypeIds() {
      return null;
   }
}
