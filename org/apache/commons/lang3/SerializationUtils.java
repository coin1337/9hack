package org.apache.commons.lang3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SerializationUtils {
   public static <T extends Serializable> T clone(T object) {
      if (object == null) {
         return null;
      } else {
         byte[] objectData = serialize(object);
         ByteArrayInputStream bais = new ByteArrayInputStream(objectData);

         try {
            SerializationUtils.ClassLoaderAwareObjectInputStream in = new SerializationUtils.ClassLoaderAwareObjectInputStream(bais, object.getClass().getClassLoader());
            Throwable var4 = null;

            Serializable var6;
            try {
               T readObject = (Serializable)in.readObject();
               var6 = readObject;
            } catch (Throwable var17) {
               var4 = var17;
               throw var17;
            } finally {
               if (in != null) {
                  if (var4 != null) {
                     try {
                        in.close();
                     } catch (Throwable var16) {
                        var4.addSuppressed(var16);
                     }
                  } else {
                     in.close();
                  }
               }

            }

            return var6;
         } catch (ClassNotFoundException var19) {
            throw new SerializationException("ClassNotFoundException while reading cloned object data", var19);
         } catch (IOException var20) {
            throw new SerializationException("IOException while reading or closing cloned object data", var20);
         }
      }
   }

   public static <T extends Serializable> T roundtrip(T msg) {
      return (Serializable)deserialize(serialize(msg));
   }

   public static void serialize(Serializable obj, OutputStream outputStream) {
      Validate.notNull(outputStream, "The OutputStream must not be null");

      try {
         ObjectOutputStream out = new ObjectOutputStream(outputStream);
         Throwable var3 = null;

         try {
            out.writeObject(obj);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (out != null) {
               if (var3 != null) {
                  try {
                     out.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  out.close();
               }
            }

         }

      } catch (IOException var15) {
         throw new SerializationException(var15);
      }
   }

   public static byte[] serialize(Serializable obj) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
      serialize(obj, baos);
      return baos.toByteArray();
   }

   public static <T> T deserialize(InputStream inputStream) {
      Validate.notNull(inputStream, "The InputStream must not be null");

      try {
         ObjectInputStream in = new ObjectInputStream(inputStream);
         Throwable var2 = null;

         Object var4;
         try {
            T obj = in.readObject();
            var4 = obj;
         } catch (Throwable var14) {
            var2 = var14;
            throw var14;
         } finally {
            if (in != null) {
               if (var2 != null) {
                  try {
                     in.close();
                  } catch (Throwable var13) {
                     var2.addSuppressed(var13);
                  }
               } else {
                  in.close();
               }
            }

         }

         return var4;
      } catch (IOException | ClassNotFoundException var16) {
         throw new SerializationException(var16);
      }
   }

   public static <T> T deserialize(byte[] objectData) {
      Validate.notNull(objectData, "The byte[] must not be null");
      return deserialize((InputStream)(new ByteArrayInputStream(objectData)));
   }

   static class ClassLoaderAwareObjectInputStream extends ObjectInputStream {
      private static final Map<String, Class<?>> primitiveTypes = new HashMap();
      private final ClassLoader classLoader;

      ClassLoaderAwareObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
         super(in);
         this.classLoader = classLoader;
      }

      protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
         String name = desc.getName();

         try {
            return Class.forName(name, false, this.classLoader);
         } catch (ClassNotFoundException var7) {
            try {
               return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException var6) {
               Class<?> cls = (Class)primitiveTypes.get(name);
               if (cls != null) {
                  return cls;
               } else {
                  throw var6;
               }
            }
         }
      }

      static {
         primitiveTypes.put("byte", Byte.TYPE);
         primitiveTypes.put("short", Short.TYPE);
         primitiveTypes.put("int", Integer.TYPE);
         primitiveTypes.put("long", Long.TYPE);
         primitiveTypes.put("float", Float.TYPE);
         primitiveTypes.put("double", Double.TYPE);
         primitiveTypes.put("boolean", Boolean.TYPE);
         primitiveTypes.put("char", Character.TYPE);
         primitiveTypes.put("void", Void.TYPE);
      }
   }
}
