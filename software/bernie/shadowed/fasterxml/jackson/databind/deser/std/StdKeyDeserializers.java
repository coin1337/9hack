package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.KeyDeserializers;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.EnumResolver;

public class StdKeyDeserializers implements KeyDeserializers, Serializable {
   private static final long serialVersionUID = 1L;

   public static KeyDeserializer constructEnumKeyDeserializer(EnumResolver enumResolver) {
      return new StdKeyDeserializer.EnumKD(enumResolver, (AnnotatedMethod)null);
   }

   public static KeyDeserializer constructEnumKeyDeserializer(EnumResolver enumResolver, AnnotatedMethod factory) {
      return new StdKeyDeserializer.EnumKD(enumResolver, factory);
   }

   public static KeyDeserializer constructDelegatingKeyDeserializer(DeserializationConfig config, JavaType type, JsonDeserializer<?> deser) {
      return new StdKeyDeserializer.DelegatingKD(type.getRawClass(), deser);
   }

   public static KeyDeserializer findStringBasedKeyDeserializer(DeserializationConfig config, JavaType type) {
      BeanDescription beanDesc = config.introspect(type);
      Constructor<?> ctor = beanDesc.findSingleArgConstructor(String.class);
      if (ctor != null) {
         if (config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(ctor, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
         }

         return new StdKeyDeserializer.StringCtorKeyDeserializer(ctor);
      } else {
         Method m = beanDesc.findFactoryMethod(String.class);
         if (m != null) {
            if (config.canOverrideAccessModifiers()) {
               ClassUtil.checkAndFixAccess(m, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            }

            return new StdKeyDeserializer.StringFactoryKeyDeserializer(m);
         } else {
            return null;
         }
      }
   }

   public KeyDeserializer findKeyDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
      Class<?> raw = type.getRawClass();
      if (raw.isPrimitive()) {
         raw = ClassUtil.wrapperType(raw);
      }

      return StdKeyDeserializer.forType(raw);
   }
}
