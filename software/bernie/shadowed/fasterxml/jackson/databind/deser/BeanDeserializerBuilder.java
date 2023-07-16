package software.bernie.shadowed.fasterxml.jackson.databind.deser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyMetadata;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ValueInjector;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Annotations;

public class BeanDeserializerBuilder {
   protected final DeserializationConfig _config;
   protected final DeserializationContext _context;
   protected final BeanDescription _beanDesc;
   protected final Map<String, SettableBeanProperty> _properties = new LinkedHashMap();
   protected List<ValueInjector> _injectables;
   protected HashMap<String, SettableBeanProperty> _backRefProperties;
   protected HashSet<String> _ignorableProps;
   protected ValueInstantiator _valueInstantiator;
   protected ObjectIdReader _objectIdReader;
   protected SettableAnyProperty _anySetter;
   protected boolean _ignoreAllUnknown;
   protected AnnotatedMethod _buildMethod;
   protected JsonPOJOBuilder.Value _builderConfig;

   public BeanDeserializerBuilder(BeanDescription beanDesc, DeserializationContext ctxt) {
      this._beanDesc = beanDesc;
      this._context = ctxt;
      this._config = ctxt.getConfig();
   }

   protected BeanDeserializerBuilder(BeanDeserializerBuilder src) {
      this._beanDesc = src._beanDesc;
      this._context = src._context;
      this._config = src._config;
      this._properties.putAll(src._properties);
      this._injectables = _copy(src._injectables);
      this._backRefProperties = _copy(src._backRefProperties);
      this._ignorableProps = src._ignorableProps;
      this._valueInstantiator = src._valueInstantiator;
      this._objectIdReader = src._objectIdReader;
      this._anySetter = src._anySetter;
      this._ignoreAllUnknown = src._ignoreAllUnknown;
      this._buildMethod = src._buildMethod;
      this._builderConfig = src._builderConfig;
   }

   private static HashMap<String, SettableBeanProperty> _copy(HashMap<String, SettableBeanProperty> src) {
      return src == null ? null : new HashMap(src);
   }

   private static <T> List<T> _copy(List<T> src) {
      return src == null ? null : new ArrayList(src);
   }

   public void addOrReplaceProperty(SettableBeanProperty prop, boolean allowOverride) {
      this._properties.put(prop.getName(), prop);
   }

   public void addProperty(SettableBeanProperty prop) {
      SettableBeanProperty old = (SettableBeanProperty)this._properties.put(prop.getName(), prop);
      if (old != null && old != prop) {
         throw new IllegalArgumentException("Duplicate property '" + prop.getName() + "' for " + this._beanDesc.getType());
      }
   }

   public void addBackReferenceProperty(String referenceName, SettableBeanProperty prop) {
      if (this._backRefProperties == null) {
         this._backRefProperties = new HashMap(4);
      }

      prop.fixAccess(this._config);
      this._backRefProperties.put(referenceName, prop);
      if (this._properties != null) {
         this._properties.remove(prop.getName());
      }

   }

   public void addInjectable(PropertyName propName, JavaType propType, Annotations contextAnnotations, AnnotatedMember member, Object valueId) {
      if (this._injectables == null) {
         this._injectables = new ArrayList();
      }

      boolean fixAccess = this._config.canOverrideAccessModifiers();
      boolean forceAccess = fixAccess && this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS);
      if (fixAccess) {
         member.fixAccess(forceAccess);
      }

      this._injectables.add(new ValueInjector(propName, propType, member, valueId));
   }

   public void addIgnorable(String propName) {
      if (this._ignorableProps == null) {
         this._ignorableProps = new HashSet();
      }

      this._ignorableProps.add(propName);
   }

   public void addCreatorProperty(SettableBeanProperty prop) {
      this.addProperty(prop);
   }

   public void setAnySetter(SettableAnyProperty s) {
      if (this._anySetter != null && s != null) {
         throw new IllegalStateException("_anySetter already set to non-null");
      } else {
         this._anySetter = s;
      }
   }

   public void setIgnoreUnknownProperties(boolean ignore) {
      this._ignoreAllUnknown = ignore;
   }

   public void setValueInstantiator(ValueInstantiator inst) {
      this._valueInstantiator = inst;
   }

   public void setObjectIdReader(ObjectIdReader r) {
      this._objectIdReader = r;
   }

   public void setPOJOBuilder(AnnotatedMethod buildMethod, JsonPOJOBuilder.Value config) {
      this._buildMethod = buildMethod;
      this._builderConfig = config;
   }

   public Iterator<SettableBeanProperty> getProperties() {
      return this._properties.values().iterator();
   }

   public SettableBeanProperty findProperty(PropertyName propertyName) {
      return (SettableBeanProperty)this._properties.get(propertyName.getSimpleName());
   }

   public boolean hasProperty(PropertyName propertyName) {
      return this.findProperty(propertyName) != null;
   }

   public SettableBeanProperty removeProperty(PropertyName name) {
      return (SettableBeanProperty)this._properties.remove(name.getSimpleName());
   }

   public SettableAnyProperty getAnySetter() {
      return this._anySetter;
   }

   public ValueInstantiator getValueInstantiator() {
      return this._valueInstantiator;
   }

   public List<ValueInjector> getInjectables() {
      return this._injectables;
   }

   public ObjectIdReader getObjectIdReader() {
      return this._objectIdReader;
   }

   public AnnotatedMethod getBuildMethod() {
      return this._buildMethod;
   }

   public JsonPOJOBuilder.Value getBuilderConfig() {
      return this._builderConfig;
   }

   public JsonDeserializer<?> build() {
      Collection<SettableBeanProperty> props = this._properties.values();
      this._fixAccess(props);
      BeanPropertyMap propertyMap = BeanPropertyMap.construct(props, this._config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES), this._collectAliases(props));
      propertyMap.assignIndexes();
      boolean anyViews = !this._config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
      if (!anyViews) {
         Iterator i$ = props.iterator();

         while(i$.hasNext()) {
            SettableBeanProperty prop = (SettableBeanProperty)i$.next();
            if (prop.hasViews()) {
               anyViews = true;
               break;
            }
         }
      }

      if (this._objectIdReader != null) {
         ObjectIdValueProperty prop = new ObjectIdValueProperty(this._objectIdReader, PropertyMetadata.STD_REQUIRED);
         propertyMap = propertyMap.withProperty(prop);
      }

      return new BeanDeserializer(this, this._beanDesc, propertyMap, this._backRefProperties, this._ignorableProps, this._ignoreAllUnknown, anyViews);
   }

   public AbstractDeserializer buildAbstract() {
      return new AbstractDeserializer(this, this._beanDesc, this._backRefProperties, this._properties);
   }

   public JsonDeserializer<?> buildBuilderBased(JavaType valueType, String expBuildMethodName) throws JsonMappingException {
      if (this._buildMethod == null) {
         if (!expBuildMethodName.isEmpty()) {
            this._context.reportBadDefinition(this._beanDesc.getType(), String.format("Builder class %s does not have build method (name: '%s')", this._beanDesc.getBeanClass().getName(), expBuildMethodName));
         }
      } else {
         Class<?> rawBuildType = this._buildMethod.getRawReturnType();
         Class<?> rawValueType = valueType.getRawClass();
         if (rawBuildType != rawValueType && !rawBuildType.isAssignableFrom(rawValueType) && !rawValueType.isAssignableFrom(rawBuildType)) {
            this._context.reportBadDefinition(this._beanDesc.getType(), String.format("Build method '%s' has wrong return type (%s), not compatible with POJO type (%s)", this._buildMethod.getFullName(), rawBuildType.getName(), valueType.getRawClass().getName()));
         }
      }

      Collection<SettableBeanProperty> props = this._properties.values();
      this._fixAccess(props);
      BeanPropertyMap propertyMap = BeanPropertyMap.construct(props, this._config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES), this._collectAliases(props));
      propertyMap.assignIndexes();
      boolean anyViews = !this._config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
      if (!anyViews) {
         Iterator i$ = props.iterator();

         while(i$.hasNext()) {
            SettableBeanProperty prop = (SettableBeanProperty)i$.next();
            if (prop.hasViews()) {
               anyViews = true;
               break;
            }
         }
      }

      if (this._objectIdReader != null) {
         ObjectIdValueProperty prop = new ObjectIdValueProperty(this._objectIdReader, PropertyMetadata.STD_REQUIRED);
         propertyMap = propertyMap.withProperty(prop);
      }

      return new BuilderBasedDeserializer(this, this._beanDesc, valueType, propertyMap, this._backRefProperties, this._ignorableProps, this._ignoreAllUnknown, anyViews);
   }

   protected void _fixAccess(Collection<SettableBeanProperty> mainProps) {
      Iterator i$ = mainProps.iterator();

      while(i$.hasNext()) {
         SettableBeanProperty prop = (SettableBeanProperty)i$.next();
         prop.fixAccess(this._config);
      }

      if (this._anySetter != null) {
         this._anySetter.fixAccess(this._config);
      }

      if (this._buildMethod != null) {
         this._buildMethod.fixAccess(this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
      }

   }

   protected Map<String, List<PropertyName>> _collectAliases(Collection<SettableBeanProperty> props) {
      Map<String, List<PropertyName>> mapping = null;
      AnnotationIntrospector intr = this._config.getAnnotationIntrospector();
      if (intr != null) {
         Iterator i$ = props.iterator();

         while(i$.hasNext()) {
            SettableBeanProperty prop = (SettableBeanProperty)i$.next();
            List<PropertyName> aliases = intr.findPropertyAliases(prop.getMember());
            if (aliases != null && !aliases.isEmpty()) {
               if (mapping == null) {
                  mapping = new HashMap();
               }

               mapping.put(prop.getName(), aliases);
            }
         }
      }

      return (Map)(mapping == null ? Collections.emptyMap() : mapping);
   }
}
