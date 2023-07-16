package software.bernie.shadowed.fasterxml.jackson.databind.ser;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap;
import software.bernie.shadowed.fasterxml.jackson.databind.util.TypeKey;

public final class SerializerCache {
   private final HashMap<TypeKey, JsonSerializer<Object>> _sharedMap = new HashMap(64);
   private final AtomicReference<ReadOnlyClassToSerializerMap> _readOnlyMap = new AtomicReference();

   public ReadOnlyClassToSerializerMap getReadOnlyLookupMap() {
      ReadOnlyClassToSerializerMap m = (ReadOnlyClassToSerializerMap)this._readOnlyMap.get();
      return m != null ? m : this._makeReadOnlyLookupMap();
   }

   private final synchronized ReadOnlyClassToSerializerMap _makeReadOnlyLookupMap() {
      ReadOnlyClassToSerializerMap m = (ReadOnlyClassToSerializerMap)this._readOnlyMap.get();
      if (m == null) {
         m = ReadOnlyClassToSerializerMap.from(this._sharedMap);
         this._readOnlyMap.set(m);
      }

      return m;
   }

   public synchronized int size() {
      return this._sharedMap.size();
   }

   public JsonSerializer<Object> untypedValueSerializer(Class<?> type) {
      synchronized(this) {
         return (JsonSerializer)this._sharedMap.get(new TypeKey(type, false));
      }
   }

   public JsonSerializer<Object> untypedValueSerializer(JavaType type) {
      synchronized(this) {
         return (JsonSerializer)this._sharedMap.get(new TypeKey(type, false));
      }
   }

   public JsonSerializer<Object> typedValueSerializer(JavaType type) {
      synchronized(this) {
         return (JsonSerializer)this._sharedMap.get(new TypeKey(type, true));
      }
   }

   public JsonSerializer<Object> typedValueSerializer(Class<?> cls) {
      synchronized(this) {
         return (JsonSerializer)this._sharedMap.get(new TypeKey(cls, true));
      }
   }

   public void addTypedSerializer(JavaType type, JsonSerializer<Object> ser) {
      synchronized(this) {
         if (this._sharedMap.put(new TypeKey(type, true), ser) == null) {
            this._readOnlyMap.set((Object)null);
         }

      }
   }

   public void addTypedSerializer(Class<?> cls, JsonSerializer<Object> ser) {
      synchronized(this) {
         if (this._sharedMap.put(new TypeKey(cls, true), ser) == null) {
            this._readOnlyMap.set((Object)null);
         }

      }
   }

   public void addAndResolveNonTypedSerializer(Class<?> type, JsonSerializer<Object> ser, SerializerProvider provider) throws JsonMappingException {
      synchronized(this) {
         if (this._sharedMap.put(new TypeKey(type, false), ser) == null) {
            this._readOnlyMap.set((Object)null);
         }

         if (ser instanceof ResolvableSerializer) {
            ((ResolvableSerializer)ser).resolve(provider);
         }

      }
   }

   public void addAndResolveNonTypedSerializer(JavaType type, JsonSerializer<Object> ser, SerializerProvider provider) throws JsonMappingException {
      synchronized(this) {
         if (this._sharedMap.put(new TypeKey(type, false), ser) == null) {
            this._readOnlyMap.set((Object)null);
         }

         if (ser instanceof ResolvableSerializer) {
            ((ResolvableSerializer)ser).resolve(provider);
         }

      }
   }

   public void addAndResolveNonTypedSerializer(Class<?> rawType, JavaType fullType, JsonSerializer<Object> ser, SerializerProvider provider) throws JsonMappingException {
      synchronized(this) {
         Object ob1 = this._sharedMap.put(new TypeKey(rawType, false), ser);
         Object ob2 = this._sharedMap.put(new TypeKey(fullType, false), ser);
         if (ob1 == null || ob2 == null) {
            this._readOnlyMap.set((Object)null);
         }

         if (ser instanceof ResolvableSerializer) {
            ((ResolvableSerializer)ser).resolve(provider);
         }

      }
   }

   public synchronized void flush() {
      this._sharedMap.clear();
   }
}
