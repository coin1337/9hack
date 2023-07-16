package software.bernie.shadowed.fasterxml.jackson.databind.module;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.Deserializers;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ArrayType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ClassKey;
import software.bernie.shadowed.fasterxml.jackson.databind.type.CollectionLikeType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.CollectionType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.MapLikeType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.MapType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ReferenceType;

public class SimpleDeserializers implements Deserializers, Serializable {
   private static final long serialVersionUID = 1L;
   protected HashMap<ClassKey, JsonDeserializer<?>> _classMappings = null;
   protected boolean _hasEnumDeserializer = false;

   public SimpleDeserializers() {
   }

   public SimpleDeserializers(Map<Class<?>, JsonDeserializer<?>> desers) {
      this.addDeserializers(desers);
   }

   public <T> void addDeserializer(Class<T> forClass, JsonDeserializer<? extends T> deser) {
      ClassKey key = new ClassKey(forClass);
      if (this._classMappings == null) {
         this._classMappings = new HashMap();
      }

      this._classMappings.put(key, deser);
      if (forClass == Enum.class) {
         this._hasEnumDeserializer = true;
      }

   }

   public void addDeserializers(Map<Class<?>, JsonDeserializer<?>> desers) {
      Iterator i$ = desers.entrySet().iterator();

      while(i$.hasNext()) {
         Entry<Class<?>, JsonDeserializer<?>> entry = (Entry)i$.next();
         Class<?> cls = (Class)entry.getKey();
         JsonDeserializer<Object> deser = (JsonDeserializer)entry.getValue();
         this.addDeserializer(cls, deser);
      }

   }

   public JsonDeserializer<?> findArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      return this._find(type);
   }

   public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
      return this._find(type);
   }

   public JsonDeserializer<?> findCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      return this._find(type);
   }

   public JsonDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      return this._find(type);
   }

   public JsonDeserializer<?> findEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
      if (this._classMappings == null) {
         return null;
      } else {
         JsonDeserializer<?> deser = (JsonDeserializer)this._classMappings.get(new ClassKey(type));
         if (deser == null && this._hasEnumDeserializer && type.isEnum()) {
            deser = (JsonDeserializer)this._classMappings.get(new ClassKey(Enum.class));
         }

         return deser;
      }
   }

   public JsonDeserializer<?> findTreeNodeDeserializer(Class<? extends JsonNode> nodeType, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
      return this._classMappings == null ? null : (JsonDeserializer)this._classMappings.get(new ClassKey(nodeType));
   }

   public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
      return this._find(refType);
   }

   public JsonDeserializer<?> findMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      return this._find(type);
   }

   public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      return this._find(type);
   }

   private final JsonDeserializer<?> _find(JavaType type) {
      return this._classMappings == null ? null : (JsonDeserializer)this._classMappings.get(new ClassKey(type.getRawClass()));
   }
}
