package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.DataInput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variant;
import software.bernie.shadowed.fasterxml.jackson.core.FormatFeature;
import software.bernie.shadowed.fasterxml.jackson.core.FormatSchema;
import software.bernie.shadowed.fasterxml.jackson.core.JsonFactory;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParseException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonPointer;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.ObjectCodec;
import software.bernie.shadowed.fasterxml.jackson.core.TreeNode;
import software.bernie.shadowed.fasterxml.jackson.core.Version;
import software.bernie.shadowed.fasterxml.jackson.core.Versioned;
import software.bernie.shadowed.fasterxml.jackson.core.filter.FilteringParserDelegate;
import software.bernie.shadowed.fasterxml.jackson.core.filter.JsonPointerBasedFilter;
import software.bernie.shadowed.fasterxml.jackson.core.filter.TokenFilter;
import software.bernie.shadowed.fasterxml.jackson.core.type.ResolvedType;
import software.bernie.shadowed.fasterxml.jackson.core.type.TypeReference;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.ContextAttributes;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.PackageVersion;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.DataFormatReaders;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import software.bernie.shadowed.fasterxml.jackson.databind.node.JsonNodeFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.node.TreeTraversingParser;
import software.bernie.shadowed.fasterxml.jackson.databind.type.SimpleType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public class ObjectReader extends ObjectCodec implements Versioned, Serializable {
   private static final long serialVersionUID = 2L;
   private static final JavaType JSON_NODE_TYPE = SimpleType.constructUnsafe(JsonNode.class);
   protected final DeserializationConfig _config;
   protected final DefaultDeserializationContext _context;
   protected final JsonFactory _parserFactory;
   protected final boolean _unwrapRoot;
   private final TokenFilter _filter;
   protected final JavaType _valueType;
   protected final JsonDeserializer<Object> _rootDeserializer;
   protected final Object _valueToUpdate;
   protected final FormatSchema _schema;
   protected final InjectableValues _injectableValues;
   protected final DataFormatReaders _dataFormatReaders;
   protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers;

   protected ObjectReader(ObjectMapper mapper, DeserializationConfig config) {
      this(mapper, config, (JavaType)null, (Object)null, (FormatSchema)null, (InjectableValues)null);
   }

   protected ObjectReader(ObjectMapper mapper, DeserializationConfig config, JavaType valueType, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues) {
      this._config = config;
      this._context = mapper._deserializationContext;
      this._rootDeserializers = mapper._rootDeserializers;
      this._parserFactory = mapper._jsonFactory;
      this._valueType = valueType;
      this._valueToUpdate = valueToUpdate;
      this._schema = schema;
      this._injectableValues = injectableValues;
      this._unwrapRoot = config.useRootWrapping();
      this._rootDeserializer = this._prefetchRootDeserializer(valueType);
      this._dataFormatReaders = null;
      this._filter = null;
   }

   protected ObjectReader(ObjectReader base, DeserializationConfig config, JavaType valueType, JsonDeserializer<Object> rootDeser, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues, DataFormatReaders dataFormatReaders) {
      this._config = config;
      this._context = base._context;
      this._rootDeserializers = base._rootDeserializers;
      this._parserFactory = base._parserFactory;
      this._valueType = valueType;
      this._rootDeserializer = rootDeser;
      this._valueToUpdate = valueToUpdate;
      this._schema = schema;
      this._injectableValues = injectableValues;
      this._unwrapRoot = config.useRootWrapping();
      this._dataFormatReaders = dataFormatReaders;
      this._filter = base._filter;
   }

   protected ObjectReader(ObjectReader base, DeserializationConfig config) {
      this._config = config;
      this._context = base._context;
      this._rootDeserializers = base._rootDeserializers;
      this._parserFactory = base._parserFactory;
      this._valueType = base._valueType;
      this._rootDeserializer = base._rootDeserializer;
      this._valueToUpdate = base._valueToUpdate;
      this._schema = base._schema;
      this._injectableValues = base._injectableValues;
      this._unwrapRoot = config.useRootWrapping();
      this._dataFormatReaders = base._dataFormatReaders;
      this._filter = base._filter;
   }

   protected ObjectReader(ObjectReader base, JsonFactory f) {
      this._config = (DeserializationConfig)base._config.with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering());
      this._context = base._context;
      this._rootDeserializers = base._rootDeserializers;
      this._parserFactory = f;
      this._valueType = base._valueType;
      this._rootDeserializer = base._rootDeserializer;
      this._valueToUpdate = base._valueToUpdate;
      this._schema = base._schema;
      this._injectableValues = base._injectableValues;
      this._unwrapRoot = base._unwrapRoot;
      this._dataFormatReaders = base._dataFormatReaders;
      this._filter = base._filter;
   }

   protected ObjectReader(ObjectReader base, TokenFilter filter) {
      this._config = base._config;
      this._context = base._context;
      this._rootDeserializers = base._rootDeserializers;
      this._parserFactory = base._parserFactory;
      this._valueType = base._valueType;
      this._rootDeserializer = base._rootDeserializer;
      this._valueToUpdate = base._valueToUpdate;
      this._schema = base._schema;
      this._injectableValues = base._injectableValues;
      this._unwrapRoot = base._unwrapRoot;
      this._dataFormatReaders = base._dataFormatReaders;
      this._filter = filter;
   }

   public Version version() {
      return PackageVersion.VERSION;
   }

   protected ObjectReader _new(ObjectReader base, JsonFactory f) {
      return new ObjectReader(base, f);
   }

   protected ObjectReader _new(ObjectReader base, DeserializationConfig config) {
      return new ObjectReader(base, config);
   }

   protected ObjectReader _new(ObjectReader base, DeserializationConfig config, JavaType valueType, JsonDeserializer<Object> rootDeser, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues, DataFormatReaders dataFormatReaders) {
      return new ObjectReader(base, config, valueType, rootDeser, valueToUpdate, schema, injectableValues, dataFormatReaders);
   }

   protected <T> MappingIterator<T> _newIterator(JsonParser p, DeserializationContext ctxt, JsonDeserializer<?> deser, boolean parserManaged) {
      return new MappingIterator(this._valueType, p, ctxt, deser, parserManaged, this._valueToUpdate);
   }

   protected JsonToken _initForReading(DeserializationContext ctxt, JsonParser p) throws IOException {
      if (this._schema != null) {
         p.setSchema(this._schema);
      }

      this._config.initialize(p);
      JsonToken t = p.getCurrentToken();
      if (t == null) {
         t = p.nextToken();
         if (t == null) {
            ctxt.reportInputMismatch(this._valueType, "No content to map due to end-of-input");
         }
      }

      return t;
   }

   protected void _initForMultiRead(DeserializationContext ctxt, JsonParser p) throws IOException {
      if (this._schema != null) {
         p.setSchema(this._schema);
      }

      this._config.initialize(p);
   }

   public ObjectReader with(DeserializationFeature feature) {
      return this._with(this._config.with(feature));
   }

   public ObjectReader with(DeserializationFeature first, DeserializationFeature... other) {
      return this._with(this._config.with(first, other));
   }

   public ObjectReader withFeatures(DeserializationFeature... features) {
      return this._with(this._config.withFeatures(features));
   }

   public ObjectReader without(DeserializationFeature feature) {
      return this._with(this._config.without(feature));
   }

   public ObjectReader without(DeserializationFeature first, DeserializationFeature... other) {
      return this._with(this._config.without(first, other));
   }

   public ObjectReader withoutFeatures(DeserializationFeature... features) {
      return this._with(this._config.withoutFeatures(features));
   }

   public ObjectReader with(JsonParser.Feature feature) {
      return this._with(this._config.with(feature));
   }

   public ObjectReader withFeatures(JsonParser.Feature... features) {
      return this._with(this._config.withFeatures(features));
   }

   public ObjectReader without(JsonParser.Feature feature) {
      return this._with(this._config.without(feature));
   }

   public ObjectReader withoutFeatures(JsonParser.Feature... features) {
      return this._with(this._config.withoutFeatures(features));
   }

   public ObjectReader with(FormatFeature feature) {
      return this._with(this._config.with(feature));
   }

   public ObjectReader withFeatures(FormatFeature... features) {
      return this._with(this._config.withFeatures(features));
   }

   public ObjectReader without(FormatFeature feature) {
      return this._with(this._config.without(feature));
   }

   public ObjectReader withoutFeatures(FormatFeature... features) {
      return this._with(this._config.withoutFeatures(features));
   }

   public ObjectReader at(String value) {
      return new ObjectReader(this, new JsonPointerBasedFilter(value));
   }

   public ObjectReader at(JsonPointer pointer) {
      return new ObjectReader(this, new JsonPointerBasedFilter(pointer));
   }

   public ObjectReader with(DeserializationConfig config) {
      return this._with(config);
   }

   public ObjectReader with(InjectableValues injectableValues) {
      return this._injectableValues == injectableValues ? this : this._new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, injectableValues, this._dataFormatReaders);
   }

   public ObjectReader with(JsonNodeFactory f) {
      return this._with(this._config.with(f));
   }

   public ObjectReader with(JsonFactory f) {
      if (f == this._parserFactory) {
         return this;
      } else {
         ObjectReader r = this._new(this, f);
         if (f.getCodec() == null) {
            f.setCodec(r);
         }

         return r;
      }
   }

   public ObjectReader withRootName(String rootName) {
      return this._with((DeserializationConfig)this._config.withRootName((String)rootName));
   }

   public ObjectReader withRootName(PropertyName rootName) {
      return this._with(this._config.withRootName(rootName));
   }

   public ObjectReader withoutRootName() {
      return this._with(this._config.withRootName(PropertyName.NO_NAME));
   }

   public ObjectReader with(FormatSchema schema) {
      if (this._schema == schema) {
         return this;
      } else {
         this._verifySchemaType(schema);
         return this._new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, schema, this._injectableValues, this._dataFormatReaders);
      }
   }

   public ObjectReader forType(JavaType valueType) {
      if (valueType != null && valueType.equals(this._valueType)) {
         return this;
      } else {
         JsonDeserializer<Object> rootDeser = this._prefetchRootDeserializer(valueType);
         DataFormatReaders det = this._dataFormatReaders;
         if (det != null) {
            det = det.withType(valueType);
         }

         return this._new(this, this._config, valueType, rootDeser, this._valueToUpdate, this._schema, this._injectableValues, det);
      }
   }

   public ObjectReader forType(Class<?> valueType) {
      return this.forType(this._config.constructType(valueType));
   }

   public ObjectReader forType(TypeReference<?> valueTypeRef) {
      return this.forType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
   }

   /** @deprecated */
   @Deprecated
   public ObjectReader withType(JavaType valueType) {
      return this.forType(valueType);
   }

   /** @deprecated */
   @Deprecated
   public ObjectReader withType(Class<?> valueType) {
      return this.forType(this._config.constructType(valueType));
   }

   /** @deprecated */
   @Deprecated
   public ObjectReader withType(Type valueType) {
      return this.forType(this._config.getTypeFactory().constructType(valueType));
   }

   /** @deprecated */
   @Deprecated
   public ObjectReader withType(TypeReference<?> valueTypeRef) {
      return this.forType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
   }

   public ObjectReader withValueToUpdate(Object value) {
      if (value == this._valueToUpdate) {
         return this;
      } else if (value == null) {
         return this._new(this, this._config, this._valueType, this._rootDeserializer, (Object)null, this._schema, this._injectableValues, this._dataFormatReaders);
      } else {
         JavaType t;
         if (this._valueType == null) {
            t = this._config.constructType(value.getClass());
         } else {
            t = this._valueType;
         }

         return this._new(this, this._config, t, this._rootDeserializer, value, this._schema, this._injectableValues, this._dataFormatReaders);
      }
   }

   public ObjectReader withView(Class<?> activeView) {
      return this._with(this._config.withView(activeView));
   }

   public ObjectReader with(Locale l) {
      return this._with((DeserializationConfig)this._config.with((Locale)l));
   }

   public ObjectReader with(TimeZone tz) {
      return this._with((DeserializationConfig)this._config.with((TimeZone)tz));
   }

   public ObjectReader withHandler(DeserializationProblemHandler h) {
      return this._with(this._config.withHandler(h));
   }

   public ObjectReader with(Base64Variant defaultBase64) {
      return this._with((DeserializationConfig)this._config.with((Base64Variant)defaultBase64));
   }

   public ObjectReader withFormatDetection(ObjectReader... readers) {
      return this.withFormatDetection(new DataFormatReaders(readers));
   }

   public ObjectReader withFormatDetection(DataFormatReaders readers) {
      return this._new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, this._injectableValues, readers);
   }

   public ObjectReader with(ContextAttributes attrs) {
      return this._with(this._config.with(attrs));
   }

   public ObjectReader withAttributes(Map<?, ?> attrs) {
      return this._with((DeserializationConfig)this._config.withAttributes(attrs));
   }

   public ObjectReader withAttribute(Object key, Object value) {
      return this._with((DeserializationConfig)this._config.withAttribute(key, value));
   }

   public ObjectReader withoutAttribute(Object key) {
      return this._with((DeserializationConfig)this._config.withoutAttribute(key));
   }

   protected ObjectReader _with(DeserializationConfig newConfig) {
      if (newConfig == this._config) {
         return this;
      } else {
         ObjectReader r = this._new(this, newConfig);
         if (this._dataFormatReaders != null) {
            r = r.withFormatDetection(this._dataFormatReaders.with(newConfig));
         }

         return r;
      }
   }

   public boolean isEnabled(DeserializationFeature f) {
      return this._config.isEnabled(f);
   }

   public boolean isEnabled(MapperFeature f) {
      return this._config.isEnabled(f);
   }

   public boolean isEnabled(JsonParser.Feature f) {
      return this._parserFactory.isEnabled(f);
   }

   public DeserializationConfig getConfig() {
      return this._config;
   }

   public JsonFactory getFactory() {
      return this._parserFactory;
   }

   public TypeFactory getTypeFactory() {
      return this._config.getTypeFactory();
   }

   public ContextAttributes getAttributes() {
      return this._config.getAttributes();
   }

   public InjectableValues getInjectableValues() {
      return this._injectableValues;
   }

   public <T> T readValue(JsonParser p) throws IOException {
      return this._bind(p, this._valueToUpdate);
   }

   public <T> T readValue(JsonParser p, Class<T> valueType) throws IOException {
      return this.forType(valueType).readValue(p);
   }

   public <T> T readValue(JsonParser p, TypeReference<?> valueTypeRef) throws IOException {
      return this.forType(valueTypeRef).readValue(p);
   }

   public <T> T readValue(JsonParser p, ResolvedType valueType) throws IOException {
      return this.forType((JavaType)valueType).readValue(p);
   }

   public <T> T readValue(JsonParser p, JavaType valueType) throws IOException {
      return this.forType(valueType).readValue(p);
   }

   public <T> Iterator<T> readValues(JsonParser p, Class<T> valueType) throws IOException {
      return this.forType(valueType).readValues(p);
   }

   public <T> Iterator<T> readValues(JsonParser p, TypeReference<?> valueTypeRef) throws IOException {
      return this.forType(valueTypeRef).readValues(p);
   }

   public <T> Iterator<T> readValues(JsonParser p, ResolvedType valueType) throws IOException {
      return this.readValues(p, (JavaType)valueType);
   }

   public <T> Iterator<T> readValues(JsonParser p, JavaType valueType) throws IOException {
      return this.forType(valueType).readValues(p);
   }

   public JsonNode createArrayNode() {
      return this._config.getNodeFactory().arrayNode();
   }

   public JsonNode createObjectNode() {
      return this._config.getNodeFactory().objectNode();
   }

   public JsonParser treeAsTokens(TreeNode n) {
      return new TreeTraversingParser((JsonNode)n, this);
   }

   public <T extends TreeNode> T readTree(JsonParser p) throws IOException {
      return this._bindAsTree(p);
   }

   public void writeTree(JsonGenerator g, TreeNode rootNode) {
      throw new UnsupportedOperationException();
   }

   public <T> T readValue(InputStream src) throws IOException {
      return this._dataFormatReaders != null ? this._detectBindAndClose(this._dataFormatReaders.findFormat(src), false) : this._bindAndClose(this._considerFilter(this._parserFactory.createParser(src), false));
   }

   public <T> T readValue(Reader src) throws IOException {
      if (this._dataFormatReaders != null) {
         this._reportUndetectableSource(src);
      }

      return this._bindAndClose(this._considerFilter(this._parserFactory.createParser(src), false));
   }

   public <T> T readValue(String src) throws IOException {
      if (this._dataFormatReaders != null) {
         this._reportUndetectableSource(src);
      }

      return this._bindAndClose(this._considerFilter(this._parserFactory.createParser(src), false));
   }

   public <T> T readValue(byte[] src) throws IOException {
      return this._dataFormatReaders != null ? this._detectBindAndClose(src, 0, src.length) : this._bindAndClose(this._considerFilter(this._parserFactory.createParser(src), false));
   }

   public <T> T readValue(byte[] src, int offset, int length) throws IOException {
      return this._dataFormatReaders != null ? this._detectBindAndClose(src, offset, length) : this._bindAndClose(this._considerFilter(this._parserFactory.createParser(src, offset, length), false));
   }

   public <T> T readValue(File src) throws IOException {
      return this._dataFormatReaders != null ? this._detectBindAndClose(this._dataFormatReaders.findFormat(this._inputStream(src)), true) : this._bindAndClose(this._considerFilter(this._parserFactory.createParser(src), false));
   }

   public <T> T readValue(URL src) throws IOException {
      return this._dataFormatReaders != null ? this._detectBindAndClose(this._dataFormatReaders.findFormat(this._inputStream(src)), true) : this._bindAndClose(this._considerFilter(this._parserFactory.createParser(src), false));
   }

   public <T> T readValue(JsonNode src) throws IOException {
      if (this._dataFormatReaders != null) {
         this._reportUndetectableSource(src);
      }

      return this._bindAndClose(this._considerFilter(this.treeAsTokens(src), false));
   }

   public <T> T readValue(DataInput src) throws IOException {
      if (this._dataFormatReaders != null) {
         this._reportUndetectableSource(src);
      }

      return this._bindAndClose(this._considerFilter(this._parserFactory.createParser(src), false));
   }

   public JsonNode readTree(InputStream in) throws IOException {
      return this._dataFormatReaders != null ? this._detectBindAndCloseAsTree(in) : this._bindAndCloseAsTree(this._considerFilter(this._parserFactory.createParser(in), false));
   }

   public JsonNode readTree(Reader r) throws IOException {
      if (this._dataFormatReaders != null) {
         this._reportUndetectableSource(r);
      }

      return this._bindAndCloseAsTree(this._considerFilter(this._parserFactory.createParser(r), false));
   }

   public JsonNode readTree(String json) throws IOException {
      if (this._dataFormatReaders != null) {
         this._reportUndetectableSource(json);
      }

      return this._bindAndCloseAsTree(this._considerFilter(this._parserFactory.createParser(json), false));
   }

   public JsonNode readTree(DataInput src) throws IOException {
      if (this._dataFormatReaders != null) {
         this._reportUndetectableSource(src);
      }

      return this._bindAndCloseAsTree(this._considerFilter(this._parserFactory.createParser(src), false));
   }

   public <T> MappingIterator<T> readValues(JsonParser p) throws IOException {
      DeserializationContext ctxt = this.createDeserializationContext(p);
      return this._newIterator(p, ctxt, this._findRootDeserializer(ctxt), false);
   }

   public <T> MappingIterator<T> readValues(InputStream src) throws IOException {
      return this._dataFormatReaders != null ? this._detectBindAndReadValues(this._dataFormatReaders.findFormat(src), false) : this._bindAndReadValues(this._considerFilter(this._parserFactory.createParser(src), true));
   }

   public <T> MappingIterator<T> readValues(Reader src) throws IOException {
      if (this._dataFormatReaders != null) {
         this._reportUndetectableSource(src);
      }

      JsonParser p = this._considerFilter(this._parserFactory.createParser(src), true);
      DeserializationContext ctxt = this.createDeserializationContext(p);
      this._initForMultiRead(ctxt, p);
      p.nextToken();
      return this._newIterator(p, ctxt, this._findRootDeserializer(ctxt), true);
   }

   public <T> MappingIterator<T> readValues(String json) throws IOException {
      if (this._dataFormatReaders != null) {
         this._reportUndetectableSource(json);
      }

      JsonParser p = this._considerFilter(this._parserFactory.createParser(json), true);
      DeserializationContext ctxt = this.createDeserializationContext(p);
      this._initForMultiRead(ctxt, p);
      p.nextToken();
      return this._newIterator(p, ctxt, this._findRootDeserializer(ctxt), true);
   }

   public <T> MappingIterator<T> readValues(byte[] src, int offset, int length) throws IOException {
      return this._dataFormatReaders != null ? this._detectBindAndReadValues(this._dataFormatReaders.findFormat(src, offset, length), false) : this._bindAndReadValues(this._considerFilter(this._parserFactory.createParser(src, offset, length), true));
   }

   public final <T> MappingIterator<T> readValues(byte[] src) throws IOException {
      return this.readValues(src, 0, src.length);
   }

   public <T> MappingIterator<T> readValues(File src) throws IOException {
      return this._dataFormatReaders != null ? this._detectBindAndReadValues(this._dataFormatReaders.findFormat(this._inputStream(src)), false) : this._bindAndReadValues(this._considerFilter(this._parserFactory.createParser(src), true));
   }

   public <T> MappingIterator<T> readValues(URL src) throws IOException {
      return this._dataFormatReaders != null ? this._detectBindAndReadValues(this._dataFormatReaders.findFormat(this._inputStream(src)), true) : this._bindAndReadValues(this._considerFilter(this._parserFactory.createParser(src), true));
   }

   public <T> MappingIterator<T> readValues(DataInput src) throws IOException {
      if (this._dataFormatReaders != null) {
         this._reportUndetectableSource(src);
      }

      return this._bindAndReadValues(this._considerFilter(this._parserFactory.createParser(src), true));
   }

   public <T> T treeToValue(TreeNode n, Class<T> valueType) throws JsonProcessingException {
      try {
         return this.readValue(this.treeAsTokens(n), valueType);
      } catch (JsonProcessingException var4) {
         throw var4;
      } catch (IOException var5) {
         throw JsonMappingException.fromUnexpectedIOE(var5);
      }
   }

   public void writeValue(JsonGenerator gen, Object value) throws IOException {
      throw new UnsupportedOperationException("Not implemented for ObjectReader");
   }

   protected Object _bind(JsonParser p, Object valueToUpdate) throws IOException {
      DeserializationContext ctxt = this.createDeserializationContext(p);
      JsonToken t = this._initForReading(ctxt, p);
      Object result;
      if (t == JsonToken.VALUE_NULL) {
         if (valueToUpdate == null) {
            result = this._findRootDeserializer(ctxt).getNullValue(ctxt);
         } else {
            result = valueToUpdate;
         }
      } else if (t != JsonToken.END_ARRAY && t != JsonToken.END_OBJECT) {
         JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt);
         if (this._unwrapRoot) {
            result = this._unwrapAndDeserialize(p, ctxt, this._valueType, deser);
         } else if (valueToUpdate == null) {
            result = deser.deserialize(p, ctxt);
         } else {
            result = deser.deserialize(p, ctxt, valueToUpdate);
         }
      } else {
         result = valueToUpdate;
      }

      p.clearCurrentToken();
      if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
         this._verifyNoTrailingTokens(p, ctxt, this._valueType);
      }

      return result;
   }

   protected Object _bindAndClose(JsonParser p0) throws IOException {
      JsonParser p = p0;
      Throwable var3 = null;

      Object var18;
      try {
         DeserializationContext ctxt = this.createDeserializationContext(p);
         JsonToken t = this._initForReading(ctxt, p);
         Object result;
         if (t == JsonToken.VALUE_NULL) {
            if (this._valueToUpdate == null) {
               result = this._findRootDeserializer(ctxt).getNullValue(ctxt);
            } else {
               result = this._valueToUpdate;
            }
         } else if (t != JsonToken.END_ARRAY && t != JsonToken.END_OBJECT) {
            JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt);
            if (this._unwrapRoot) {
               result = this._unwrapAndDeserialize(p, ctxt, this._valueType, deser);
            } else if (this._valueToUpdate == null) {
               result = deser.deserialize(p, ctxt);
            } else {
               deser.deserialize(p, ctxt, this._valueToUpdate);
               result = this._valueToUpdate;
            }
         } else {
            result = this._valueToUpdate;
         }

         if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
            this._verifyNoTrailingTokens(p, ctxt, this._valueType);
         }

         var18 = result;
      } catch (Throwable var16) {
         var3 = var16;
         throw var16;
      } finally {
         if (p0 != null) {
            if (var3 != null) {
               try {
                  p.close();
               } catch (Throwable var15) {
                  var3.addSuppressed(var15);
               }
            } else {
               p0.close();
            }
         }

      }

      return var18;
   }

   protected final JsonNode _bindAndCloseAsTree(JsonParser p0) throws IOException {
      JsonParser p = p0;
      Throwable var3 = null;

      JsonNode var4;
      try {
         var4 = this._bindAsTree(p);
      } catch (Throwable var13) {
         var3 = var13;
         throw var13;
      } finally {
         if (p0 != null) {
            if (var3 != null) {
               try {
                  p.close();
               } catch (Throwable var12) {
                  var3.addSuppressed(var12);
               }
            } else {
               p0.close();
            }
         }

      }

      return var4;
   }

   protected final JsonNode _bindAsTree(JsonParser p) throws IOException {
      this._config.initialize(p);
      if (this._schema != null) {
         p.setSchema(this._schema);
      }

      JsonToken t = p.getCurrentToken();
      if (t == null) {
         t = p.nextToken();
         if (t == null) {
            return null;
         }
      }

      DeserializationContext ctxt = this.createDeserializationContext(p);
      if (t == JsonToken.VALUE_NULL) {
         return ctxt.getNodeFactory().nullNode();
      } else {
         JsonDeserializer<Object> deser = this._findTreeDeserializer(ctxt);
         Object result;
         if (this._unwrapRoot) {
            result = this._unwrapAndDeserialize(p, ctxt, JSON_NODE_TYPE, deser);
         } else {
            result = deser.deserialize(p, ctxt);
            if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
               this._verifyNoTrailingTokens(p, ctxt, JSON_NODE_TYPE);
            }
         }

         return (JsonNode)result;
      }
   }

   protected <T> MappingIterator<T> _bindAndReadValues(JsonParser p) throws IOException {
      DeserializationContext ctxt = this.createDeserializationContext(p);
      this._initForMultiRead(ctxt, p);
      p.nextToken();
      return this._newIterator(p, ctxt, this._findRootDeserializer(ctxt), true);
   }

   protected Object _unwrapAndDeserialize(JsonParser p, DeserializationContext ctxt, JavaType rootType, JsonDeserializer<Object> deser) throws IOException {
      PropertyName expRootName = this._config.findRootName(rootType);
      String expSimpleName = expRootName.getSimpleName();
      if (p.getCurrentToken() != JsonToken.START_OBJECT) {
         ctxt.reportWrongTokenException(rootType, JsonToken.START_OBJECT, "Current token not START_OBJECT (needed to unwrap root name '%s'), but %s", expSimpleName, p.getCurrentToken());
      }

      if (p.nextToken() != JsonToken.FIELD_NAME) {
         ctxt.reportWrongTokenException(rootType, JsonToken.FIELD_NAME, "Current token not FIELD_NAME (to contain expected root name '%s'), but %s", expSimpleName, p.getCurrentToken());
      }

      String actualName = p.getCurrentName();
      if (!expSimpleName.equals(actualName)) {
         ctxt.reportInputMismatch(rootType, "Root name '%s' does not match expected ('%s') for type %s", actualName, expSimpleName, rootType);
      }

      p.nextToken();
      Object result;
      if (this._valueToUpdate == null) {
         result = deser.deserialize(p, ctxt);
      } else {
         deser.deserialize(p, ctxt, this._valueToUpdate);
         result = this._valueToUpdate;
      }

      if (p.nextToken() != JsonToken.END_OBJECT) {
         ctxt.reportWrongTokenException(rootType, JsonToken.END_OBJECT, "Current token not END_OBJECT (to match wrapper object with root name '%s'), but %s", expSimpleName, p.getCurrentToken());
      }

      if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
         this._verifyNoTrailingTokens(p, ctxt, this._valueType);
      }

      return result;
   }

   protected JsonParser _considerFilter(JsonParser p, boolean multiValue) {
      return (JsonParser)(this._filter != null && !FilteringParserDelegate.class.isInstance(p) ? new FilteringParserDelegate(p, this._filter, false, multiValue) : p);
   }

   protected final void _verifyNoTrailingTokens(JsonParser p, DeserializationContext ctxt, JavaType bindType) throws IOException {
      JsonToken t = p.nextToken();
      if (t != null) {
         Class<?> bt = ClassUtil.rawClass(bindType);
         if (bt == null && this._valueToUpdate != null) {
            bt = this._valueToUpdate.getClass();
         }

         ctxt.reportTrailingTokens(bt, p, t);
      }

   }

   protected Object _detectBindAndClose(byte[] src, int offset, int length) throws IOException {
      DataFormatReaders.Match match = this._dataFormatReaders.findFormat(src, offset, length);
      if (!match.hasMatch()) {
         this._reportUnkownFormat(this._dataFormatReaders, match);
      }

      JsonParser p = match.createParserWithMatch();
      return match.getReader()._bindAndClose(p);
   }

   protected Object _detectBindAndClose(DataFormatReaders.Match match, boolean forceClosing) throws IOException {
      if (!match.hasMatch()) {
         this._reportUnkownFormat(this._dataFormatReaders, match);
      }

      JsonParser p = match.createParserWithMatch();
      if (forceClosing) {
         p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
      }

      return match.getReader()._bindAndClose(p);
   }

   protected <T> MappingIterator<T> _detectBindAndReadValues(DataFormatReaders.Match match, boolean forceClosing) throws IOException {
      if (!match.hasMatch()) {
         this._reportUnkownFormat(this._dataFormatReaders, match);
      }

      JsonParser p = match.createParserWithMatch();
      if (forceClosing) {
         p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
      }

      return match.getReader()._bindAndReadValues(p);
   }

   protected JsonNode _detectBindAndCloseAsTree(InputStream in) throws IOException {
      DataFormatReaders.Match match = this._dataFormatReaders.findFormat(in);
      if (!match.hasMatch()) {
         this._reportUnkownFormat(this._dataFormatReaders, match);
      }

      JsonParser p = match.createParserWithMatch();
      p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
      return match.getReader()._bindAndCloseAsTree(p);
   }

   protected void _reportUnkownFormat(DataFormatReaders detector, DataFormatReaders.Match match) throws JsonProcessingException {
      throw new JsonParseException((JsonParser)null, "Cannot detect format from input, does not look like any of detectable formats " + detector.toString());
   }

   protected void _verifySchemaType(FormatSchema schema) {
      if (schema != null && !this._parserFactory.canUseSchema(schema)) {
         throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + this._parserFactory.getFormatName());
      }
   }

   protected DefaultDeserializationContext createDeserializationContext(JsonParser p) {
      return this._context.createInstance(this._config, p, this._injectableValues);
   }

   protected InputStream _inputStream(URL src) throws IOException {
      return src.openStream();
   }

   protected InputStream _inputStream(File f) throws IOException {
      return new FileInputStream(f);
   }

   protected void _reportUndetectableSource(Object src) throws JsonProcessingException {
      throw new JsonParseException((JsonParser)null, "Cannot use source of type " + src.getClass().getName() + " with format auto-detection: must be byte- not char-based");
   }

   protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt) throws JsonMappingException {
      if (this._rootDeserializer != null) {
         return this._rootDeserializer;
      } else {
         JavaType t = this._valueType;
         if (t == null) {
            ctxt.reportBadDefinition((JavaType)null, "No value type configured for ObjectReader");
         }

         JsonDeserializer<Object> deser = (JsonDeserializer)this._rootDeserializers.get(t);
         if (deser != null) {
            return deser;
         } else {
            deser = ctxt.findRootValueDeserializer(t);
            if (deser == null) {
               ctxt.reportBadDefinition(t, "Cannot find a deserializer for type " + t);
            }

            this._rootDeserializers.put(t, deser);
            return deser;
         }
      }
   }

   protected JsonDeserializer<Object> _findTreeDeserializer(DeserializationContext ctxt) throws JsonMappingException {
      JsonDeserializer<Object> deser = (JsonDeserializer)this._rootDeserializers.get(JSON_NODE_TYPE);
      if (deser == null) {
         deser = ctxt.findRootValueDeserializer(JSON_NODE_TYPE);
         if (deser == null) {
            ctxt.reportBadDefinition(JSON_NODE_TYPE, "Cannot find a deserializer for type " + JSON_NODE_TYPE);
         }

         this._rootDeserializers.put(JSON_NODE_TYPE, deser);
      }

      return deser;
   }

   protected JsonDeserializer<Object> _prefetchRootDeserializer(JavaType valueType) {
      if (valueType != null && this._config.isEnabled(DeserializationFeature.EAGER_DESERIALIZER_FETCH)) {
         JsonDeserializer<Object> deser = (JsonDeserializer)this._rootDeserializers.get(valueType);
         if (deser == null) {
            try {
               DeserializationContext ctxt = this.createDeserializationContext((JsonParser)null);
               deser = ctxt.findRootValueDeserializer(valueType);
               if (deser != null) {
                  this._rootDeserializers.put(valueType, deser);
               }

               return deser;
            } catch (JsonProcessingException var4) {
            }
         }

         return deser;
      } else {
         return null;
      }
   }
}
