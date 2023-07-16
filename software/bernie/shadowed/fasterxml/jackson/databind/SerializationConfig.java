package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.Serializable;
import java.text.DateFormat;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.core.FormatFeature;
import software.bernie.shadowed.fasterxml.jackson.core.JsonFactory;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.PrettyPrinter;
import software.bernie.shadowed.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import software.bernie.shadowed.fasterxml.jackson.core.util.Instantiatable;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.BaseSettings;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.ConfigOverrides;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.ContextAttributes;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfigBase;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.FilterProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.util.RootNameLookup;

public final class SerializationConfig extends MapperConfigBase<SerializationFeature, SerializationConfig> implements Serializable {
   private static final long serialVersionUID = 1L;
   protected static final PrettyPrinter DEFAULT_PRETTY_PRINTER = new DefaultPrettyPrinter();
   protected final FilterProvider _filterProvider;
   protected final PrettyPrinter _defaultPrettyPrinter;
   protected final int _serFeatures;
   protected final int _generatorFeatures;
   protected final int _generatorFeaturesToChange;
   protected final int _formatWriteFeatures;
   protected final int _formatWriteFeaturesToChange;

   public SerializationConfig(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
      super(base, str, mixins, rootNames, configOverrides);
      this._serFeatures = collectFeatureDefaults(SerializationFeature.class);
      this._filterProvider = null;
      this._defaultPrettyPrinter = DEFAULT_PRETTY_PRINTER;
      this._generatorFeatures = 0;
      this._generatorFeaturesToChange = 0;
      this._formatWriteFeatures = 0;
      this._formatWriteFeaturesToChange = 0;
   }

   protected SerializationConfig(SerializationConfig src, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
      super(src, mixins, rootNames, configOverrides);
      this._serFeatures = src._serFeatures;
      this._filterProvider = src._filterProvider;
      this._defaultPrettyPrinter = src._defaultPrettyPrinter;
      this._generatorFeatures = src._generatorFeatures;
      this._generatorFeaturesToChange = src._generatorFeaturesToChange;
      this._formatWriteFeatures = src._formatWriteFeatures;
      this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
   }

   private SerializationConfig(SerializationConfig src, SubtypeResolver str) {
      super(src, (SubtypeResolver)str);
      this._serFeatures = src._serFeatures;
      this._filterProvider = src._filterProvider;
      this._defaultPrettyPrinter = src._defaultPrettyPrinter;
      this._generatorFeatures = src._generatorFeatures;
      this._generatorFeaturesToChange = src._generatorFeaturesToChange;
      this._formatWriteFeatures = src._formatWriteFeatures;
      this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
   }

   private SerializationConfig(SerializationConfig src, int mapperFeatures, int serFeatures, int generatorFeatures, int generatorFeatureMask, int formatFeatures, int formatFeaturesMask) {
      super(src, mapperFeatures);
      this._serFeatures = serFeatures;
      this._filterProvider = src._filterProvider;
      this._defaultPrettyPrinter = src._defaultPrettyPrinter;
      this._generatorFeatures = generatorFeatures;
      this._generatorFeaturesToChange = generatorFeatureMask;
      this._formatWriteFeatures = formatFeatures;
      this._formatWriteFeaturesToChange = formatFeaturesMask;
   }

   private SerializationConfig(SerializationConfig src, BaseSettings base) {
      super(src, (BaseSettings)base);
      this._serFeatures = src._serFeatures;
      this._filterProvider = src._filterProvider;
      this._defaultPrettyPrinter = src._defaultPrettyPrinter;
      this._generatorFeatures = src._generatorFeatures;
      this._generatorFeaturesToChange = src._generatorFeaturesToChange;
      this._formatWriteFeatures = src._formatWriteFeatures;
      this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
   }

   private SerializationConfig(SerializationConfig src, FilterProvider filters) {
      super(src);
      this._serFeatures = src._serFeatures;
      this._filterProvider = filters;
      this._defaultPrettyPrinter = src._defaultPrettyPrinter;
      this._generatorFeatures = src._generatorFeatures;
      this._generatorFeaturesToChange = src._generatorFeaturesToChange;
      this._formatWriteFeatures = src._formatWriteFeatures;
      this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
   }

   private SerializationConfig(SerializationConfig src, Class<?> view) {
      super(src, (Class)view);
      this._serFeatures = src._serFeatures;
      this._filterProvider = src._filterProvider;
      this._defaultPrettyPrinter = src._defaultPrettyPrinter;
      this._generatorFeatures = src._generatorFeatures;
      this._generatorFeaturesToChange = src._generatorFeaturesToChange;
      this._formatWriteFeatures = src._formatWriteFeatures;
      this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
   }

   private SerializationConfig(SerializationConfig src, PropertyName rootName) {
      super(src, (PropertyName)rootName);
      this._serFeatures = src._serFeatures;
      this._filterProvider = src._filterProvider;
      this._defaultPrettyPrinter = src._defaultPrettyPrinter;
      this._generatorFeatures = src._generatorFeatures;
      this._generatorFeaturesToChange = src._generatorFeaturesToChange;
      this._formatWriteFeatures = src._formatWriteFeatures;
      this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
   }

   protected SerializationConfig(SerializationConfig src, ContextAttributes attrs) {
      super(src, (ContextAttributes)attrs);
      this._serFeatures = src._serFeatures;
      this._filterProvider = src._filterProvider;
      this._defaultPrettyPrinter = src._defaultPrettyPrinter;
      this._generatorFeatures = src._generatorFeatures;
      this._generatorFeaturesToChange = src._generatorFeaturesToChange;
      this._formatWriteFeatures = src._formatWriteFeatures;
      this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
   }

   protected SerializationConfig(SerializationConfig src, SimpleMixInResolver mixins) {
      super(src, (SimpleMixInResolver)mixins);
      this._serFeatures = src._serFeatures;
      this._filterProvider = src._filterProvider;
      this._defaultPrettyPrinter = src._defaultPrettyPrinter;
      this._generatorFeatures = src._generatorFeatures;
      this._generatorFeaturesToChange = src._generatorFeaturesToChange;
      this._formatWriteFeatures = src._formatWriteFeatures;
      this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
   }

   protected SerializationConfig(SerializationConfig src, PrettyPrinter defaultPP) {
      super(src);
      this._serFeatures = src._serFeatures;
      this._filterProvider = src._filterProvider;
      this._defaultPrettyPrinter = defaultPP;
      this._generatorFeatures = src._generatorFeatures;
      this._generatorFeaturesToChange = src._generatorFeaturesToChange;
      this._formatWriteFeatures = src._formatWriteFeatures;
      this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
   }

   protected final SerializationConfig _withBase(BaseSettings newBase) {
      return this._base == newBase ? this : new SerializationConfig(this, newBase);
   }

   protected final SerializationConfig _withMapperFeatures(int mapperFeatures) {
      return new SerializationConfig(this, mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
   }

   public SerializationConfig withRootName(PropertyName rootName) {
      if (rootName == null) {
         if (this._rootName == null) {
            return this;
         }
      } else if (rootName.equals(this._rootName)) {
         return this;
      }

      return new SerializationConfig(this, rootName);
   }

   public SerializationConfig with(SubtypeResolver str) {
      return str == this._subtypeResolver ? this : new SerializationConfig(this, str);
   }

   public SerializationConfig withView(Class<?> view) {
      return this._view == view ? this : new SerializationConfig(this, view);
   }

   public SerializationConfig with(ContextAttributes attrs) {
      return attrs == this._attributes ? this : new SerializationConfig(this, attrs);
   }

   public SerializationConfig with(DateFormat df) {
      SerializationConfig cfg = (SerializationConfig)super.with(df);
      return df == null ? cfg.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) : cfg.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
   }

   public SerializationConfig with(SerializationFeature feature) {
      int newSerFeatures = this._serFeatures | feature.getMask();
      return newSerFeatures == this._serFeatures ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
   }

   public SerializationConfig with(SerializationFeature first, SerializationFeature... features) {
      int newSerFeatures = this._serFeatures | first.getMask();
      SerializationFeature[] arr$ = features;
      int len$ = features.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         SerializationFeature f = arr$[i$];
         newSerFeatures |= f.getMask();
      }

      return newSerFeatures == this._serFeatures ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
   }

   public SerializationConfig withFeatures(SerializationFeature... features) {
      int newSerFeatures = this._serFeatures;
      SerializationFeature[] arr$ = features;
      int len$ = features.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         SerializationFeature f = arr$[i$];
         newSerFeatures |= f.getMask();
      }

      return newSerFeatures == this._serFeatures ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
   }

   public SerializationConfig without(SerializationFeature feature) {
      int newSerFeatures = this._serFeatures & ~feature.getMask();
      return newSerFeatures == this._serFeatures ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
   }

   public SerializationConfig without(SerializationFeature first, SerializationFeature... features) {
      int newSerFeatures = this._serFeatures & ~first.getMask();
      SerializationFeature[] arr$ = features;
      int len$ = features.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         SerializationFeature f = arr$[i$];
         newSerFeatures &= ~f.getMask();
      }

      return newSerFeatures == this._serFeatures ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
   }

   public SerializationConfig withoutFeatures(SerializationFeature... features) {
      int newSerFeatures = this._serFeatures;
      SerializationFeature[] arr$ = features;
      int len$ = features.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         SerializationFeature f = arr$[i$];
         newSerFeatures &= ~f.getMask();
      }

      return newSerFeatures == this._serFeatures ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
   }

   public SerializationConfig with(JsonGenerator.Feature feature) {
      int newSet = this._generatorFeatures | feature.getMask();
      int newMask = this._generatorFeaturesToChange | feature.getMask();
      return this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
   }

   public SerializationConfig withFeatures(JsonGenerator.Feature... features) {
      int newSet = this._generatorFeatures;
      int newMask = this._generatorFeaturesToChange;
      JsonGenerator.Feature[] arr$ = features;
      int len$ = features.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         JsonGenerator.Feature f = arr$[i$];
         int mask = f.getMask();
         newSet |= mask;
         newMask |= mask;
      }

      return this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
   }

   public SerializationConfig without(JsonGenerator.Feature feature) {
      int newSet = this._generatorFeatures & ~feature.getMask();
      int newMask = this._generatorFeaturesToChange | feature.getMask();
      return this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
   }

   public SerializationConfig withoutFeatures(JsonGenerator.Feature... features) {
      int newSet = this._generatorFeatures;
      int newMask = this._generatorFeaturesToChange;
      JsonGenerator.Feature[] arr$ = features;
      int len$ = features.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         JsonGenerator.Feature f = arr$[i$];
         int mask = f.getMask();
         newSet &= ~mask;
         newMask |= mask;
      }

      return this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
   }

   public SerializationConfig with(FormatFeature feature) {
      int newSet = this._formatWriteFeatures | feature.getMask();
      int newMask = this._formatWriteFeaturesToChange | feature.getMask();
      return this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
   }

   public SerializationConfig withFeatures(FormatFeature... features) {
      int newSet = this._formatWriteFeatures;
      int newMask = this._formatWriteFeaturesToChange;
      FormatFeature[] arr$ = features;
      int len$ = features.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         FormatFeature f = arr$[i$];
         int mask = f.getMask();
         newSet |= mask;
         newMask |= mask;
      }

      return this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
   }

   public SerializationConfig without(FormatFeature feature) {
      int newSet = this._formatWriteFeatures & ~feature.getMask();
      int newMask = this._formatWriteFeaturesToChange | feature.getMask();
      return this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
   }

   public SerializationConfig withoutFeatures(FormatFeature... features) {
      int newSet = this._formatWriteFeatures;
      int newMask = this._formatWriteFeaturesToChange;
      FormatFeature[] arr$ = features;
      int len$ = features.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         FormatFeature f = arr$[i$];
         int mask = f.getMask();
         newSet &= ~mask;
         newMask |= mask;
      }

      return this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
   }

   public SerializationConfig withFilters(FilterProvider filterProvider) {
      return filterProvider == this._filterProvider ? this : new SerializationConfig(this, filterProvider);
   }

   /** @deprecated */
   @Deprecated
   public SerializationConfig withPropertyInclusion(JsonInclude.Value incl) {
      this._configOverrides.setDefaultInclusion(incl);
      return this;
   }

   public SerializationConfig withDefaultPrettyPrinter(PrettyPrinter pp) {
      return this._defaultPrettyPrinter == pp ? this : new SerializationConfig(this, pp);
   }

   public PrettyPrinter constructDefaultPrettyPrinter() {
      PrettyPrinter pp = this._defaultPrettyPrinter;
      if (pp instanceof Instantiatable) {
         pp = (PrettyPrinter)((Instantiatable)pp).createInstance();
      }

      return pp;
   }

   public void initialize(JsonGenerator g) {
      if (SerializationFeature.INDENT_OUTPUT.enabledIn(this._serFeatures) && g.getPrettyPrinter() == null) {
         PrettyPrinter pp = this.constructDefaultPrettyPrinter();
         if (pp != null) {
            g.setPrettyPrinter(pp);
         }
      }

      boolean useBigDec = SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(this._serFeatures);
      int mask = this._generatorFeaturesToChange;
      if (mask != 0 || useBigDec) {
         int newFlags = this._generatorFeatures;
         if (useBigDec) {
            int f = JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN.getMask();
            newFlags |= f;
            mask |= f;
         }

         g.overrideStdFeatures(newFlags, mask);
      }

      if (this._formatWriteFeaturesToChange != 0) {
         g.overrideFormatFeatures(this._formatWriteFeatures, this._formatWriteFeaturesToChange);
      }

   }

   /** @deprecated */
   @Deprecated
   public JsonInclude.Include getSerializationInclusion() {
      JsonInclude.Include incl = this.getDefaultPropertyInclusion().getValueInclusion();
      return incl == JsonInclude.Include.USE_DEFAULTS ? JsonInclude.Include.ALWAYS : incl;
   }

   public boolean useRootWrapping() {
      if (this._rootName != null) {
         return !this._rootName.isEmpty();
      } else {
         return this.isEnabled(SerializationFeature.WRAP_ROOT_VALUE);
      }
   }

   public final boolean isEnabled(SerializationFeature f) {
      return (this._serFeatures & f.getMask()) != 0;
   }

   public final boolean isEnabled(JsonGenerator.Feature f, JsonFactory factory) {
      int mask = f.getMask();
      if ((this._generatorFeaturesToChange & mask) != 0) {
         return (this._generatorFeatures & f.getMask()) != 0;
      } else {
         return factory.isEnabled(f);
      }
   }

   public final boolean hasSerializationFeatures(int featureMask) {
      return (this._serFeatures & featureMask) == featureMask;
   }

   public final int getSerializationFeatures() {
      return this._serFeatures;
   }

   public FilterProvider getFilterProvider() {
      return this._filterProvider;
   }

   public PrettyPrinter getDefaultPrettyPrinter() {
      return this._defaultPrettyPrinter;
   }

   public <T extends BeanDescription> T introspect(JavaType type) {
      return this.getClassIntrospector().forSerialization(this, type, this);
   }
}
