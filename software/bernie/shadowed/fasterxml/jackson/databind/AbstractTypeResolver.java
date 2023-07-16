package software.bernie.shadowed.fasterxml.jackson.databind;

public abstract class AbstractTypeResolver {
   public JavaType findTypeMapping(DeserializationConfig config, JavaType type) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public JavaType resolveAbstractType(DeserializationConfig config, JavaType type) {
      return null;
   }

   public JavaType resolveAbstractType(DeserializationConfig config, BeanDescription typeDesc) {
      return null;
   }
}
