package software.bernie.shadowed.fasterxml.jackson.databind.deser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.Annotated;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ArrayType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.CollectionLikeType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.CollectionType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.MapLikeType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.MapType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ReferenceType;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Converter;

public final class DeserializerCache implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _cachedDeserializers = new ConcurrentHashMap(64, 0.75F, 4);
   protected final HashMap<JavaType, JsonDeserializer<Object>> _incompleteDeserializers = new HashMap(8);

   Object writeReplace() {
      this._incompleteDeserializers.clear();
      return this;
   }

   public int cachedDeserializersCount() {
      return this._cachedDeserializers.size();
   }

   public void flushCachedDeserializers() {
      this._cachedDeserializers.clear();
   }

   public JsonDeserializer<Object> findValueDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType propertyType) throws JsonMappingException {
      JsonDeserializer<Object> deser = this._findCachedDeserializer(propertyType);
      if (deser == null) {
         deser = this._createAndCacheValueDeserializer(ctxt, factory, propertyType);
         if (deser == null) {
            deser = this._handleUnknownValueDeserializer(ctxt, propertyType);
         }
      }

      return deser;
   }

   public KeyDeserializer findKeyDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
      KeyDeserializer kd = factory.createKeyDeserializer(ctxt, type);
      if (kd == null) {
         return this._handleUnknownKeyDeserializer(ctxt, type);
      } else {
         if (kd instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer)kd).resolve(ctxt);
         }

         return kd;
      }
   }

   public boolean hasValueDeserializerFor(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
      JsonDeserializer<Object> deser = this._findCachedDeserializer(type);
      if (deser == null) {
         deser = this._createAndCacheValueDeserializer(ctxt, factory, type);
      }

      return deser != null;
   }

   protected JsonDeserializer<Object> _findCachedDeserializer(JavaType type) {
      if (type == null) {
         throw new IllegalArgumentException("Null JavaType passed");
      } else {
         return this._hasCustomValueHandler(type) ? null : (JsonDeserializer)this._cachedDeserializers.get(type);
      }
   }

   protected JsonDeserializer<Object> _createAndCacheValueDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
      synchronized(this._incompleteDeserializers) {
         JsonDeserializer<Object> deser = this._findCachedDeserializer(type);
         if (deser != null) {
            return deser;
         } else {
            int count = this._incompleteDeserializers.size();
            if (count > 0) {
               deser = (JsonDeserializer)this._incompleteDeserializers.get(type);
               if (deser != null) {
                  return deser;
               }
            }

            JsonDeserializer var7;
            try {
               var7 = this._createAndCache2(ctxt, factory, type);
            } finally {
               if (count == 0 && this._incompleteDeserializers.size() > 0) {
                  this._incompleteDeserializers.clear();
               }

            }

            return var7;
         }
      }
   }

   protected JsonDeserializer<Object> _createAndCache2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
      JsonDeserializer deser;
      try {
         deser = this._createDeserializer(ctxt, factory, type);
      } catch (IllegalArgumentException var6) {
         throw JsonMappingException.from((DeserializationContext)ctxt, var6.getMessage(), var6);
      }

      if (deser == null) {
         return null;
      } else {
         boolean addToCache = !this._hasCustomValueHandler(type) && deser.isCachable();
         if (deser instanceof ResolvableDeserializer) {
            this._incompleteDeserializers.put(type, deser);
            ((ResolvableDeserializer)deser).resolve(ctxt);
            this._incompleteDeserializers.remove(type);
         }

         if (addToCache) {
            this._cachedDeserializers.put(type, deser);
         }

         return deser;
      }
   }

   protected JsonDeserializer<Object> _createDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      if (type.isAbstract() || type.isMapLikeType() || type.isCollectionLikeType()) {
         type = factory.mapAbstractType(config, type);
      }

      BeanDescription beanDesc = config.introspect(type);
      JsonDeserializer<Object> deser = this.findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
      if (deser != null) {
         return deser;
      } else {
         JavaType newType = this.modifyTypeByAnnotation(ctxt, beanDesc.getClassInfo(), type);
         if (newType != type) {
            type = newType;
            beanDesc = config.introspect(newType);
         }

         Class<?> builder = beanDesc.findPOJOBuilder();
         if (builder != null) {
            return factory.createBuilderBasedDeserializer(ctxt, type, beanDesc, builder);
         } else {
            Converter<Object, Object> conv = beanDesc.findDeserializationConverter();
            if (conv == null) {
               return this._createDeserializer2(ctxt, factory, type, beanDesc);
            } else {
               JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
               if (!delegateType.hasRawClass(type.getRawClass())) {
                  beanDesc = config.introspect(delegateType);
               }

               return new StdDelegatingDeserializer(conv, delegateType, this._createDeserializer2(ctxt, factory, delegateType, beanDesc));
            }
         }
      }
   }

   protected JsonDeserializer<?> _createDeserializer2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      if (type.isEnumType()) {
         return factory.createEnumDeserializer(ctxt, type, beanDesc);
      } else {
         if (type.isContainerType()) {
            if (type.isArrayType()) {
               return factory.createArrayDeserializer(ctxt, (ArrayType)type, beanDesc);
            }

            JsonFormat.Value format;
            if (type.isMapLikeType()) {
               format = beanDesc.findExpectedFormat((JsonFormat.Value)null);
               if (format == null || format.getShape() != JsonFormat.Shape.OBJECT) {
                  MapLikeType mlt = (MapLikeType)type;
                  if (mlt.isTrueMapType()) {
                     return factory.createMapDeserializer(ctxt, (MapType)mlt, beanDesc);
                  }

                  return factory.createMapLikeDeserializer(ctxt, mlt, beanDesc);
               }
            }

            if (type.isCollectionLikeType()) {
               format = beanDesc.findExpectedFormat((JsonFormat.Value)null);
               if (format == null || format.getShape() != JsonFormat.Shape.OBJECT) {
                  CollectionLikeType clt = (CollectionLikeType)type;
                  if (clt.isTrueCollectionType()) {
                     return factory.createCollectionDeserializer(ctxt, (CollectionType)clt, beanDesc);
                  }

                  return factory.createCollectionLikeDeserializer(ctxt, clt, beanDesc);
               }
            }
         }

         if (type.isReferenceType()) {
            return factory.createReferenceDeserializer(ctxt, (ReferenceType)type, beanDesc);
         } else {
            return JsonNode.class.isAssignableFrom(type.getRawClass()) ? factory.createTreeDeserializer(config, type, beanDesc) : factory.createBeanDeserializer(ctxt, type, beanDesc);
         }
      }
   }

   protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
      Object deserDef = ctxt.getAnnotationIntrospector().findDeserializer(ann);
      if (deserDef == null) {
         return null;
      } else {
         JsonDeserializer<Object> deser = ctxt.deserializerInstance(ann, deserDef);
         return this.findConvertingDeserializer(ctxt, ann, deser);
      }
   }

   protected JsonDeserializer<Object> findConvertingDeserializer(DeserializationContext ctxt, Annotated a, JsonDeserializer<Object> deser) throws JsonMappingException {
      Converter<Object, Object> conv = this.findConverter(ctxt, a);
      if (conv == null) {
         return deser;
      } else {
         JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
         return new StdDelegatingDeserializer(conv, delegateType, deser);
      }
   }

   protected Converter<Object, Object> findConverter(DeserializationContext ctxt, Annotated a) throws JsonMappingException {
      Object convDef = ctxt.getAnnotationIntrospector().findDeserializationConverter(a);
      return convDef == null ? null : ctxt.converterInstance(a, convDef);
   }

   private JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (intr == null) {
         return (JavaType)type;
      } else {
         JavaType contentType;
         Object cdDef;
         if (((JavaType)type).isMapLikeType()) {
            contentType = ((JavaType)type).getKeyType();
            if (contentType != null && contentType.getValueHandler() == null) {
               cdDef = intr.findKeyDeserializer(a);
               if (cdDef != null) {
                  KeyDeserializer kd = ctxt.keyDeserializerInstance(a, cdDef);
                  if (kd != null) {
                     type = ((MapLikeType)type).withKeyValueHandler(kd);
                     contentType = ((JavaType)type).getKeyType();
                  }
               }
            }
         }

         contentType = ((JavaType)type).getContentType();
         if (contentType != null && contentType.getValueHandler() == null) {
            cdDef = intr.findContentDeserializer(a);
            if (cdDef != null) {
               JsonDeserializer<?> cd = null;
               if (cdDef instanceof JsonDeserializer) {
                  JsonDeserializer var10 = (JsonDeserializer)cdDef;
               } else {
                  Class<?> cdClass = this._verifyAsClass(cdDef, "findContentDeserializer", JsonDeserializer.None.class);
                  if (cdClass != null) {
                     cd = ctxt.deserializerInstance(a, cdClass);
                  }
               }

               if (cd != null) {
                  type = ((JavaType)type).withContentValueHandler(cd);
               }
            }
         }

         JavaType type = intr.refineDeserializationType(ctxt.getConfig(), a, (JavaType)type);
         return type;
      }
   }

   private boolean _hasCustomValueHandler(JavaType t) {
      if (t.isContainerType()) {
         JavaType ct = t.getContentType();
         if (ct != null) {
            return ct.getValueHandler() != null || ct.getTypeHandler() != null;
         }
      }

      return false;
   }

   private Class<?> _verifyAsClass(Object src, String methodName, Class<?> noneClass) {
      if (src == null) {
         return null;
      } else if (!(src instanceof Class)) {
         throw new IllegalStateException("AnnotationIntrospector." + methodName + "() returned value of type " + src.getClass().getName() + ": expected type JsonSerializer or Class<JsonSerializer> instead");
      } else {
         Class<?> cls = (Class)src;
         return cls != noneClass && !ClassUtil.isBogusClass(cls) ? cls : null;
      }
   }

   protected JsonDeserializer<Object> _handleUnknownValueDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
      Class<?> rawClass = type.getRawClass();
      return !ClassUtil.isConcrete(rawClass) ? (JsonDeserializer)ctxt.reportBadDefinition(type, "Cannot find a Value deserializer for abstract type " + type) : (JsonDeserializer)ctxt.reportBadDefinition(type, "Cannot find a Value deserializer for type " + type);
   }

   protected KeyDeserializer _handleUnknownKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
      return (KeyDeserializer)ctxt.reportBadDefinition(type, "Cannot find a (Map) Key deserializer for type " + type);
   }
}
