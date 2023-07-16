package software.bernie.shadowed.fasterxml.jackson.databind.module;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.Serializers;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ArrayType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ClassKey;
import software.bernie.shadowed.fasterxml.jackson.databind.type.CollectionLikeType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.CollectionType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.MapLikeType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.MapType;

public class SimpleSerializers extends Serializers.Base implements Serializable {
   private static final long serialVersionUID = 8531646511998456779L;
   protected HashMap<ClassKey, JsonSerializer<?>> _classMappings = null;
   protected HashMap<ClassKey, JsonSerializer<?>> _interfaceMappings = null;
   protected boolean _hasEnumSerializer = false;

   public SimpleSerializers() {
   }

   public SimpleSerializers(List<JsonSerializer<?>> sers) {
      this.addSerializers(sers);
   }

   public void addSerializer(JsonSerializer<?> ser) {
      Class<?> cls = ser.handledType();
      if (cls != null && cls != Object.class) {
         this._addSerializer(cls, ser);
      } else {
         throw new IllegalArgumentException("JsonSerializer of type " + ser.getClass().getName() + " does not define valid handledType() -- must either register with method that takes type argument " + " or make serializer extend 'com.fasterxml.jackson.databind.ser.std.StdSerializer'");
      }
   }

   public <T> void addSerializer(Class<? extends T> type, JsonSerializer<T> ser) {
      this._addSerializer(type, ser);
   }

   public void addSerializers(List<JsonSerializer<?>> sers) {
      Iterator i$ = sers.iterator();

      while(i$.hasNext()) {
         JsonSerializer<?> ser = (JsonSerializer)i$.next();
         this.addSerializer(ser);
      }

   }

   public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
      Class<?> cls = type.getRawClass();
      ClassKey key = new ClassKey(cls);
      JsonSerializer<?> ser = null;
      if (cls.isInterface()) {
         if (this._interfaceMappings != null) {
            ser = (JsonSerializer)this._interfaceMappings.get(key);
            if (ser != null) {
               return ser;
            }
         }
      } else if (this._classMappings != null) {
         ser = (JsonSerializer)this._classMappings.get(key);
         if (ser != null) {
            return ser;
         }

         if (this._hasEnumSerializer && type.isEnumType()) {
            key.reset(Enum.class);
            ser = (JsonSerializer)this._classMappings.get(key);
            if (ser != null) {
               return ser;
            }
         }

         for(Class curr = cls; curr != null; curr = curr.getSuperclass()) {
            key.reset(curr);
            ser = (JsonSerializer)this._classMappings.get(key);
            if (ser != null) {
               return ser;
            }
         }
      }

      if (this._interfaceMappings != null) {
         ser = this._findInterfaceMapping(cls, key);
         if (ser != null) {
            return ser;
         }

         if (!cls.isInterface()) {
            while((cls = cls.getSuperclass()) != null) {
               ser = this._findInterfaceMapping(cls, key);
               if (ser != null) {
                  return ser;
               }
            }
         }
      }

      return null;
   }

   public JsonSerializer<?> findArraySerializer(SerializationConfig config, ArrayType type, BeanDescription beanDesc, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
      return this.findSerializer(config, type, beanDesc);
   }

   public JsonSerializer<?> findCollectionSerializer(SerializationConfig config, CollectionType type, BeanDescription beanDesc, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
      return this.findSerializer(config, type, beanDesc);
   }

   public JsonSerializer<?> findCollectionLikeSerializer(SerializationConfig config, CollectionLikeType type, BeanDescription beanDesc, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
      return this.findSerializer(config, type, beanDesc);
   }

   public JsonSerializer<?> findMapSerializer(SerializationConfig config, MapType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
      return this.findSerializer(config, type, beanDesc);
   }

   public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config, MapLikeType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
      return this.findSerializer(config, type, beanDesc);
   }

   protected JsonSerializer<?> _findInterfaceMapping(Class<?> cls, ClassKey key) {
      Class[] arr$ = cls.getInterfaces();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Class<?> iface = arr$[i$];
         key.reset(iface);
         JsonSerializer<?> ser = (JsonSerializer)this._interfaceMappings.get(key);
         if (ser != null) {
            return ser;
         }

         ser = this._findInterfaceMapping(iface, key);
         if (ser != null) {
            return ser;
         }
      }

      return null;
   }

   protected void _addSerializer(Class<?> cls, JsonSerializer<?> ser) {
      ClassKey key = new ClassKey(cls);
      if (cls.isInterface()) {
         if (this._interfaceMappings == null) {
            this._interfaceMappings = new HashMap();
         }

         this._interfaceMappings.put(key, ser);
      } else {
         if (this._classMappings == null) {
            this._classMappings = new HashMap();
         }

         this._classMappings.put(key, ser);
         if (cls == Enum.class) {
            this._hasEnumSerializer = true;
         }
      }

   }
}
