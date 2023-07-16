package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.lang.reflect.Type;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeBindings;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeFactory;

public interface TypeResolutionContext {
   JavaType resolveType(Type var1);

   public static class Basic implements TypeResolutionContext {
      private final TypeFactory _typeFactory;
      private final TypeBindings _bindings;

      public Basic(TypeFactory tf, TypeBindings b) {
         this._typeFactory = tf;
         this._bindings = b;
      }

      public JavaType resolveType(Type type) {
         return this._typeFactory.constructType(type, this._bindings);
      }
   }
}
