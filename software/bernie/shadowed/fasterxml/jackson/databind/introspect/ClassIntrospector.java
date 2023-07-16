package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfig;

public abstract class ClassIntrospector {
   protected ClassIntrospector() {
   }

   public abstract BeanDescription forSerialization(SerializationConfig var1, JavaType var2, ClassIntrospector.MixInResolver var3);

   public abstract BeanDescription forDeserialization(DeserializationConfig var1, JavaType var2, ClassIntrospector.MixInResolver var3);

   public abstract BeanDescription forDeserializationWithBuilder(DeserializationConfig var1, JavaType var2, ClassIntrospector.MixInResolver var3);

   public abstract BeanDescription forCreation(DeserializationConfig var1, JavaType var2, ClassIntrospector.MixInResolver var3);

   public abstract BeanDescription forClassAnnotations(MapperConfig<?> var1, JavaType var2, ClassIntrospector.MixInResolver var3);

   public abstract BeanDescription forDirectClassAnnotations(MapperConfig<?> var1, JavaType var2, ClassIntrospector.MixInResolver var3);

   public interface MixInResolver {
      Class<?> findMixInClassFor(Class<?> var1);

      ClassIntrospector.MixInResolver copy();
   }
}
