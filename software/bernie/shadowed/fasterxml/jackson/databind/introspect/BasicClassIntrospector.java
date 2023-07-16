package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.type.SimpleType;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.LRUMap;

public class BasicClassIntrospector extends ClassIntrospector implements Serializable {
   private static final long serialVersionUID = 1L;
   protected static final BasicBeanDescription STRING_DESC = BasicBeanDescription.forOtherUse((MapperConfig)null, SimpleType.constructUnsafe(String.class), AnnotatedClassResolver.createPrimordial(String.class));
   protected static final BasicBeanDescription BOOLEAN_DESC;
   protected static final BasicBeanDescription INT_DESC;
   protected static final BasicBeanDescription LONG_DESC;
   protected final LRUMap<JavaType, BasicBeanDescription> _cachedFCA = new LRUMap(16, 64);

   public BasicBeanDescription forSerialization(SerializationConfig cfg, JavaType type, ClassIntrospector.MixInResolver r) {
      BasicBeanDescription desc = this._findStdTypeDesc(type);
      if (desc == null) {
         desc = this._findStdJdkCollectionDesc(cfg, type);
         if (desc == null) {
            desc = BasicBeanDescription.forSerialization(this.collectProperties(cfg, type, r, true, "set"));
         }

         this._cachedFCA.putIfAbsent(type, desc);
      }

      return desc;
   }

   public BasicBeanDescription forDeserialization(DeserializationConfig cfg, JavaType type, ClassIntrospector.MixInResolver r) {
      BasicBeanDescription desc = this._findStdTypeDesc(type);
      if (desc == null) {
         desc = this._findStdJdkCollectionDesc(cfg, type);
         if (desc == null) {
            desc = BasicBeanDescription.forDeserialization(this.collectProperties(cfg, type, r, false, "set"));
         }

         this._cachedFCA.putIfAbsent(type, desc);
      }

      return desc;
   }

   public BasicBeanDescription forDeserializationWithBuilder(DeserializationConfig cfg, JavaType type, ClassIntrospector.MixInResolver r) {
      BasicBeanDescription desc = BasicBeanDescription.forDeserialization(this.collectPropertiesWithBuilder(cfg, type, r, false));
      this._cachedFCA.putIfAbsent(type, desc);
      return desc;
   }

   public BasicBeanDescription forCreation(DeserializationConfig cfg, JavaType type, ClassIntrospector.MixInResolver r) {
      BasicBeanDescription desc = this._findStdTypeDesc(type);
      if (desc == null) {
         desc = this._findStdJdkCollectionDesc(cfg, type);
         if (desc == null) {
            desc = BasicBeanDescription.forDeserialization(this.collectProperties(cfg, type, r, false, "set"));
         }
      }

      return desc;
   }

   public BasicBeanDescription forClassAnnotations(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
      BasicBeanDescription desc = this._findStdTypeDesc(type);
      if (desc == null) {
         desc = (BasicBeanDescription)this._cachedFCA.get(type);
         if (desc == null) {
            desc = BasicBeanDescription.forOtherUse(config, type, this._resolveAnnotatedClass(config, type, r));
            this._cachedFCA.put(type, desc);
         }
      }

      return desc;
   }

   public BasicBeanDescription forDirectClassAnnotations(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
      BasicBeanDescription desc = this._findStdTypeDesc(type);
      if (desc == null) {
         desc = BasicBeanDescription.forOtherUse(config, type, this._resolveAnnotatedWithoutSuperTypes(config, type, r));
      }

      return desc;
   }

   protected POJOPropertiesCollector collectProperties(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, boolean forSerialization, String mutatorPrefix) {
      return this.constructPropertyCollector(config, this._resolveAnnotatedClass(config, type, r), type, forSerialization, mutatorPrefix);
   }

   protected POJOPropertiesCollector collectPropertiesWithBuilder(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, boolean forSerialization) {
      AnnotatedClass ac = this._resolveAnnotatedClass(config, type, r);
      AnnotationIntrospector ai = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
      JsonPOJOBuilder.Value builderConfig = ai == null ? null : ai.findPOJOBuilderConfig(ac);
      String mutatorPrefix = builderConfig == null ? "with" : builderConfig.withPrefix;
      return this.constructPropertyCollector(config, ac, type, forSerialization, mutatorPrefix);
   }

   protected POJOPropertiesCollector constructPropertyCollector(MapperConfig<?> config, AnnotatedClass ac, JavaType type, boolean forSerialization, String mutatorPrefix) {
      return new POJOPropertiesCollector(config, forSerialization, type, ac, mutatorPrefix);
   }

   protected BasicBeanDescription _findStdTypeDesc(JavaType type) {
      Class<?> cls = type.getRawClass();
      if (cls.isPrimitive()) {
         if (cls == Boolean.TYPE) {
            return BOOLEAN_DESC;
         }

         if (cls == Integer.TYPE) {
            return INT_DESC;
         }

         if (cls == Long.TYPE) {
            return LONG_DESC;
         }
      } else if (cls == String.class) {
         return STRING_DESC;
      }

      return null;
   }

   protected boolean _isStdJDKCollection(JavaType type) {
      if (type.isContainerType() && !type.isArrayType()) {
         Class<?> raw = type.getRawClass();
         String pkgName = ClassUtil.getPackageName(raw);
         return pkgName != null && (pkgName.startsWith("java.lang") || pkgName.startsWith("java.util")) && (Collection.class.isAssignableFrom(raw) || Map.class.isAssignableFrom(raw));
      } else {
         return false;
      }
   }

   protected BasicBeanDescription _findStdJdkCollectionDesc(MapperConfig<?> cfg, JavaType type) {
      return this._isStdJDKCollection(type) ? BasicBeanDescription.forOtherUse(cfg, type, this._resolveAnnotatedClass(cfg, type, cfg)) : null;
   }

   protected AnnotatedClass _resolveAnnotatedClass(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
      return AnnotatedClassResolver.resolve(config, type, r);
   }

   protected AnnotatedClass _resolveAnnotatedWithoutSuperTypes(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
      return AnnotatedClassResolver.resolveWithoutSuperTypes(config, type, r);
   }

   static {
      BOOLEAN_DESC = BasicBeanDescription.forOtherUse((MapperConfig)null, SimpleType.constructUnsafe(Boolean.TYPE), AnnotatedClassResolver.createPrimordial(Boolean.TYPE));
      INT_DESC = BasicBeanDescription.forOtherUse((MapperConfig)null, SimpleType.constructUnsafe(Integer.TYPE), AnnotatedClassResolver.createPrimordial(Integer.TYPE));
      LONG_DESC = BasicBeanDescription.forOtherUse((MapperConfig)null, SimpleType.constructUnsafe(Long.TYPE), AnnotatedClassResolver.createPrimordial(Long.TYPE));
   }
}
