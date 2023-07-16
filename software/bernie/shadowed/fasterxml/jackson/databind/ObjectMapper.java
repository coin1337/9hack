package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonAutoDetect;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonSetter;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonTypeInfo;
import software.bernie.shadowed.fasterxml.jackson.annotation.PropertyAccessor;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variant;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variants;
import software.bernie.shadowed.fasterxml.jackson.core.FormatSchema;
import software.bernie.shadowed.fasterxml.jackson.core.JsonEncoding;
import software.bernie.shadowed.fasterxml.jackson.core.JsonFactory;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerationException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParseException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.ObjectCodec;
import software.bernie.shadowed.fasterxml.jackson.core.PrettyPrinter;
import software.bernie.shadowed.fasterxml.jackson.core.TreeNode;
import software.bernie.shadowed.fasterxml.jackson.core.Version;
import software.bernie.shadowed.fasterxml.jackson.core.Versioned;
import software.bernie.shadowed.fasterxml.jackson.core.io.CharacterEscapes;
import software.bernie.shadowed.fasterxml.jackson.core.io.SegmentedStringWriter;
import software.bernie.shadowed.fasterxml.jackson.core.type.ResolvedType;
import software.bernie.shadowed.fasterxml.jackson.core.type.TypeReference;
import software.bernie.shadowed.fasterxml.jackson.core.util.ByteArrayBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.BaseSettings;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.ConfigOverrides;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.ContextAttributes;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MutableConfigOverride;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.PackageVersion;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.DeserializerFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.Deserializers;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.KeyDeserializers;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ValueInstantiators;
import software.bernie.shadowed.fasterxml.jackson.databind.exc.MismatchedInputException;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.ClassIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.VisibilityChecker;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonschema.JsonSchema;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.NamedType;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.node.ArrayNode;
import software.bernie.shadowed.fasterxml.jackson.databind.node.JsonNodeFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.node.NullNode;
import software.bernie.shadowed.fasterxml.jackson.databind.node.ObjectNode;
import software.bernie.shadowed.fasterxml.jackson.databind.node.POJONode;
import software.bernie.shadowed.fasterxml.jackson.databind.node.TreeTraversingParser;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.FilterProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.SerializerFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.Serializers;
import software.bernie.shadowed.fasterxml.jackson.databind.type.SimpleType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeModifier;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.RootNameLookup;
import software.bernie.shadowed.fasterxml.jackson.databind.util.StdDateFormat;
import software.bernie.shadowed.fasterxml.jackson.databind.util.TokenBuffer;

public class ObjectMapper extends ObjectCodec implements Versioned, Serializable {
   private static final long serialVersionUID = 2L;
   private static final JavaType JSON_NODE_TYPE = SimpleType.constructUnsafe(JsonNode.class);
   protected static final AnnotationIntrospector DEFAULT_ANNOTATION_INTROSPECTOR = new JacksonAnnotationIntrospector();
   protected static final BaseSettings DEFAULT_BASE;
   protected final JsonFactory _jsonFactory;
   protected TypeFactory _typeFactory;
   protected InjectableValues _injectableValues;
   protected SubtypeResolver _subtypeResolver;
   protected final ConfigOverrides _configOverrides;
   protected SimpleMixInResolver _mixIns;
   protected SerializationConfig _serializationConfig;
   protected DefaultSerializerProvider _serializerProvider;
   protected SerializerFactory _serializerFactory;
   protected DeserializationConfig _deserializationConfig;
   protected DefaultDeserializationContext _deserializationContext;
   protected Set<Object> _registeredModuleTypes;
   protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers;

   public ObjectMapper() {
      this((JsonFactory)null, (DefaultSerializerProvider)null, (DefaultDeserializationContext)null);
   }

   public ObjectMapper(JsonFactory jf) {
      this(jf, (DefaultSerializerProvider)null, (DefaultDeserializationContext)null);
   }

   protected ObjectMapper(ObjectMapper src) {
      this._rootDeserializers = new ConcurrentHashMap(64, 0.6F, 2);
      this._jsonFactory = src._jsonFactory.copy();
      this._jsonFactory.setCodec(this);
      this._subtypeResolver = src._subtypeResolver;
      this._typeFactory = src._typeFactory;
      this._injectableValues = src._injectableValues;
      this._configOverrides = src._configOverrides.copy();
      this._mixIns = src._mixIns.copy();
      RootNameLookup rootNames = new RootNameLookup();
      this._serializationConfig = new SerializationConfig(src._serializationConfig, this._mixIns, rootNames, this._configOverrides);
      this._deserializationConfig = new DeserializationConfig(src._deserializationConfig, this._mixIns, rootNames, this._configOverrides);
      this._serializerProvider = src._serializerProvider.copy();
      this._deserializationContext = src._deserializationContext.copy();
      this._serializerFactory = src._serializerFactory;
      Set<Object> reg = src._registeredModuleTypes;
      if (reg == null) {
         this._registeredModuleTypes = null;
      } else {
         this._registeredModuleTypes = new LinkedHashSet(reg);
      }

   }

   public ObjectMapper(JsonFactory jf, DefaultSerializerProvider sp, DefaultDeserializationContext dc) {
      this._rootDeserializers = new ConcurrentHashMap(64, 0.6F, 2);
      if (jf == null) {
         this._jsonFactory = new MappingJsonFactory(this);
      } else {
         this._jsonFactory = jf;
         if (jf.getCodec() == null) {
            this._jsonFactory.setCodec(this);
         }
      }

      this._subtypeResolver = new StdSubtypeResolver();
      RootNameLookup rootNames = new RootNameLookup();
      this._typeFactory = TypeFactory.defaultInstance();
      SimpleMixInResolver mixins = new SimpleMixInResolver((ClassIntrospector.MixInResolver)null);
      this._mixIns = mixins;
      BaseSettings base = DEFAULT_BASE.withClassIntrospector(this.defaultClassIntrospector());
      this._configOverrides = new ConfigOverrides();
      this._serializationConfig = new SerializationConfig(base, this._subtypeResolver, mixins, rootNames, this._configOverrides);
      this._deserializationConfig = new DeserializationConfig(base, this._subtypeResolver, mixins, rootNames, this._configOverrides);
      boolean needOrder = this._jsonFactory.requiresPropertyOrdering();
      if (needOrder ^ this._serializationConfig.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)) {
         this.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, needOrder);
      }

      this._serializerProvider = (DefaultSerializerProvider)(sp == null ? new DefaultSerializerProvider.Impl() : sp);
      this._deserializationContext = (DefaultDeserializationContext)(dc == null ? new DefaultDeserializationContext.Impl(BeanDeserializerFactory.instance) : dc);
      this._serializerFactory = BeanSerializerFactory.instance;
   }

   protected ClassIntrospector defaultClassIntrospector() {
      return new BasicClassIntrospector();
   }

   public ObjectMapper copy() {
      this._checkInvalidCopy(ObjectMapper.class);
      return new ObjectMapper(this);
   }

   protected void _checkInvalidCopy(Class<?> exp) {
      if (this.getClass() != exp) {
         throw new IllegalStateException("Failed copy(): " + this.getClass().getName() + " (version: " + this.version() + ") does not override copy(); it has to");
      }
   }

   protected ObjectReader _newReader(DeserializationConfig config) {
      return new ObjectReader(this, config);
   }

   protected ObjectReader _newReader(DeserializationConfig config, JavaType valueType, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues) {
      return new ObjectReader(this, config, valueType, valueToUpdate, schema, injectableValues);
   }

   protected ObjectWriter _newWriter(SerializationConfig config) {
      return new ObjectWriter(this, config);
   }

   protected ObjectWriter _newWriter(SerializationConfig config, FormatSchema schema) {
      return new ObjectWriter(this, config, schema);
   }

   protected ObjectWriter _newWriter(SerializationConfig config, JavaType rootType, PrettyPrinter pp) {
      return new ObjectWriter(this, config, rootType, pp);
   }

   public Version version() {
      return PackageVersion.VERSION;
   }

   public ObjectMapper registerModule(Module module) {
      if (this.isEnabled(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS)) {
         Object typeId = module.getTypeId();
         if (typeId != null) {
            if (this._registeredModuleTypes == null) {
               this._registeredModuleTypes = new LinkedHashSet();
            }

            if (!this._registeredModuleTypes.add(typeId)) {
               return this;
            }
         }
      }

      String name = module.getModuleName();
      if (name == null) {
         throw new IllegalArgumentException("Module without defined name");
      } else {
         Version version = module.version();
         if (version == null) {
            throw new IllegalArgumentException("Module without defined version");
         } else {
            module.setupModule(new Module.SetupContext() {
               public Version getMapperVersion() {
                  return ObjectMapper.this.version();
               }

               public <C extends ObjectCodec> C getOwner() {
                  return ObjectMapper.this;
               }

               public TypeFactory getTypeFactory() {
                  return ObjectMapper.this._typeFactory;
               }

               public boolean isEnabled(MapperFeature f) {
                  return ObjectMapper.this.isEnabled(f);
               }

               public boolean isEnabled(DeserializationFeature f) {
                  return ObjectMapper.this.isEnabled(f);
               }

               public boolean isEnabled(SerializationFeature f) {
                  return ObjectMapper.this.isEnabled(f);
               }

               public boolean isEnabled(JsonFactory.Feature f) {
                  return ObjectMapper.this.isEnabled(f);
               }

               public boolean isEnabled(JsonParser.Feature f) {
                  return ObjectMapper.this.isEnabled(f);
               }

               public boolean isEnabled(JsonGenerator.Feature f) {
                  return ObjectMapper.this.isEnabled(f);
               }

               public MutableConfigOverride configOverride(Class<?> type) {
                  return ObjectMapper.this.configOverride(type);
               }

               public void addDeserializers(Deserializers d) {
                  DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAdditionalDeserializers(d);
                  ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
               }

               public void addKeyDeserializers(KeyDeserializers d) {
                  DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAdditionalKeyDeserializers(d);
                  ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
               }

               public void addBeanDeserializerModifier(BeanDeserializerModifier modifier) {
                  DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withDeserializerModifier(modifier);
                  ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
               }

               public void addSerializers(Serializers s) {
                  ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withAdditionalSerializers(s);
               }

               public void addKeySerializers(Serializers s) {
                  ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withAdditionalKeySerializers(s);
               }

               public void addBeanSerializerModifier(BeanSerializerModifier modifier) {
                  ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withSerializerModifier(modifier);
               }

               public void addAbstractTypeResolver(AbstractTypeResolver resolver) {
                  DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAbstractTypeResolver(resolver);
                  ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
               }

               public void addTypeModifier(TypeModifier modifier) {
                  TypeFactory f = ObjectMapper.this._typeFactory;
                  f = f.withModifier(modifier);
                  ObjectMapper.this.setTypeFactory(f);
               }

               public void addValueInstantiators(ValueInstantiators instantiators) {
                  DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withValueInstantiators(instantiators);
                  ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
               }

               public void setClassIntrospector(ClassIntrospector ci) {
                  ObjectMapper.this._deserializationConfig = (DeserializationConfig)ObjectMapper.this._deserializationConfig.with((ClassIntrospector)ci);
                  ObjectMapper.this._serializationConfig = (SerializationConfig)ObjectMapper.this._serializationConfig.with((ClassIntrospector)ci);
               }

               public void insertAnnotationIntrospector(AnnotationIntrospector ai) {
                  ObjectMapper.this._deserializationConfig = (DeserializationConfig)ObjectMapper.this._deserializationConfig.withInsertedAnnotationIntrospector(ai);
                  ObjectMapper.this._serializationConfig = (SerializationConfig)ObjectMapper.this._serializationConfig.withInsertedAnnotationIntrospector(ai);
               }

               public void appendAnnotationIntrospector(AnnotationIntrospector ai) {
                  ObjectMapper.this._deserializationConfig = (DeserializationConfig)ObjectMapper.this._deserializationConfig.withAppendedAnnotationIntrospector(ai);
                  ObjectMapper.this._serializationConfig = (SerializationConfig)ObjectMapper.this._serializationConfig.withAppendedAnnotationIntrospector(ai);
               }

               public void registerSubtypes(Class<?>... subtypes) {
                  ObjectMapper.this.registerSubtypes(subtypes);
               }

               public void registerSubtypes(NamedType... subtypes) {
                  ObjectMapper.this.registerSubtypes(subtypes);
               }

               public void registerSubtypes(Collection<Class<?>> subtypes) {
                  ObjectMapper.this.registerSubtypes(subtypes);
               }

               public void setMixInAnnotations(Class<?> target, Class<?> mixinSource) {
                  ObjectMapper.this.addMixIn(target, mixinSource);
               }

               public void addDeserializationProblemHandler(DeserializationProblemHandler handler) {
                  ObjectMapper.this.addHandler(handler);
               }

               public void setNamingStrategy(PropertyNamingStrategy naming) {
                  ObjectMapper.this.setPropertyNamingStrategy(naming);
               }
            });
            return this;
         }
      }
   }

   public ObjectMapper registerModules(Module... modules) {
      Module[] arr$ = modules;
      int len$ = modules.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Module module = arr$[i$];
         this.registerModule(module);
      }

      return this;
   }

   public ObjectMapper registerModules(Iterable<Module> modules) {
      Iterator i$ = modules.iterator();

      while(i$.hasNext()) {
         Module module = (Module)i$.next();
         this.registerModule(module);
      }

      return this;
   }

   public static List<Module> findModules() {
      return findModules((ClassLoader)null);
   }

   public static List<Module> findModules(ClassLoader classLoader) {
      ArrayList<Module> modules = new ArrayList();
      ServiceLoader<Module> loader = secureGetServiceLoader(Module.class, classLoader);
      Iterator i$ = loader.iterator();

      while(i$.hasNext()) {
         Module module = (Module)i$.next();
         modules.add(module);
      }

      return modules;
   }

   private static <T> ServiceLoader<T> secureGetServiceLoader(final Class<T> clazz, final ClassLoader classLoader) {
      SecurityManager sm = System.getSecurityManager();
      if (sm == null) {
         return classLoader == null ? ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
      } else {
         return (ServiceLoader)AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<T>>() {
            public ServiceLoader<T> run() {
               return classLoader == null ? ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
            }
         });
      }
   }

   public ObjectMapper findAndRegisterModules() {
      return this.registerModules((Iterable)findModules());
   }

   public SerializationConfig getSerializationConfig() {
      return this._serializationConfig;
   }

   public DeserializationConfig getDeserializationConfig() {
      return this._deserializationConfig;
   }

   public DeserializationContext getDeserializationContext() {
      return this._deserializationContext;
   }

   public ObjectMapper setSerializerFactory(SerializerFactory f) {
      this._serializerFactory = f;
      return this;
   }

   public SerializerFactory getSerializerFactory() {
      return this._serializerFactory;
   }

   public ObjectMapper setSerializerProvider(DefaultSerializerProvider p) {
      this._serializerProvider = p;
      return this;
   }

   public SerializerProvider getSerializerProvider() {
      return this._serializerProvider;
   }

   public SerializerProvider getSerializerProviderInstance() {
      return this._serializerProvider(this._serializationConfig);
   }

   public ObjectMapper setMixIns(Map<Class<?>, Class<?>> sourceMixins) {
      this._mixIns.setLocalDefinitions(sourceMixins);
      return this;
   }

   public ObjectMapper addMixIn(Class<?> target, Class<?> mixinSource) {
      this._mixIns.addLocalDefinition(target, mixinSource);
      return this;
   }

   public ObjectMapper setMixInResolver(ClassIntrospector.MixInResolver resolver) {
      SimpleMixInResolver r = this._mixIns.withOverrides(resolver);
      if (r != this._mixIns) {
         this._mixIns = r;
         this._deserializationConfig = new DeserializationConfig(this._deserializationConfig, r);
         this._serializationConfig = new SerializationConfig(this._serializationConfig, r);
      }

      return this;
   }

   public Class<?> findMixInClassFor(Class<?> cls) {
      return this._mixIns.findMixInClassFor(cls);
   }

   public int mixInCount() {
      return this._mixIns.localSize();
   }

   /** @deprecated */
   @Deprecated
   public void setMixInAnnotations(Map<Class<?>, Class<?>> sourceMixins) {
      this.setMixIns(sourceMixins);
   }

   /** @deprecated */
   @Deprecated
   public final void addMixInAnnotations(Class<?> target, Class<?> mixinSource) {
      this.addMixIn(target, mixinSource);
   }

   public VisibilityChecker<?> getVisibilityChecker() {
      return this._serializationConfig.getDefaultVisibilityChecker();
   }

   public ObjectMapper setVisibility(VisibilityChecker<?> vc) {
      this._configOverrides.setDefaultVisibility(vc);
      return this;
   }

   public ObjectMapper setVisibility(PropertyAccessor forMethod, JsonAutoDetect.Visibility visibility) {
      VisibilityChecker<?> vc = this._configOverrides.getDefaultVisibility();
      vc = vc.withVisibility(forMethod, visibility);
      this._configOverrides.setDefaultVisibility(vc);
      return this;
   }

   public SubtypeResolver getSubtypeResolver() {
      return this._subtypeResolver;
   }

   public ObjectMapper setSubtypeResolver(SubtypeResolver str) {
      this._subtypeResolver = str;
      this._deserializationConfig = this._deserializationConfig.with(str);
      this._serializationConfig = this._serializationConfig.with(str);
      return this;
   }

   public ObjectMapper setAnnotationIntrospector(AnnotationIntrospector ai) {
      this._serializationConfig = (SerializationConfig)this._serializationConfig.with((AnnotationIntrospector)ai);
      this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with((AnnotationIntrospector)ai);
      return this;
   }

   public ObjectMapper setAnnotationIntrospectors(AnnotationIntrospector serializerAI, AnnotationIntrospector deserializerAI) {
      this._serializationConfig = (SerializationConfig)this._serializationConfig.with((AnnotationIntrospector)serializerAI);
      this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with((AnnotationIntrospector)deserializerAI);
      return this;
   }

   public ObjectMapper setPropertyNamingStrategy(PropertyNamingStrategy s) {
      this._serializationConfig = (SerializationConfig)this._serializationConfig.with((PropertyNamingStrategy)s);
      this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with((PropertyNamingStrategy)s);
      return this;
   }

   public PropertyNamingStrategy getPropertyNamingStrategy() {
      return this._serializationConfig.getPropertyNamingStrategy();
   }

   public ObjectMapper setDefaultPrettyPrinter(PrettyPrinter pp) {
      this._serializationConfig = this._serializationConfig.withDefaultPrettyPrinter(pp);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public void setVisibilityChecker(VisibilityChecker<?> vc) {
      this.setVisibility(vc);
   }

   public ObjectMapper setSerializationInclusion(JsonInclude.Include incl) {
      this.setPropertyInclusion(JsonInclude.Value.construct(incl, incl));
      return this;
   }

   /** @deprecated */
   @Deprecated
   public ObjectMapper setPropertyInclusion(JsonInclude.Value incl) {
      return this.setDefaultPropertyInclusion(incl);
   }

   public ObjectMapper setDefaultPropertyInclusion(JsonInclude.Value incl) {
      this._configOverrides.setDefaultInclusion(incl);
      return this;
   }

   public ObjectMapper setDefaultPropertyInclusion(JsonInclude.Include incl) {
      this._configOverrides.setDefaultInclusion(JsonInclude.Value.construct(incl, incl));
      return this;
   }

   public ObjectMapper setDefaultSetterInfo(JsonSetter.Value v) {
      this._configOverrides.setDefaultSetterInfo(v);
      return this;
   }

   public ObjectMapper setDefaultVisibility(JsonAutoDetect.Value vis) {
      this._configOverrides.setDefaultVisibility(VisibilityChecker.Std.construct(vis));
      return this;
   }

   public ObjectMapper setDefaultMergeable(Boolean b) {
      this._configOverrides.setDefaultMergeable(b);
      return this;
   }

   public ObjectMapper enableDefaultTyping() {
      return this.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
   }

   public ObjectMapper enableDefaultTyping(ObjectMapper.DefaultTyping dti) {
      return this.enableDefaultTyping(dti, JsonTypeInfo.As.WRAPPER_ARRAY);
   }

   public ObjectMapper enableDefaultTyping(ObjectMapper.DefaultTyping applicability, JsonTypeInfo.As includeAs) {
      if (includeAs == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
         throw new IllegalArgumentException("Cannot use includeAs of " + includeAs);
      } else {
         TypeResolverBuilder<?> typer = new ObjectMapper.DefaultTypeResolverBuilder(applicability);
         TypeResolverBuilder<?> typer = typer.init(JsonTypeInfo.Id.CLASS, (TypeIdResolver)null);
         typer = typer.inclusion(includeAs);
         return this.setDefaultTyping(typer);
      }
   }

   public ObjectMapper enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping applicability, String propertyName) {
      TypeResolverBuilder<?> typer = new ObjectMapper.DefaultTypeResolverBuilder(applicability);
      TypeResolverBuilder<?> typer = typer.init(JsonTypeInfo.Id.CLASS, (TypeIdResolver)null);
      typer = typer.inclusion(JsonTypeInfo.As.PROPERTY);
      typer = typer.typeProperty(propertyName);
      return this.setDefaultTyping(typer);
   }

   public ObjectMapper disableDefaultTyping() {
      return this.setDefaultTyping((TypeResolverBuilder)null);
   }

   public ObjectMapper setDefaultTyping(TypeResolverBuilder<?> typer) {
      this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with((TypeResolverBuilder)typer);
      this._serializationConfig = (SerializationConfig)this._serializationConfig.with((TypeResolverBuilder)typer);
      return this;
   }

   public void registerSubtypes(Class<?>... classes) {
      this.getSubtypeResolver().registerSubtypes(classes);
   }

   public void registerSubtypes(NamedType... types) {
      this.getSubtypeResolver().registerSubtypes(types);
   }

   public void registerSubtypes(Collection<Class<?>> subtypes) {
      this.getSubtypeResolver().registerSubtypes(subtypes);
   }

   public MutableConfigOverride configOverride(Class<?> type) {
      return this._configOverrides.findOrCreateOverride(type);
   }

   public TypeFactory getTypeFactory() {
      return this._typeFactory;
   }

   public ObjectMapper setTypeFactory(TypeFactory f) {
      this._typeFactory = f;
      this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with((TypeFactory)f);
      this._serializationConfig = (SerializationConfig)this._serializationConfig.with((TypeFactory)f);
      return this;
   }

   public JavaType constructType(Type t) {
      return this._typeFactory.constructType(t);
   }

   public JsonNodeFactory getNodeFactory() {
      return this._deserializationConfig.getNodeFactory();
   }

   public ObjectMapper setNodeFactory(JsonNodeFactory f) {
      this._deserializationConfig = this._deserializationConfig.with(f);
      return this;
   }

   public ObjectMapper addHandler(DeserializationProblemHandler h) {
      this._deserializationConfig = this._deserializationConfig.withHandler(h);
      return this;
   }

   public ObjectMapper clearProblemHandlers() {
      this._deserializationConfig = this._deserializationConfig.withNoProblemHandlers();
      return this;
   }

   public ObjectMapper setConfig(DeserializationConfig config) {
      this._deserializationConfig = config;
      return this;
   }

   /** @deprecated */
   @Deprecated
   public void setFilters(FilterProvider filterProvider) {
      this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
   }

   public ObjectMapper setFilterProvider(FilterProvider filterProvider) {
      this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
      return this;
   }

   public ObjectMapper setBase64Variant(Base64Variant v) {
      this._serializationConfig = (SerializationConfig)this._serializationConfig.with((Base64Variant)v);
      this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with((Base64Variant)v);
      return this;
   }

   public ObjectMapper setConfig(SerializationConfig config) {
      this._serializationConfig = config;
      return this;
   }

   public JsonFactory getFactory() {
      return this._jsonFactory;
   }

   /** @deprecated */
   @Deprecated
   public JsonFactory getJsonFactory() {
      return this.getFactory();
   }

   public ObjectMapper setDateFormat(DateFormat dateFormat) {
      this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with((DateFormat)dateFormat);
      this._serializationConfig = this._serializationConfig.with(dateFormat);
      return this;
   }

   public DateFormat getDateFormat() {
      return this._serializationConfig.getDateFormat();
   }

   public Object setHandlerInstantiator(HandlerInstantiator hi) {
      this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with((HandlerInstantiator)hi);
      this._serializationConfig = (SerializationConfig)this._serializationConfig.with((HandlerInstantiator)hi);
      return this;
   }

   public ObjectMapper setInjectableValues(InjectableValues injectableValues) {
      this._injectableValues = injectableValues;
      return this;
   }

   public InjectableValues getInjectableValues() {
      return this._injectableValues;
   }

   public ObjectMapper setLocale(Locale l) {
      this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with((Locale)l);
      this._serializationConfig = (SerializationConfig)this._serializationConfig.with((Locale)l);
      return this;
   }

   public ObjectMapper setTimeZone(TimeZone tz) {
      this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with((TimeZone)tz);
      this._serializationConfig = (SerializationConfig)this._serializationConfig.with((TimeZone)tz);
      return this;
   }

   public boolean isEnabled(MapperFeature f) {
      return this._serializationConfig.isEnabled(f);
   }

   public ObjectMapper configure(MapperFeature f, boolean state) {
      this._serializationConfig = state ? (SerializationConfig)this._serializationConfig.with((MapperFeature[])(new MapperFeature[]{f})) : (SerializationConfig)this._serializationConfig.without((MapperFeature[])(new MapperFeature[]{f}));
      this._deserializationConfig = state ? (DeserializationConfig)this._deserializationConfig.with((MapperFeature[])(new MapperFeature[]{f})) : (DeserializationConfig)this._deserializationConfig.without((MapperFeature[])(new MapperFeature[]{f}));
      return this;
   }

   public ObjectMapper enable(MapperFeature... f) {
      this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.with((MapperFeature[])f);
      this._serializationConfig = (SerializationConfig)this._serializationConfig.with((MapperFeature[])f);
      return this;
   }

   public ObjectMapper disable(MapperFeature... f) {
      this._deserializationConfig = (DeserializationConfig)this._deserializationConfig.without((MapperFeature[])f);
      this._serializationConfig = (SerializationConfig)this._serializationConfig.without((MapperFeature[])f);
      return this;
   }

   public boolean isEnabled(SerializationFeature f) {
      return this._serializationConfig.isEnabled(f);
   }

   public ObjectMapper configure(SerializationFeature f, boolean state) {
      this._serializationConfig = state ? this._serializationConfig.with(f) : this._serializationConfig.without(f);
      return this;
   }

   public ObjectMapper enable(SerializationFeature f) {
      this._serializationConfig = this._serializationConfig.with(f);
      return this;
   }

   public ObjectMapper enable(SerializationFeature first, SerializationFeature... f) {
      this._serializationConfig = this._serializationConfig.with(first, f);
      return this;
   }

   public ObjectMapper disable(SerializationFeature f) {
      this._serializationConfig = this._serializationConfig.without(f);
      return this;
   }

   public ObjectMapper disable(SerializationFeature first, SerializationFeature... f) {
      this._serializationConfig = this._serializationConfig.without(first, f);
      return this;
   }

   public boolean isEnabled(DeserializationFeature f) {
      return this._deserializationConfig.isEnabled(f);
   }

   public ObjectMapper configure(DeserializationFeature f, boolean state) {
      this._deserializationConfig = state ? this._deserializationConfig.with(f) : this._deserializationConfig.without(f);
      return this;
   }

   public ObjectMapper enable(DeserializationFeature feature) {
      this._deserializationConfig = this._deserializationConfig.with(feature);
      return this;
   }

   public ObjectMapper enable(DeserializationFeature first, DeserializationFeature... f) {
      this._deserializationConfig = this._deserializationConfig.with(first, f);
      return this;
   }

   public ObjectMapper disable(DeserializationFeature feature) {
      this._deserializationConfig = this._deserializationConfig.without(feature);
      return this;
   }

   public ObjectMapper disable(DeserializationFeature first, DeserializationFeature... f) {
      this._deserializationConfig = this._deserializationConfig.without(first, f);
      return this;
   }

   public boolean isEnabled(JsonParser.Feature f) {
      return this._deserializationConfig.isEnabled(f, this._jsonFactory);
   }

   public ObjectMapper configure(JsonParser.Feature f, boolean state) {
      this._jsonFactory.configure(f, state);
      return this;
   }

   public ObjectMapper enable(JsonParser.Feature... features) {
      JsonParser.Feature[] arr$ = features;
      int len$ = features.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         JsonParser.Feature f = arr$[i$];
         this._jsonFactory.enable(f);
      }

      return this;
   }

   public ObjectMapper disable(JsonParser.Feature... features) {
      JsonParser.Feature[] arr$ = features;
      int len$ = features.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         JsonParser.Feature f = arr$[i$];
         this._jsonFactory.disable(f);
      }

      return this;
   }

   public boolean isEnabled(JsonGenerator.Feature f) {
      return this._serializationConfig.isEnabled(f, this._jsonFactory);
   }

   public ObjectMapper configure(JsonGenerator.Feature f, boolean state) {
      this._jsonFactory.configure(f, state);
      return this;
   }

   public ObjectMapper enable(JsonGenerator.Feature... features) {
      JsonGenerator.Feature[] arr$ = features;
      int len$ = features.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         JsonGenerator.Feature f = arr$[i$];
         this._jsonFactory.enable(f);
      }

      return this;
   }

   public ObjectMapper disable(JsonGenerator.Feature... features) {
      JsonGenerator.Feature[] arr$ = features;
      int len$ = features.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         JsonGenerator.Feature f = arr$[i$];
         this._jsonFactory.disable(f);
      }

      return this;
   }

   public boolean isEnabled(JsonFactory.Feature f) {
      return this._jsonFactory.isEnabled(f);
   }

   public <T> T readValue(JsonParser p, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readValue(this.getDeserializationConfig(), p, this._typeFactory.constructType((Type)valueType));
   }

   public <T> T readValue(JsonParser p, TypeReference<?> valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
      return this._readValue(this.getDeserializationConfig(), p, this._typeFactory.constructType(valueTypeRef));
   }

   public final <T> T readValue(JsonParser p, ResolvedType valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readValue(this.getDeserializationConfig(), p, (JavaType)valueType);
   }

   public <T> T readValue(JsonParser p, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readValue(this.getDeserializationConfig(), p, valueType);
   }

   public <T extends TreeNode> T readTree(JsonParser p) throws IOException, JsonProcessingException {
      DeserializationConfig cfg = this.getDeserializationConfig();
      JsonToken t = p.getCurrentToken();
      if (t == null) {
         t = p.nextToken();
         if (t == null) {
            return null;
         }
      }

      JsonNode n = (JsonNode)this._readValue(cfg, p, JSON_NODE_TYPE);
      if (n == null) {
         n = this.getNodeFactory().nullNode();
      }

      return (TreeNode)n;
   }

   public <T> MappingIterator<T> readValues(JsonParser p, ResolvedType valueType) throws IOException, JsonProcessingException {
      return this.readValues(p, (JavaType)valueType);
   }

   public <T> MappingIterator<T> readValues(JsonParser p, JavaType valueType) throws IOException, JsonProcessingException {
      DeserializationConfig config = this.getDeserializationConfig();
      DeserializationContext ctxt = this.createDeserializationContext(p, config);
      JsonDeserializer<?> deser = this._findRootDeserializer(ctxt, valueType);
      return new MappingIterator(valueType, p, ctxt, deser, false, (Object)null);
   }

   public <T> MappingIterator<T> readValues(JsonParser p, Class<T> valueType) throws IOException, JsonProcessingException {
      return this.readValues(p, this._typeFactory.constructType((Type)valueType));
   }

   public <T> MappingIterator<T> readValues(JsonParser p, TypeReference<?> valueTypeRef) throws IOException, JsonProcessingException {
      return this.readValues(p, this._typeFactory.constructType(valueTypeRef));
   }

   public JsonNode readTree(InputStream in) throws IOException {
      return this._readTreeAndClose(this._jsonFactory.createParser(in));
   }

   public JsonNode readTree(Reader r) throws IOException {
      return this._readTreeAndClose(this._jsonFactory.createParser(r));
   }

   public JsonNode readTree(String content) throws IOException {
      return this._readTreeAndClose(this._jsonFactory.createParser(content));
   }

   public JsonNode readTree(byte[] content) throws IOException {
      return this._readTreeAndClose(this._jsonFactory.createParser(content));
   }

   public JsonNode readTree(File file) throws IOException, JsonProcessingException {
      return this._readTreeAndClose(this._jsonFactory.createParser(file));
   }

   public JsonNode readTree(URL source) throws IOException {
      return this._readTreeAndClose(this._jsonFactory.createParser(source));
   }

   public void writeValue(JsonGenerator g, Object value) throws IOException, JsonGenerationException, JsonMappingException {
      SerializationConfig config = this.getSerializationConfig();
      if (config.isEnabled(SerializationFeature.INDENT_OUTPUT) && g.getPrettyPrinter() == null) {
         g.setPrettyPrinter(config.constructDefaultPrettyPrinter());
      }

      if (config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
         this._writeCloseableValue(g, value, config);
      } else {
         this._serializerProvider(config).serializeValue(g, value);
         if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
            g.flush();
         }
      }

   }

   public void writeTree(JsonGenerator jgen, TreeNode rootNode) throws IOException, JsonProcessingException {
      SerializationConfig config = this.getSerializationConfig();
      this._serializerProvider(config).serializeValue(jgen, rootNode);
      if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
         jgen.flush();
      }

   }

   public void writeTree(JsonGenerator jgen, JsonNode rootNode) throws IOException, JsonProcessingException {
      SerializationConfig config = this.getSerializationConfig();
      this._serializerProvider(config).serializeValue(jgen, rootNode);
      if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
         jgen.flush();
      }

   }

   public ObjectNode createObjectNode() {
      return this._deserializationConfig.getNodeFactory().objectNode();
   }

   public ArrayNode createArrayNode() {
      return this._deserializationConfig.getNodeFactory().arrayNode();
   }

   public JsonParser treeAsTokens(TreeNode n) {
      return new TreeTraversingParser((JsonNode)n, this);
   }

   public <T> T treeToValue(TreeNode n, Class<T> valueType) throws JsonProcessingException {
      try {
         if (valueType != Object.class && valueType.isAssignableFrom(n.getClass())) {
            return n;
         } else {
            if (n.asToken() == JsonToken.VALUE_EMBEDDED_OBJECT && n instanceof POJONode) {
               Object ob = ((POJONode)n).getPojo();
               if (ob == null || valueType.isInstance(ob)) {
                  return ob;
               }
            }

            return this.readValue(this.treeAsTokens(n), valueType);
         }
      } catch (JsonProcessingException var4) {
         throw var4;
      } catch (IOException var5) {
         throw new IllegalArgumentException(var5.getMessage(), var5);
      }
   }

   public <T extends JsonNode> T valueToTree(Object fromValue) throws IllegalArgumentException {
      if (fromValue == null) {
         return null;
      } else {
         TokenBuffer buf = new TokenBuffer(this, false);
         if (this.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            buf = buf.forceUseOfBigDecimal(true);
         }

         try {
            this.writeValue((JsonGenerator)buf, fromValue);
            JsonParser p = buf.asParser();
            JsonNode result = (JsonNode)this.readTree(p);
            p.close();
            return result;
         } catch (IOException var5) {
            throw new IllegalArgumentException(var5.getMessage(), var5);
         }
      }
   }

   public boolean canSerialize(Class<?> type) {
      return this._serializerProvider(this.getSerializationConfig()).hasSerializerFor(type, (AtomicReference)null);
   }

   public boolean canSerialize(Class<?> type, AtomicReference<Throwable> cause) {
      return this._serializerProvider(this.getSerializationConfig()).hasSerializerFor(type, cause);
   }

   public boolean canDeserialize(JavaType type) {
      return this.createDeserializationContext((JsonParser)null, this.getDeserializationConfig()).hasValueDeserializerFor(type, (AtomicReference)null);
   }

   public boolean canDeserialize(JavaType type, AtomicReference<Throwable> cause) {
      return this.createDeserializationContext((JsonParser)null, this.getDeserializationConfig()).hasValueDeserializerFor(type, cause);
   }

   public <T> T readValue(File src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType((Type)valueType));
   }

   public <T> T readValue(File src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
   }

   public <T> T readValue(File src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
   }

   public <T> T readValue(URL src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType((Type)valueType));
   }

   public <T> T readValue(URL src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
   }

   public <T> T readValue(URL src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
   }

   public <T> T readValue(String content, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(content), this._typeFactory.constructType((Type)valueType));
   }

   public <T> T readValue(String content, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(content), this._typeFactory.constructType(valueTypeRef));
   }

   public <T> T readValue(String content, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(content), valueType);
   }

   public <T> T readValue(Reader src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType((Type)valueType));
   }

   public <T> T readValue(Reader src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
   }

   public <T> T readValue(Reader src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
   }

   public <T> T readValue(InputStream src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType((Type)valueType));
   }

   public <T> T readValue(InputStream src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
   }

   public <T> T readValue(InputStream src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
   }

   public <T> T readValue(byte[] src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType((Type)valueType));
   }

   public <T> T readValue(byte[] src, int offset, int len, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType((Type)valueType));
   }

   public <T> T readValue(byte[] src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
   }

   public <T> T readValue(byte[] src, int offset, int len, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueTypeRef));
   }

   public <T> T readValue(byte[] src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
   }

   public <T> T readValue(byte[] src, int offset, int len, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
      return this._readMapAndClose(this._jsonFactory.createParser(src, offset, len), valueType);
   }

   public <T> T readValue(DataInput src, Class<T> valueType) throws IOException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType((Type)valueType));
   }

   public <T> T readValue(DataInput src, JavaType valueType) throws IOException {
      return this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
   }

   public void writeValue(File resultFile, Object value) throws IOException, JsonGenerationException, JsonMappingException {
      this._configAndWriteValue(this._jsonFactory.createGenerator(resultFile, JsonEncoding.UTF8), value);
   }

   public void writeValue(OutputStream out, Object value) throws IOException, JsonGenerationException, JsonMappingException {
      this._configAndWriteValue(this._jsonFactory.createGenerator(out, JsonEncoding.UTF8), value);
   }

   public void writeValue(DataOutput out, Object value) throws IOException {
      this._configAndWriteValue(this._jsonFactory.createGenerator(out, JsonEncoding.UTF8), value);
   }

   public void writeValue(Writer w, Object value) throws IOException, JsonGenerationException, JsonMappingException {
      this._configAndWriteValue(this._jsonFactory.createGenerator(w), value);
   }

   public String writeValueAsString(Object value) throws JsonProcessingException {
      SegmentedStringWriter sw = new SegmentedStringWriter(this._jsonFactory._getBufferRecycler());

      try {
         this._configAndWriteValue(this._jsonFactory.createGenerator((Writer)sw), value);
      } catch (JsonProcessingException var4) {
         throw var4;
      } catch (IOException var5) {
         throw JsonMappingException.fromUnexpectedIOE(var5);
      }

      return sw.getAndClear();
   }

   public byte[] writeValueAsBytes(Object value) throws JsonProcessingException {
      ByteArrayBuilder bb = new ByteArrayBuilder(this._jsonFactory._getBufferRecycler());

      try {
         this._configAndWriteValue(this._jsonFactory.createGenerator((OutputStream)bb, JsonEncoding.UTF8), value);
      } catch (JsonProcessingException var4) {
         throw var4;
      } catch (IOException var5) {
         throw JsonMappingException.fromUnexpectedIOE(var5);
      }

      byte[] result = bb.toByteArray();
      bb.release();
      return result;
   }

   public ObjectWriter writer() {
      return this._newWriter(this.getSerializationConfig());
   }

   public ObjectWriter writer(SerializationFeature feature) {
      return this._newWriter(this.getSerializationConfig().with(feature));
   }

   public ObjectWriter writer(SerializationFeature first, SerializationFeature... other) {
      return this._newWriter(this.getSerializationConfig().with(first, other));
   }

   public ObjectWriter writer(DateFormat df) {
      return this._newWriter(this.getSerializationConfig().with(df));
   }

   public ObjectWriter writerWithView(Class<?> serializationView) {
      return this._newWriter(this.getSerializationConfig().withView(serializationView));
   }

   public ObjectWriter writerFor(Class<?> rootType) {
      return this._newWriter(this.getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType((Type)rootType), (PrettyPrinter)null);
   }

   public ObjectWriter writerFor(TypeReference<?> rootType) {
      return this._newWriter(this.getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType(rootType), (PrettyPrinter)null);
   }

   public ObjectWriter writerFor(JavaType rootType) {
      return this._newWriter(this.getSerializationConfig(), rootType, (PrettyPrinter)null);
   }

   public ObjectWriter writer(PrettyPrinter pp) {
      if (pp == null) {
         pp = ObjectWriter.NULL_PRETTY_PRINTER;
      }

      return this._newWriter(this.getSerializationConfig(), (JavaType)null, pp);
   }

   public ObjectWriter writerWithDefaultPrettyPrinter() {
      SerializationConfig config = this.getSerializationConfig();
      return this._newWriter(config, (JavaType)null, config.getDefaultPrettyPrinter());
   }

   public ObjectWriter writer(FilterProvider filterProvider) {
      return this._newWriter(this.getSerializationConfig().withFilters(filterProvider));
   }

   public ObjectWriter writer(FormatSchema schema) {
      this._verifySchemaType(schema);
      return this._newWriter(this.getSerializationConfig(), schema);
   }

   public ObjectWriter writer(Base64Variant defaultBase64) {
      return this._newWriter((SerializationConfig)this.getSerializationConfig().with((Base64Variant)defaultBase64));
   }

   public ObjectWriter writer(CharacterEscapes escapes) {
      return this._newWriter(this.getSerializationConfig()).with(escapes);
   }

   public ObjectWriter writer(ContextAttributes attrs) {
      return this._newWriter(this.getSerializationConfig().with(attrs));
   }

   /** @deprecated */
   @Deprecated
   public ObjectWriter writerWithType(Class<?> rootType) {
      return this._newWriter(this.getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType((Type)rootType), (PrettyPrinter)null);
   }

   /** @deprecated */
   @Deprecated
   public ObjectWriter writerWithType(TypeReference<?> rootType) {
      return this._newWriter(this.getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType(rootType), (PrettyPrinter)null);
   }

   /** @deprecated */
   @Deprecated
   public ObjectWriter writerWithType(JavaType rootType) {
      return this._newWriter(this.getSerializationConfig(), rootType, (PrettyPrinter)null);
   }

   public ObjectReader reader() {
      return this._newReader(this.getDeserializationConfig()).with(this._injectableValues);
   }

   public ObjectReader reader(DeserializationFeature feature) {
      return this._newReader(this.getDeserializationConfig().with(feature));
   }

   public ObjectReader reader(DeserializationFeature first, DeserializationFeature... other) {
      return this._newReader(this.getDeserializationConfig().with(first, other));
   }

   public ObjectReader readerForUpdating(Object valueToUpdate) {
      JavaType t = this._typeFactory.constructType((Type)valueToUpdate.getClass());
      return this._newReader(this.getDeserializationConfig(), t, valueToUpdate, (FormatSchema)null, this._injectableValues);
   }

   public ObjectReader readerFor(JavaType type) {
      return this._newReader(this.getDeserializationConfig(), type, (Object)null, (FormatSchema)null, this._injectableValues);
   }

   public ObjectReader readerFor(Class<?> type) {
      return this._newReader(this.getDeserializationConfig(), this._typeFactory.constructType((Type)type), (Object)null, (FormatSchema)null, this._injectableValues);
   }

   public ObjectReader readerFor(TypeReference<?> type) {
      return this._newReader(this.getDeserializationConfig(), this._typeFactory.constructType(type), (Object)null, (FormatSchema)null, this._injectableValues);
   }

   public ObjectReader reader(JsonNodeFactory f) {
      return this._newReader(this.getDeserializationConfig()).with(f);
   }

   public ObjectReader reader(FormatSchema schema) {
      this._verifySchemaType(schema);
      return this._newReader(this.getDeserializationConfig(), (JavaType)null, (Object)null, schema, this._injectableValues);
   }

   public ObjectReader reader(InjectableValues injectableValues) {
      return this._newReader(this.getDeserializationConfig(), (JavaType)null, (Object)null, (FormatSchema)null, injectableValues);
   }

   public ObjectReader readerWithView(Class<?> view) {
      return this._newReader(this.getDeserializationConfig().withView(view));
   }

   public ObjectReader reader(Base64Variant defaultBase64) {
      return this._newReader((DeserializationConfig)this.getDeserializationConfig().with((Base64Variant)defaultBase64));
   }

   public ObjectReader reader(ContextAttributes attrs) {
      return this._newReader(this.getDeserializationConfig().with(attrs));
   }

   /** @deprecated */
   @Deprecated
   public ObjectReader reader(JavaType type) {
      return this._newReader(this.getDeserializationConfig(), type, (Object)null, (FormatSchema)null, this._injectableValues);
   }

   /** @deprecated */
   @Deprecated
   public ObjectReader reader(Class<?> type) {
      return this._newReader(this.getDeserializationConfig(), this._typeFactory.constructType((Type)type), (Object)null, (FormatSchema)null, this._injectableValues);
   }

   /** @deprecated */
   @Deprecated
   public ObjectReader reader(TypeReference<?> type) {
      return this._newReader(this.getDeserializationConfig(), this._typeFactory.constructType(type), (Object)null, (FormatSchema)null, this._injectableValues);
   }

   public <T> T convertValue(Object fromValue, Class<T> toValueType) throws IllegalArgumentException {
      return this._convert(fromValue, this._typeFactory.constructType((Type)toValueType));
   }

   public <T> T convertValue(Object fromValue, TypeReference<?> toValueTypeRef) throws IllegalArgumentException {
      return this._convert(fromValue, this._typeFactory.constructType(toValueTypeRef));
   }

   public <T> T convertValue(Object fromValue, JavaType toValueType) throws IllegalArgumentException {
      return this._convert(fromValue, toValueType);
   }

   protected Object _convert(Object fromValue, JavaType toValueType) throws IllegalArgumentException {
      if (fromValue != null) {
         Class<?> targetType = toValueType.getRawClass();
         if (targetType != Object.class && !toValueType.hasGenericTypes() && targetType.isAssignableFrom(fromValue.getClass())) {
            return fromValue;
         }
      }

      TokenBuffer buf = new TokenBuffer(this, false);
      if (this.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
         buf = buf.forceUseOfBigDecimal(true);
      }

      try {
         SerializationConfig config = this.getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
         this._serializerProvider(config).serializeValue(buf, fromValue);
         JsonParser p = buf.asParser();
         DeserializationConfig deserConfig = this.getDeserializationConfig();
         JsonToken t = this._initForReading(p, toValueType);
         Object result;
         DefaultDeserializationContext ctxt;
         if (t == JsonToken.VALUE_NULL) {
            ctxt = this.createDeserializationContext(p, deserConfig);
            result = this._findRootDeserializer(ctxt, toValueType).getNullValue(ctxt);
         } else if (t != JsonToken.END_ARRAY && t != JsonToken.END_OBJECT) {
            ctxt = this.createDeserializationContext(p, deserConfig);
            JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, toValueType);
            result = deser.deserialize(p, ctxt);
         } else {
            result = null;
         }

         p.close();
         return result;
      } catch (IOException var11) {
         throw new IllegalArgumentException(var11.getMessage(), var11);
      }
   }

   public <T> T updateValue(T valueToUpdate, Object overrides) throws JsonMappingException {
      T result = valueToUpdate;
      if (valueToUpdate != null && overrides != null) {
         TokenBuffer buf = new TokenBuffer(this, false);
         if (this.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            buf = buf.forceUseOfBigDecimal(true);
         }

         try {
            SerializationConfig config = this.getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
            this._serializerProvider(config).serializeValue(buf, overrides);
            JsonParser p = buf.asParser();
            result = this.readerForUpdating(valueToUpdate).readValue(p);
            p.close();
         } catch (IOException var7) {
            if (var7 instanceof JsonMappingException) {
               throw (JsonMappingException)var7;
            }

            throw JsonMappingException.fromUnexpectedIOE(var7);
         }
      }

      return result;
   }

   /** @deprecated */
   @Deprecated
   public JsonSchema generateJsonSchema(Class<?> t) throws JsonMappingException {
      return this._serializerProvider(this.getSerializationConfig()).generateJsonSchema(t);
   }

   public void acceptJsonFormatVisitor(Class<?> type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
      this.acceptJsonFormatVisitor(this._typeFactory.constructType((Type)type), visitor);
   }

   public void acceptJsonFormatVisitor(JavaType type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
      if (type == null) {
         throw new IllegalArgumentException("type must be provided");
      } else {
         this._serializerProvider(this.getSerializationConfig()).acceptJsonFormatVisitor(type, visitor);
      }
   }

   protected DefaultSerializerProvider _serializerProvider(SerializationConfig config) {
      return this._serializerProvider.createInstance(config, this._serializerFactory);
   }

   protected final void _configAndWriteValue(JsonGenerator g, Object value) throws IOException {
      SerializationConfig cfg = this.getSerializationConfig();
      cfg.initialize(g);
      if (cfg.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
         this._configAndWriteCloseable(g, value, cfg);
      } else {
         try {
            this._serializerProvider(cfg).serializeValue(g, value);
         } catch (Exception var5) {
            ClassUtil.closeOnFailAndThrowAsIOE(g, var5);
            return;
         }

         g.close();
      }
   }

   private final void _configAndWriteCloseable(JsonGenerator g, Object value, SerializationConfig cfg) throws IOException {
      Closeable toClose = (Closeable)value;

      try {
         this._serializerProvider(cfg).serializeValue(g, value);
         Closeable tmpToClose = toClose;
         toClose = null;
         tmpToClose.close();
      } catch (Exception var6) {
         ClassUtil.closeOnFailAndThrowAsIOE(g, toClose, var6);
         return;
      }

      g.close();
   }

   private final void _writeCloseableValue(JsonGenerator g, Object value, SerializationConfig cfg) throws IOException {
      Closeable toClose = (Closeable)value;

      try {
         this._serializerProvider(cfg).serializeValue(g, value);
         if (cfg.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
            g.flush();
         }
      } catch (Exception var6) {
         ClassUtil.closeOnFailAndThrowAsIOE((JsonGenerator)null, toClose, var6);
         return;
      }

      toClose.close();
   }

   protected Object _readValue(DeserializationConfig cfg, JsonParser p, JavaType valueType) throws IOException {
      JsonToken t = this._initForReading(p, valueType);
      DeserializationContext ctxt = this.createDeserializationContext(p, cfg);
      Object result;
      if (t == JsonToken.VALUE_NULL) {
         result = this._findRootDeserializer(ctxt, valueType).getNullValue(ctxt);
      } else if (t != JsonToken.END_ARRAY && t != JsonToken.END_OBJECT) {
         JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, valueType);
         if (cfg.useRootWrapping()) {
            result = this._unwrapAndDeserialize(p, ctxt, cfg, valueType, deser);
         } else {
            result = deser.deserialize(p, ctxt);
         }
      } else {
         result = null;
      }

      p.clearCurrentToken();
      if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
         this._verifyNoTrailingTokens(p, ctxt, valueType);
      }

      return result;
   }

   protected Object _readMapAndClose(JsonParser p0, JavaType valueType) throws IOException {
      JsonParser p = p0;
      Throwable var4 = null;

      Object var20;
      try {
         JsonToken t = this._initForReading(p, valueType);
         DeserializationConfig cfg = this.getDeserializationConfig();
         DeserializationContext ctxt = this.createDeserializationContext(p, cfg);
         Object result;
         if (t == JsonToken.VALUE_NULL) {
            result = this._findRootDeserializer(ctxt, valueType).getNullValue(ctxt);
         } else if (t != JsonToken.END_ARRAY && t != JsonToken.END_OBJECT) {
            JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, valueType);
            if (cfg.useRootWrapping()) {
               result = this._unwrapAndDeserialize(p, ctxt, cfg, valueType, deser);
            } else {
               result = deser.deserialize(p, ctxt);
            }

            ctxt.checkUnresolvedObjectId();
         } else {
            result = null;
         }

         if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
            this._verifyNoTrailingTokens(p, ctxt, valueType);
         }

         var20 = result;
      } catch (Throwable var18) {
         var4 = var18;
         throw var18;
      } finally {
         if (p0 != null) {
            if (var4 != null) {
               try {
                  p.close();
               } catch (Throwable var17) {
                  var4.addSuppressed(var17);
               }
            } else {
               p0.close();
            }
         }

      }

      return var20;
   }

   protected JsonNode _readTreeAndClose(JsonParser p0) throws IOException {
      JsonParser p = p0;
      Throwable var3 = null;

      JsonNode var10;
      try {
         JavaType valueType = JSON_NODE_TYPE;
         DeserializationConfig cfg = this.getDeserializationConfig();
         cfg.initialize(p);
         JsonToken t = p.getCurrentToken();
         DefaultDeserializationContext ctxt;
         if (t == null) {
            t = p.nextToken();
            if (t == null) {
               ctxt = null;
               return ctxt;
            }
         }

         if (t == JsonToken.VALUE_NULL) {
            NullNode var23 = cfg.getNodeFactory().nullNode();
            return var23;
         }

         ctxt = this.createDeserializationContext(p, cfg);
         JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, valueType);
         Object result;
         if (cfg.useRootWrapping()) {
            result = this._unwrapAndDeserialize(p, ctxt, cfg, valueType, deser);
         } else {
            result = deser.deserialize(p, ctxt);
            if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
               this._verifyNoTrailingTokens(p, ctxt, valueType);
            }
         }

         var10 = (JsonNode)result;
      } catch (Throwable var21) {
         var3 = var21;
         throw var21;
      } finally {
         if (p0 != null) {
            if (var3 != null) {
               try {
                  p.close();
               } catch (Throwable var20) {
                  var3.addSuppressed(var20);
               }
            } else {
               p0.close();
            }
         }

      }

      return var10;
   }

   protected Object _unwrapAndDeserialize(JsonParser p, DeserializationContext ctxt, DeserializationConfig config, JavaType rootType, JsonDeserializer<Object> deser) throws IOException {
      PropertyName expRootName = config.findRootName(rootType);
      String expSimpleName = expRootName.getSimpleName();
      if (p.getCurrentToken() != JsonToken.START_OBJECT) {
         ctxt.reportWrongTokenException(rootType, JsonToken.START_OBJECT, "Current token not START_OBJECT (needed to unwrap root name '%s'), but %s", expSimpleName, p.getCurrentToken());
      }

      if (p.nextToken() != JsonToken.FIELD_NAME) {
         ctxt.reportWrongTokenException(rootType, JsonToken.FIELD_NAME, "Current token not FIELD_NAME (to contain expected root name '" + expSimpleName + "'), but " + p.getCurrentToken());
      }

      String actualName = p.getCurrentName();
      if (!expSimpleName.equals(actualName)) {
         ctxt.reportInputMismatch(rootType, "Root name '%s' does not match expected ('%s') for type %s", actualName, expSimpleName);
      }

      p.nextToken();
      Object result = deser.deserialize(p, ctxt);
      if (p.nextToken() != JsonToken.END_OBJECT) {
         ctxt.reportWrongTokenException(rootType, JsonToken.END_OBJECT, "Current token not END_OBJECT (to match wrapper object with root name '%s'), but %s", expSimpleName, p.getCurrentToken());
      }

      if (config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
         this._verifyNoTrailingTokens(p, ctxt, rootType);
      }

      return result;
   }

   protected DefaultDeserializationContext createDeserializationContext(JsonParser p, DeserializationConfig cfg) {
      return this._deserializationContext.createInstance(cfg, p, this._injectableValues);
   }

   protected JsonToken _initForReading(JsonParser p, JavaType targetType) throws IOException {
      this._deserializationConfig.initialize(p);
      JsonToken t = p.getCurrentToken();
      if (t == null) {
         t = p.nextToken();
         if (t == null) {
            throw MismatchedInputException.from(p, targetType, "No content to map due to end-of-input");
         }
      }

      return t;
   }

   /** @deprecated */
   @Deprecated
   protected JsonToken _initForReading(JsonParser p) throws IOException {
      return this._initForReading(p, (JavaType)null);
   }

   protected final void _verifyNoTrailingTokens(JsonParser p, DeserializationContext ctxt, JavaType bindType) throws IOException {
      JsonToken t = p.nextToken();
      if (t != null) {
         Class<?> bt = ClassUtil.rawClass(bindType);
         ctxt.reportTrailingTokens(bt, p, t);
      }

   }

   protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt, JavaType valueType) throws JsonMappingException {
      JsonDeserializer<Object> deser = (JsonDeserializer)this._rootDeserializers.get(valueType);
      if (deser != null) {
         return deser;
      } else {
         deser = ctxt.findRootValueDeserializer(valueType);
         if (deser == null) {
            return (JsonDeserializer)ctxt.reportBadDefinition(valueType, "Cannot find a deserializer for type " + valueType);
         } else {
            this._rootDeserializers.put(valueType, deser);
            return deser;
         }
      }
   }

   protected void _verifySchemaType(FormatSchema schema) {
      if (schema != null && !this._jsonFactory.canUseSchema(schema)) {
         throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + this._jsonFactory.getFormatName());
      }
   }

   static {
      DEFAULT_BASE = new BaseSettings((ClassIntrospector)null, DEFAULT_ANNOTATION_INTROSPECTOR, (PropertyNamingStrategy)null, TypeFactory.defaultInstance(), (TypeResolverBuilder)null, StdDateFormat.instance, (HandlerInstantiator)null, Locale.getDefault(), (TimeZone)null, Base64Variants.getDefaultVariant());
   }

   public static class DefaultTypeResolverBuilder extends StdTypeResolverBuilder implements Serializable {
      private static final long serialVersionUID = 1L;
      protected final ObjectMapper.DefaultTyping _appliesFor;

      public DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping t) {
         this._appliesFor = t;
      }

      public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
         return this.useForType(baseType) ? super.buildTypeDeserializer(config, baseType, subtypes) : null;
      }

      public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
         return this.useForType(baseType) ? super.buildTypeSerializer(config, baseType, subtypes) : null;
      }

      public boolean useForType(JavaType t) {
         if (t.isPrimitive()) {
            return false;
         } else {
            switch(this._appliesFor) {
            case NON_CONCRETE_AND_ARRAYS:
               while(t.isArrayType()) {
                  t = t.getContentType();
               }
            case OBJECT_AND_NON_CONCRETE:
               break;
            case NON_FINAL:
               while(t.isArrayType()) {
                  t = t.getContentType();
               }

               while(t.isReferenceType()) {
                  t = t.getReferencedType();
               }

               return !t.isFinal() && !TreeNode.class.isAssignableFrom(t.getRawClass());
            default:
               return t.isJavaLangObject();
            }

            while(t.isReferenceType()) {
               t = t.getReferencedType();
            }

            return t.isJavaLangObject() || !t.isConcrete() && !TreeNode.class.isAssignableFrom(t.getRawClass());
         }
      }
   }

   public static enum DefaultTyping {
      JAVA_LANG_OBJECT,
      OBJECT_AND_NON_CONCRETE,
      NON_CONCRETE_AND_ARRAYS,
      NON_FINAL;
   }
}
