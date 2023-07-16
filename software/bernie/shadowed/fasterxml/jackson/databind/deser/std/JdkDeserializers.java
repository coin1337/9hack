package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;

public class JdkDeserializers {
   private static final HashSet<String> _classNames = new HashSet();

   public static JsonDeserializer<?> find(Class<?> rawType, String clsName) {
      if (_classNames.contains(clsName)) {
         JsonDeserializer<?> d = FromStringDeserializer.findDeserializer(rawType);
         if (d != null) {
            return d;
         }

         if (rawType == UUID.class) {
            return new UUIDDeserializer();
         }

         if (rawType == StackTraceElement.class) {
            return new StackTraceElementDeserializer();
         }

         if (rawType == AtomicBoolean.class) {
            return new AtomicBooleanDeserializer();
         }

         if (rawType == ByteBuffer.class) {
            return new ByteBufferDeserializer();
         }
      }

      return null;
   }

   static {
      Class<?>[] types = new Class[]{UUID.class, AtomicBoolean.class, StackTraceElement.class, ByteBuffer.class};
      Class[] arr$ = types;
      int len$ = types.length;

      int i$;
      Class cls;
      for(i$ = 0; i$ < len$; ++i$) {
         cls = arr$[i$];
         _classNames.add(cls.getName());
      }

      arr$ = FromStringDeserializer.types();
      len$ = arr$.length;

      for(i$ = 0; i$ < len$; ++i$) {
         cls = arr$[i$];
         _classNames.add(cls.getName());
      }

   }
}
