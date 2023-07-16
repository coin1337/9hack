package software.bernie.shadowed.fasterxml.jackson.databind.ser.impl;

import java.util.Arrays;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;

public abstract class PropertySerializerMap {
   protected final boolean _resetWhenFull;

   protected PropertySerializerMap(boolean resetWhenFull) {
      this._resetWhenFull = resetWhenFull;
   }

   protected PropertySerializerMap(PropertySerializerMap base) {
      this._resetWhenFull = base._resetWhenFull;
   }

   public abstract JsonSerializer<Object> serializerFor(Class<?> var1);

   public final PropertySerializerMap.SerializerAndMapResult findAndAddPrimarySerializer(Class<?> type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
      JsonSerializer<Object> serializer = provider.findPrimaryPropertySerializer(type, property);
      return new PropertySerializerMap.SerializerAndMapResult(serializer, this.newWith(type, serializer));
   }

   public final PropertySerializerMap.SerializerAndMapResult findAndAddPrimarySerializer(JavaType type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
      JsonSerializer<Object> serializer = provider.findPrimaryPropertySerializer(type, property);
      return new PropertySerializerMap.SerializerAndMapResult(serializer, this.newWith(type.getRawClass(), serializer));
   }

   public final PropertySerializerMap.SerializerAndMapResult findAndAddSecondarySerializer(Class<?> type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
      JsonSerializer<Object> serializer = provider.findValueSerializer(type, property);
      return new PropertySerializerMap.SerializerAndMapResult(serializer, this.newWith(type, serializer));
   }

   public final PropertySerializerMap.SerializerAndMapResult findAndAddSecondarySerializer(JavaType type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
      JsonSerializer<Object> serializer = provider.findValueSerializer(type, property);
      return new PropertySerializerMap.SerializerAndMapResult(serializer, this.newWith(type.getRawClass(), serializer));
   }

   public final PropertySerializerMap.SerializerAndMapResult findAndAddRootValueSerializer(Class<?> type, SerializerProvider provider) throws JsonMappingException {
      JsonSerializer<Object> serializer = provider.findTypedValueSerializer((Class)type, false, (BeanProperty)null);
      return new PropertySerializerMap.SerializerAndMapResult(serializer, this.newWith(type, serializer));
   }

   public final PropertySerializerMap.SerializerAndMapResult findAndAddRootValueSerializer(JavaType type, SerializerProvider provider) throws JsonMappingException {
      JsonSerializer<Object> serializer = provider.findTypedValueSerializer((JavaType)type, false, (BeanProperty)null);
      return new PropertySerializerMap.SerializerAndMapResult(serializer, this.newWith(type.getRawClass(), serializer));
   }

   public final PropertySerializerMap.SerializerAndMapResult findAndAddKeySerializer(Class<?> type, SerializerProvider provider, BeanProperty property) throws JsonMappingException {
      JsonSerializer<Object> serializer = provider.findKeySerializer(type, property);
      return new PropertySerializerMap.SerializerAndMapResult(serializer, this.newWith(type, serializer));
   }

   public final PropertySerializerMap.SerializerAndMapResult addSerializer(Class<?> type, JsonSerializer<Object> serializer) {
      return new PropertySerializerMap.SerializerAndMapResult(serializer, this.newWith(type, serializer));
   }

   public final PropertySerializerMap.SerializerAndMapResult addSerializer(JavaType type, JsonSerializer<Object> serializer) {
      return new PropertySerializerMap.SerializerAndMapResult(serializer, this.newWith(type.getRawClass(), serializer));
   }

   public abstract PropertySerializerMap newWith(Class<?> var1, JsonSerializer<Object> var2);

   /** @deprecated */
   @Deprecated
   public static PropertySerializerMap emptyMap() {
      return emptyForProperties();
   }

   public static PropertySerializerMap emptyForProperties() {
      return PropertySerializerMap.Empty.FOR_PROPERTIES;
   }

   public static PropertySerializerMap emptyForRootValues() {
      return PropertySerializerMap.Empty.FOR_ROOT_VALUES;
   }

   private static final class Multi extends PropertySerializerMap {
      private static final int MAX_ENTRIES = 8;
      private final PropertySerializerMap.TypeAndSerializer[] _entries;

      public Multi(PropertySerializerMap base, PropertySerializerMap.TypeAndSerializer[] entries) {
         super(base);
         this._entries = entries;
      }

      public JsonSerializer<Object> serializerFor(Class<?> type) {
         int i = 0;

         for(int len = this._entries.length; i < len; ++i) {
            PropertySerializerMap.TypeAndSerializer entry = this._entries[i];
            if (entry.type == type) {
               return entry.serializer;
            }
         }

         return null;
      }

      public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
         int len = this._entries.length;
         if (len == 8) {
            return (PropertySerializerMap)(this._resetWhenFull ? new PropertySerializerMap.Single(this, type, serializer) : this);
         } else {
            PropertySerializerMap.TypeAndSerializer[] entries = (PropertySerializerMap.TypeAndSerializer[])Arrays.copyOf(this._entries, len + 1);
            entries[len] = new PropertySerializerMap.TypeAndSerializer(type, serializer);
            return new PropertySerializerMap.Multi(this, entries);
         }
      }
   }

   private static final class Double extends PropertySerializerMap {
      private final Class<?> _type1;
      private final Class<?> _type2;
      private final JsonSerializer<Object> _serializer1;
      private final JsonSerializer<Object> _serializer2;

      public Double(PropertySerializerMap base, Class<?> type1, JsonSerializer<Object> serializer1, Class<?> type2, JsonSerializer<Object> serializer2) {
         super(base);
         this._type1 = type1;
         this._serializer1 = serializer1;
         this._type2 = type2;
         this._serializer2 = serializer2;
      }

      public JsonSerializer<Object> serializerFor(Class<?> type) {
         if (type == this._type1) {
            return this._serializer1;
         } else {
            return type == this._type2 ? this._serializer2 : null;
         }
      }

      public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
         PropertySerializerMap.TypeAndSerializer[] ts = new PropertySerializerMap.TypeAndSerializer[]{new PropertySerializerMap.TypeAndSerializer(this._type1, this._serializer1), new PropertySerializerMap.TypeAndSerializer(this._type2, this._serializer2), new PropertySerializerMap.TypeAndSerializer(type, serializer)};
         return new PropertySerializerMap.Multi(this, ts);
      }
   }

   private static final class Single extends PropertySerializerMap {
      private final Class<?> _type;
      private final JsonSerializer<Object> _serializer;

      public Single(PropertySerializerMap base, Class<?> type, JsonSerializer<Object> serializer) {
         super(base);
         this._type = type;
         this._serializer = serializer;
      }

      public JsonSerializer<Object> serializerFor(Class<?> type) {
         return type == this._type ? this._serializer : null;
      }

      public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
         return new PropertySerializerMap.Double(this, this._type, this._serializer, type, serializer);
      }
   }

   private static final class Empty extends PropertySerializerMap {
      public static final PropertySerializerMap.Empty FOR_PROPERTIES = new PropertySerializerMap.Empty(false);
      public static final PropertySerializerMap.Empty FOR_ROOT_VALUES = new PropertySerializerMap.Empty(true);

      protected Empty(boolean resetWhenFull) {
         super(resetWhenFull);
      }

      public JsonSerializer<Object> serializerFor(Class<?> type) {
         return null;
      }

      public PropertySerializerMap newWith(Class<?> type, JsonSerializer<Object> serializer) {
         return new PropertySerializerMap.Single(this, type, serializer);
      }
   }

   private static final class TypeAndSerializer {
      public final Class<?> type;
      public final JsonSerializer<Object> serializer;

      public TypeAndSerializer(Class<?> type, JsonSerializer<Object> serializer) {
         this.type = type;
         this.serializer = serializer;
      }
   }

   public static final class SerializerAndMapResult {
      public final JsonSerializer<Object> serializer;
      public final PropertySerializerMap map;

      public SerializerAndMapResult(JsonSerializer<Object> serializer, PropertySerializerMap map) {
         this.serializer = serializer;
         this.map = map;
      }
   }
}
