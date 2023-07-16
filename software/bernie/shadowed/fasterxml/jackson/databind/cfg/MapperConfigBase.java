package software.bernie.shadowed.fasterxml.jackson.databind.cfg;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonAutoDetect;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonIgnoreProperties;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonSetter;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variant;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyNamingStrategy;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedClass;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.ClassIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.VisibilityChecker;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.util.RootNameLookup;

public abstract class MapperConfigBase<CFG extends ConfigFeature, T extends MapperConfigBase<CFG, T>> extends MapperConfig<T> implements Serializable {
   protected static final ConfigOverride EMPTY_OVERRIDE = ConfigOverride.empty();
   private static final int DEFAULT_MAPPER_FEATURES = collectFeatureDefaults(MapperFeature.class);
   private static final int AUTO_DETECT_MASK;
   protected final SimpleMixInResolver _mixIns;
   protected final SubtypeResolver _subtypeResolver;
   protected final PropertyName _rootName;
   protected final Class<?> _view;
   protected final ContextAttributes _attributes;
   protected final RootNameLookup _rootNames;
   protected final ConfigOverrides _configOverrides;

   protected MapperConfigBase(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
      super(base, DEFAULT_MAPPER_FEATURES);
      this._mixIns = mixins;
      this._subtypeResolver = str;
      this._rootNames = rootNames;
      this._rootName = null;
      this._view = null;
      this._attributes = ContextAttributes.getEmpty();
      this._configOverrides = configOverrides;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
      super(src);
      this._mixIns = mixins;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = configOverrides;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src) {
      super(src);
      this._mixIns = src._mixIns;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, BaseSettings base) {
      super(src, base);
      this._mixIns = src._mixIns;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, int mapperFeatures) {
      super((MapperConfig)src, mapperFeatures);
      this._mixIns = src._mixIns;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, SubtypeResolver str) {
      super(src);
      this._mixIns = src._mixIns;
      this._subtypeResolver = str;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, PropertyName rootName) {
      super(src);
      this._mixIns = src._mixIns;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, Class<?> view) {
      super(src);
      this._mixIns = src._mixIns;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, SimpleMixInResolver mixins) {
      super(src);
      this._mixIns = mixins;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, ContextAttributes attr) {
      super(src);
      this._mixIns = src._mixIns;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = attr;
      this._configOverrides = src._configOverrides;
   }

   protected abstract T _withBase(BaseSettings var1);

   protected abstract T _withMapperFeatures(int var1);

   public final T with(MapperFeature... features) {
      int newMapperFlags = this._mapperFeatures;
      MapperFeature[] arr$ = features;
      int len$ = features.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         MapperFeature f = arr$[i$];
         newMapperFlags |= f.getMask();
      }

      return newMapperFlags == this._mapperFeatures ? this : this._withMapperFeatures(newMapperFlags);
   }

   public final T without(MapperFeature... features) {
      int newMapperFlags = this._mapperFeatures;
      MapperFeature[] arr$ = features;
      int len$ = features.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         MapperFeature f = arr$[i$];
         newMapperFlags &= ~f.getMask();
      }

      return newMapperFlags == this._mapperFeatures ? this : this._withMapperFeatures(newMapperFlags);
   }

   public final T with(MapperFeature feature, boolean state) {
      int newMapperFlags;
      if (state) {
         newMapperFlags = this._mapperFeatures | feature.getMask();
      } else {
         newMapperFlags = this._mapperFeatures & ~feature.getMask();
      }

      return newMapperFlags == this._mapperFeatures ? this : this._withMapperFeatures(newMapperFlags);
   }

   public final T with(AnnotationIntrospector ai) {
      return this._withBase(this._base.withAnnotationIntrospector(ai));
   }

   public final T withAppendedAnnotationIntrospector(AnnotationIntrospector ai) {
      return this._withBase(this._base.withAppendedAnnotationIntrospector(ai));
   }

   public final T withInsertedAnnotationIntrospector(AnnotationIntrospector ai) {
      return this._withBase(this._base.withInsertedAnnotationIntrospector(ai));
   }

   public final T with(ClassIntrospector ci) {
      return this._withBase(this._base.withClassIntrospector(ci));
   }

   public abstract T with(ContextAttributes var1);

   public T withAttributes(Map<?, ?> attributes) {
      return this.with(this.getAttributes().withSharedAttributes(attributes));
   }

   public T withAttribute(Object key, Object value) {
      return this.with(this.getAttributes().withSharedAttribute(key, value));
   }

   public T withoutAttribute(Object key) {
      return this.with(this.getAttributes().withoutSharedAttribute(key));
   }

   public final T with(TypeFactory tf) {
      return this._withBase(this._base.withTypeFactory(tf));
   }

   public final T with(TypeResolverBuilder<?> trb) {
      return this._withBase(this._base.withTypeResolverBuilder(trb));
   }

   public final T with(PropertyNamingStrategy pns) {
      return this._withBase(this._base.withPropertyNamingStrategy(pns));
   }

   public final T with(HandlerInstantiator hi) {
      return this._withBase(this._base.withHandlerInstantiator(hi));
   }

   public final T with(Base64Variant base64) {
      return this._withBase(this._base.with(base64));
   }

   public T with(DateFormat df) {
      return this._withBase(this._base.withDateFormat(df));
   }

   public final T with(Locale l) {
      return this._withBase(this._base.with(l));
   }

   public final T with(TimeZone tz) {
      return this._withBase(this._base.with(tz));
   }

   public abstract T withRootName(PropertyName var1);

   public T withRootName(String rootName) {
      return rootName == null ? this.withRootName((PropertyName)null) : this.withRootName(PropertyName.construct(rootName));
   }

   public abstract T with(SubtypeResolver var1);

   public abstract T withView(Class<?> var1);

   public final SubtypeResolver getSubtypeResolver() {
      return this._subtypeResolver;
   }

   /** @deprecated */
   @Deprecated
   public final String getRootName() {
      return this._rootName == null ? null : this._rootName.getSimpleName();
   }

   public final PropertyName getFullRootName() {
      return this._rootName;
   }

   public final Class<?> getActiveView() {
      return this._view;
   }

   public final ContextAttributes getAttributes() {
      return this._attributes;
   }

   public final ConfigOverride getConfigOverride(Class<?> type) {
      ConfigOverride override = this._configOverrides.findOverride(type);
      return override == null ? EMPTY_OVERRIDE : override;
   }

   public final ConfigOverride findConfigOverride(Class<?> type) {
      return this._configOverrides.findOverride(type);
   }

   public final JsonInclude.Value getDefaultPropertyInclusion() {
      return this._configOverrides.getDefaultInclusion();
   }

   public final JsonInclude.Value getDefaultPropertyInclusion(Class<?> baseType) {
      JsonInclude.Value v = this.getConfigOverride(baseType).getInclude();
      JsonInclude.Value def = this.getDefaultPropertyInclusion();
      return def == null ? v : def.withOverrides(v);
   }

   public final JsonInclude.Value getDefaultInclusion(Class<?> baseType, Class<?> propertyType) {
      JsonInclude.Value v = this.getConfigOverride(propertyType).getIncludeAsProperty();
      JsonInclude.Value def = this.getDefaultPropertyInclusion(baseType);
      return def == null ? v : def.withOverrides(v);
   }

   public final JsonFormat.Value getDefaultPropertyFormat(Class<?> type) {
      ConfigOverride overrides = this._configOverrides.findOverride(type);
      if (overrides != null) {
         JsonFormat.Value v = overrides.getFormat();
         if (v != null) {
            return v;
         }
      }

      return EMPTY_FORMAT;
   }

   public final JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> type) {
      ConfigOverride overrides = this._configOverrides.findOverride(type);
      if (overrides != null) {
         JsonIgnoreProperties.Value v = overrides.getIgnorals();
         if (v != null) {
            return v;
         }
      }

      return null;
   }

   public final JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> baseType, AnnotatedClass actualClass) {
      AnnotationIntrospector intr = this.getAnnotationIntrospector();
      JsonIgnoreProperties.Value base = intr == null ? null : intr.findPropertyIgnorals(actualClass);
      JsonIgnoreProperties.Value overrides = this.getDefaultPropertyIgnorals(baseType);
      return JsonIgnoreProperties.Value.merge(base, overrides);
   }

   public final VisibilityChecker<?> getDefaultVisibilityChecker() {
      VisibilityChecker<?> vchecker = this._configOverrides.getDefaultVisibility();
      if ((this._mapperFeatures & AUTO_DETECT_MASK) != 0) {
         if (!this.isEnabled(MapperFeature.AUTO_DETECT_FIELDS)) {
            vchecker = vchecker.withFieldVisibility(JsonAutoDetect.Visibility.NONE);
         }

         if (!this.isEnabled(MapperFeature.AUTO_DETECT_GETTERS)) {
            vchecker = vchecker.withGetterVisibility(JsonAutoDetect.Visibility.NONE);
         }

         if (!this.isEnabled(MapperFeature.AUTO_DETECT_IS_GETTERS)) {
            vchecker = vchecker.withIsGetterVisibility(JsonAutoDetect.Visibility.NONE);
         }

         if (!this.isEnabled(MapperFeature.AUTO_DETECT_SETTERS)) {
            vchecker = vchecker.withSetterVisibility(JsonAutoDetect.Visibility.NONE);
         }

         if (!this.isEnabled(MapperFeature.AUTO_DETECT_CREATORS)) {
            vchecker = vchecker.withCreatorVisibility(JsonAutoDetect.Visibility.NONE);
         }
      }

      return vchecker;
   }

   public final VisibilityChecker<?> getDefaultVisibilityChecker(Class<?> baseType, AnnotatedClass actualClass) {
      VisibilityChecker<?> vc = this.getDefaultVisibilityChecker();
      AnnotationIntrospector intr = this.getAnnotationIntrospector();
      if (intr != null) {
         vc = intr.findAutoDetectVisibility(actualClass, vc);
      }

      ConfigOverride overrides = this._configOverrides.findOverride(baseType);
      if (overrides != null) {
         vc = vc.withOverrides(overrides.getVisibility());
      }

      return vc;
   }

   public final JsonSetter.Value getDefaultSetterInfo() {
      return this._configOverrides.getDefaultSetterInfo();
   }

   public Boolean getDefaultMergeable() {
      return this._configOverrides.getDefaultMergeable();
   }

   public Boolean getDefaultMergeable(Class<?> baseType) {
      ConfigOverride cfg = this._configOverrides.findOverride(baseType);
      if (cfg != null) {
         Boolean b = cfg.getMergeable();
         if (b != null) {
            return b;
         }
      }

      return this._configOverrides.getDefaultMergeable();
   }

   public PropertyName findRootName(JavaType rootType) {
      return this._rootName != null ? this._rootName : this._rootNames.findRootName((JavaType)rootType, this);
   }

   public PropertyName findRootName(Class<?> rawRootType) {
      return this._rootName != null ? this._rootName : this._rootNames.findRootName((Class)rawRootType, this);
   }

   public final Class<?> findMixInClassFor(Class<?> cls) {
      return this._mixIns.findMixInClassFor(cls);
   }

   public ClassIntrospector.MixInResolver copy() {
      throw new UnsupportedOperationException();
   }

   public final int mixInCount() {
      return this._mixIns.localSize();
   }

   static {
      AUTO_DETECT_MASK = MapperFeature.AUTO_DETECT_FIELDS.getMask() | MapperFeature.AUTO_DETECT_GETTERS.getMask() | MapperFeature.AUTO_DETECT_IS_GETTERS.getMask() | MapperFeature.AUTO_DETECT_SETTERS.getMask() | MapperFeature.AUTO_DETECT_CREATORS.getMask();
   }
}
