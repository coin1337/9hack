package software.bernie.shadowed.fasterxml.jackson.databind.module;

import java.io.Serializable;
import java.util.HashMap;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.KeyDeserializers;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ClassKey;

public class SimpleKeyDeserializers implements KeyDeserializers, Serializable {
   private static final long serialVersionUID = 1L;
   protected HashMap<ClassKey, KeyDeserializer> _classMappings = null;

   public SimpleKeyDeserializers addDeserializer(Class<?> forClass, KeyDeserializer deser) {
      if (this._classMappings == null) {
         this._classMappings = new HashMap();
      }

      this._classMappings.put(new ClassKey(forClass), deser);
      return this;
   }

   public KeyDeserializer findKeyDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) {
      return this._classMappings == null ? null : (KeyDeserializer)this._classMappings.get(new ClassKey(type.getRawClass()));
   }
}
