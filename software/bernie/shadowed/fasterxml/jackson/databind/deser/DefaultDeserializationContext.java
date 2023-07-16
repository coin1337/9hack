package software.bernie.shadowed.fasterxml.jackson.databind.deser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdGenerator;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdResolver;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.InjectableValues;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.Annotated;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public abstract class DefaultDeserializationContext extends DeserializationContext implements Serializable {
   private static final long serialVersionUID = 1L;
   protected transient LinkedHashMap<ObjectIdGenerator.IdKey, ReadableObjectId> _objectIds;
   private List<ObjectIdResolver> _objectIdResolvers;

   protected DefaultDeserializationContext(DeserializerFactory df, DeserializerCache cache) {
      super(df, cache);
   }

   protected DefaultDeserializationContext(DefaultDeserializationContext src, DeserializationConfig config, JsonParser jp, InjectableValues values) {
      super(src, config, jp, values);
   }

   protected DefaultDeserializationContext(DefaultDeserializationContext src, DeserializerFactory factory) {
      super((DeserializationContext)src, (DeserializerFactory)factory);
   }

   protected DefaultDeserializationContext(DefaultDeserializationContext src) {
      super((DeserializationContext)src);
   }

   public DefaultDeserializationContext copy() {
      throw new IllegalStateException("DefaultDeserializationContext sub-class not overriding copy()");
   }

   public ReadableObjectId findObjectId(Object id, ObjectIdGenerator<?> gen, ObjectIdResolver resolverType) {
      if (id == null) {
         return null;
      } else {
         ObjectIdGenerator.IdKey key = gen.key(id);
         if (this._objectIds == null) {
            this._objectIds = new LinkedHashMap();
         } else {
            ReadableObjectId entry = (ReadableObjectId)this._objectIds.get(key);
            if (entry != null) {
               return entry;
            }
         }

         ObjectIdResolver resolver = null;
         if (this._objectIdResolvers == null) {
            this._objectIdResolvers = new ArrayList(8);
         } else {
            Iterator i$ = this._objectIdResolvers.iterator();

            while(i$.hasNext()) {
               ObjectIdResolver res = (ObjectIdResolver)i$.next();
               if (res.canUseFor(resolverType)) {
                  resolver = res;
                  break;
               }
            }
         }

         if (resolver == null) {
            resolver = resolverType.newForDeserialization(this);
            this._objectIdResolvers.add(resolver);
         }

         ReadableObjectId entry = this.createReadableObjectId(key);
         entry.setResolver(resolver);
         this._objectIds.put(key, entry);
         return entry;
      }
   }

   protected ReadableObjectId createReadableObjectId(ObjectIdGenerator.IdKey key) {
      return new ReadableObjectId(key);
   }

   public void checkUnresolvedObjectId() throws UnresolvedForwardReference {
      if (this._objectIds != null) {
         if (this.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS)) {
            UnresolvedForwardReference exception = null;
            Iterator i$ = this._objectIds.entrySet().iterator();

            while(true) {
               ReadableObjectId roid;
               do {
                  do {
                     if (!i$.hasNext()) {
                        if (exception != null) {
                           throw exception;
                        }

                        return;
                     }

                     Entry<ObjectIdGenerator.IdKey, ReadableObjectId> entry = (Entry)i$.next();
                     roid = (ReadableObjectId)entry.getValue();
                  } while(!roid.hasReferringProperties());
               } while(this.tryToResolveUnresolvedObjectId(roid));

               if (exception == null) {
                  exception = new UnresolvedForwardReference(this.getParser(), "Unresolved forward references for: ");
               }

               Object key = roid.getKey().key;
               Iterator iterator = roid.referringProperties();

               while(iterator.hasNext()) {
                  ReadableObjectId.Referring referring = (ReadableObjectId.Referring)iterator.next();
                  exception.addUnresolvedId(key, referring.getBeanType(), referring.getLocation());
               }
            }
         }
      }
   }

   protected boolean tryToResolveUnresolvedObjectId(ReadableObjectId roid) {
      return roid.tryToResolveUnresolved(this);
   }

   public JsonDeserializer<Object> deserializerInstance(Annotated ann, Object deserDef) throws JsonMappingException {
      if (deserDef == null) {
         return null;
      } else {
         JsonDeserializer deser;
         if (deserDef instanceof JsonDeserializer) {
            deser = (JsonDeserializer)deserDef;
         } else {
            if (!(deserDef instanceof Class)) {
               throw new IllegalStateException("AnnotationIntrospector returned deserializer definition of type " + deserDef.getClass().getName() + "; expected type JsonDeserializer or Class<JsonDeserializer> instead");
            }

            Class<?> deserClass = (Class)deserDef;
            if (deserClass == JsonDeserializer.None.class || ClassUtil.isBogusClass(deserClass)) {
               return null;
            }

            if (!JsonDeserializer.class.isAssignableFrom(deserClass)) {
               throw new IllegalStateException("AnnotationIntrospector returned Class " + deserClass.getName() + "; expected Class<JsonDeserializer>");
            }

            HandlerInstantiator hi = this._config.getHandlerInstantiator();
            deser = hi == null ? null : hi.deserializerInstance(this._config, ann, deserClass);
            if (deser == null) {
               deser = (JsonDeserializer)ClassUtil.createInstance(deserClass, this._config.canOverrideAccessModifiers());
            }
         }

         if (deser instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer)deser).resolve(this);
         }

         return deser;
      }
   }

   public final KeyDeserializer keyDeserializerInstance(Annotated ann, Object deserDef) throws JsonMappingException {
      if (deserDef == null) {
         return null;
      } else {
         KeyDeserializer deser;
         if (deserDef instanceof KeyDeserializer) {
            deser = (KeyDeserializer)deserDef;
         } else {
            if (!(deserDef instanceof Class)) {
               throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + deserDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
            }

            Class<?> deserClass = (Class)deserDef;
            if (deserClass == KeyDeserializer.None.class || ClassUtil.isBogusClass(deserClass)) {
               return null;
            }

            if (!KeyDeserializer.class.isAssignableFrom(deserClass)) {
               throw new IllegalStateException("AnnotationIntrospector returned Class " + deserClass.getName() + "; expected Class<KeyDeserializer>");
            }

            HandlerInstantiator hi = this._config.getHandlerInstantiator();
            deser = hi == null ? null : hi.keyDeserializerInstance(this._config, ann, deserClass);
            if (deser == null) {
               deser = (KeyDeserializer)ClassUtil.createInstance(deserClass, this._config.canOverrideAccessModifiers());
            }
         }

         if (deser instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer)deser).resolve(this);
         }

         return deser;
      }
   }

   public abstract DefaultDeserializationContext with(DeserializerFactory var1);

   public abstract DefaultDeserializationContext createInstance(DeserializationConfig var1, JsonParser var2, InjectableValues var3);

   public static final class Impl extends DefaultDeserializationContext {
      private static final long serialVersionUID = 1L;

      public Impl(DeserializerFactory df) {
         super((DeserializerFactory)df, (DeserializerCache)null);
      }

      protected Impl(DefaultDeserializationContext.Impl src, DeserializationConfig config, JsonParser jp, InjectableValues values) {
         super(src, config, jp, values);
      }

      protected Impl(DefaultDeserializationContext.Impl src) {
         super(src);
      }

      protected Impl(DefaultDeserializationContext.Impl src, DeserializerFactory factory) {
         super((DefaultDeserializationContext)src, (DeserializerFactory)factory);
      }

      public DefaultDeserializationContext copy() {
         ClassUtil.verifyMustOverride(DefaultDeserializationContext.Impl.class, this, "copy");
         return new DefaultDeserializationContext.Impl(this);
      }

      public DefaultDeserializationContext createInstance(DeserializationConfig config, JsonParser p, InjectableValues values) {
         return new DefaultDeserializationContext.Impl(this, config, p, values);
      }

      public DefaultDeserializationContext with(DeserializerFactory factory) {
         return new DefaultDeserializationContext.Impl(this, factory);
      }
   }
}
