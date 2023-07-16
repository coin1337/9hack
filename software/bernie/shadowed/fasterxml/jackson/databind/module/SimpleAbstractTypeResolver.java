package software.bernie.shadowed.fasterxml.jackson.databind.module;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import software.bernie.shadowed.fasterxml.jackson.databind.AbstractTypeResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ClassKey;

public class SimpleAbstractTypeResolver extends AbstractTypeResolver implements Serializable {
   private static final long serialVersionUID = 8635483102371490919L;
   protected final HashMap<ClassKey, Class<?>> _mappings = new HashMap();

   public <T> SimpleAbstractTypeResolver addMapping(Class<T> superType, Class<? extends T> subType) {
      if (superType == subType) {
         throw new IllegalArgumentException("Cannot add mapping from class to itself");
      } else if (!superType.isAssignableFrom(subType)) {
         throw new IllegalArgumentException("Cannot add mapping from class " + superType.getName() + " to " + subType.getName() + ", as latter is not a subtype of former");
      } else if (!Modifier.isAbstract(superType.getModifiers())) {
         throw new IllegalArgumentException("Cannot add mapping from class " + superType.getName() + " since it is not abstract");
      } else {
         this._mappings.put(new ClassKey(superType), subType);
         return this;
      }
   }

   public JavaType findTypeMapping(DeserializationConfig config, JavaType type) {
      Class<?> src = type.getRawClass();
      Class<?> dst = (Class)this._mappings.get(new ClassKey(src));
      return dst == null ? null : config.getTypeFactory().constructSpecializedType(type, dst);
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
