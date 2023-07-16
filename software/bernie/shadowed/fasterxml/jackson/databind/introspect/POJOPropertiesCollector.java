package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import software.bernie.shadowed.fasterxml.jackson.annotation.JacksonInject;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonCreator;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyNamingStrategy;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.util.BeanUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public class POJOPropertiesCollector {
   protected final MapperConfig<?> _config;
   protected final boolean _forSerialization;
   protected final boolean _stdBeanNaming;
   protected final JavaType _type;
   protected final AnnotatedClass _classDef;
   protected final VisibilityChecker<?> _visibilityChecker;
   protected final AnnotationIntrospector _annotationIntrospector;
   protected final boolean _useAnnotations;
   protected final String _mutatorPrefix;
   protected boolean _collected;
   protected LinkedHashMap<String, POJOPropertyBuilder> _properties;
   protected LinkedList<POJOPropertyBuilder> _creatorProperties;
   protected LinkedList<AnnotatedMember> _anyGetters;
   protected LinkedList<AnnotatedMethod> _anySetters;
   protected LinkedList<AnnotatedMember> _anySetterField;
   protected LinkedList<AnnotatedMember> _jsonValueAccessors;
   protected HashSet<String> _ignoredPropertyNames;
   protected LinkedHashMap<Object, AnnotatedMember> _injectables;

   protected POJOPropertiesCollector(MapperConfig<?> config, boolean forSerialization, JavaType type, AnnotatedClass classDef, String mutatorPrefix) {
      this._config = config;
      this._stdBeanNaming = config.isEnabled(MapperFeature.USE_STD_BEAN_NAMING);
      this._forSerialization = forSerialization;
      this._type = type;
      this._classDef = classDef;
      this._mutatorPrefix = mutatorPrefix == null ? "set" : mutatorPrefix;
      if (config.isAnnotationProcessingEnabled()) {
         this._useAnnotations = true;
         this._annotationIntrospector = this._config.getAnnotationIntrospector();
      } else {
         this._useAnnotations = false;
         this._annotationIntrospector = AnnotationIntrospector.nopInstance();
      }

      this._visibilityChecker = this._config.getDefaultVisibilityChecker(type.getRawClass(), classDef);
   }

   public MapperConfig<?> getConfig() {
      return this._config;
   }

   public JavaType getType() {
      return this._type;
   }

   public AnnotatedClass getClassDef() {
      return this._classDef;
   }

   public AnnotationIntrospector getAnnotationIntrospector() {
      return this._annotationIntrospector;
   }

   public List<BeanPropertyDefinition> getProperties() {
      Map<String, POJOPropertyBuilder> props = this.getPropertyMap();
      return new ArrayList(props.values());
   }

   public Map<Object, AnnotatedMember> getInjectables() {
      if (!this._collected) {
         this.collectAll();
      }

      return this._injectables;
   }

   /** @deprecated */
   @Deprecated
   public AnnotatedMethod getJsonValueMethod() {
      AnnotatedMember m = this.getJsonValueAccessor();
      return m instanceof AnnotatedMethod ? (AnnotatedMethod)m : null;
   }

   public AnnotatedMember getJsonValueAccessor() {
      if (!this._collected) {
         this.collectAll();
      }

      if (this._jsonValueAccessors != null) {
         if (this._jsonValueAccessors.size() > 1) {
            this.reportProblem("Multiple 'as-value' properties defined (%s vs %s)", this._jsonValueAccessors.get(0), this._jsonValueAccessors.get(1));
         }

         return (AnnotatedMember)this._jsonValueAccessors.get(0);
      } else {
         return null;
      }
   }

   public AnnotatedMember getAnyGetter() {
      if (!this._collected) {
         this.collectAll();
      }

      if (this._anyGetters != null) {
         if (this._anyGetters.size() > 1) {
            this.reportProblem("Multiple 'any-getters' defined (%s vs %s)", this._anyGetters.get(0), this._anyGetters.get(1));
         }

         return (AnnotatedMember)this._anyGetters.getFirst();
      } else {
         return null;
      }
   }

   public AnnotatedMember getAnySetterField() {
      if (!this._collected) {
         this.collectAll();
      }

      if (this._anySetterField != null) {
         if (this._anySetterField.size() > 1) {
            this.reportProblem("Multiple 'any-setter' fields defined (%s vs %s)", this._anySetterField.get(0), this._anySetterField.get(1));
         }

         return (AnnotatedMember)this._anySetterField.getFirst();
      } else {
         return null;
      }
   }

   public AnnotatedMethod getAnySetterMethod() {
      if (!this._collected) {
         this.collectAll();
      }

      if (this._anySetters != null) {
         if (this._anySetters.size() > 1) {
            this.reportProblem("Multiple 'any-setter' methods defined (%s vs %s)", this._anySetters.get(0), this._anySetters.get(1));
         }

         return (AnnotatedMethod)this._anySetters.getFirst();
      } else {
         return null;
      }
   }

   public Set<String> getIgnoredPropertyNames() {
      return this._ignoredPropertyNames;
   }

   public ObjectIdInfo getObjectIdInfo() {
      ObjectIdInfo info = this._annotationIntrospector.findObjectIdInfo(this._classDef);
      if (info != null) {
         info = this._annotationIntrospector.findObjectReferenceInfo(this._classDef, info);
      }

      return info;
   }

   public Class<?> findPOJOBuilderClass() {
      return this._annotationIntrospector.findPOJOBuilder(this._classDef);
   }

   protected Map<String, POJOPropertyBuilder> getPropertyMap() {
      if (!this._collected) {
         this.collectAll();
      }

      return this._properties;
   }

   protected void collectAll() {
      LinkedHashMap<String, POJOPropertyBuilder> props = new LinkedHashMap();
      this._addFields(props);
      this._addMethods(props);
      if (!this._classDef.isNonStaticInnerClass()) {
         this._addCreators(props);
      }

      this._addInjectables(props);
      this._removeUnwantedProperties(props);
      Iterator i$ = props.values().iterator();

      while(i$.hasNext()) {
         POJOPropertyBuilder property = (POJOPropertyBuilder)i$.next();
         property.mergeAnnotations(this._forSerialization);
      }

      this._removeUnwantedAccessor(props);
      this._renameProperties(props);
      PropertyNamingStrategy naming = this._findNamingStrategy();
      if (naming != null) {
         this._renameUsing(props, naming);
      }

      Iterator i$ = props.values().iterator();

      while(i$.hasNext()) {
         POJOPropertyBuilder property = (POJOPropertyBuilder)i$.next();
         property.trimByVisibility();
      }

      if (this._config.isEnabled(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME)) {
         this._renameWithWrappers(props);
      }

      this._sortProperties(props);
      this._properties = props;
      this._collected = true;
   }

   protected void _addFields(Map<String, POJOPropertyBuilder> props) {
      AnnotationIntrospector ai = this._annotationIntrospector;
      boolean pruneFinalFields = !this._forSerialization && !this._config.isEnabled(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS);
      boolean transientAsIgnoral = this._config.isEnabled(MapperFeature.PROPAGATE_TRANSIENT_MARKER);
      Iterator i$ = this._classDef.fields().iterator();

      while(true) {
         while(i$.hasNext()) {
            AnnotatedField f = (AnnotatedField)i$.next();
            String implName = ai.findImplicitPropertyName(f);
            if (Boolean.TRUE.equals(ai.hasAsValue(f))) {
               if (this._jsonValueAccessors == null) {
                  this._jsonValueAccessors = new LinkedList();
               }

               this._jsonValueAccessors.add(f);
            } else if (Boolean.TRUE.equals(ai.hasAnySetter(f))) {
               if (this._anySetterField == null) {
                  this._anySetterField = new LinkedList();
               }

               this._anySetterField.add(f);
            } else {
               if (implName == null) {
                  implName = f.getName();
               }

               PropertyName pn;
               if (this._forSerialization) {
                  pn = ai.findNameForSerialization(f);
               } else {
                  pn = ai.findNameForDeserialization(f);
               }

               boolean hasName = pn != null;
               boolean nameExplicit = hasName;
               if (hasName && pn.isEmpty()) {
                  pn = this._propNameFromSimple(implName);
                  nameExplicit = false;
               }

               boolean visible = pn != null;
               if (!visible) {
                  visible = this._visibilityChecker.isFieldVisible(f);
               }

               boolean ignored = ai.hasIgnoreMarker(f);
               if (f.isTransient() && !hasName) {
                  visible = false;
                  if (transientAsIgnoral) {
                     ignored = true;
                  }
               }

               if (!pruneFinalFields || pn != null || ignored || !Modifier.isFinal(f.getModifiers())) {
                  this._property(props, implName).addField(f, pn, nameExplicit, visible, ignored);
               }
            }
         }

         return;
      }
   }

   protected void _addCreators(Map<String, POJOPropertyBuilder> props) {
      if (this._useAnnotations) {
         Iterator i$ = this._classDef.getConstructors().iterator();

         int i;
         int len;
         while(i$.hasNext()) {
            AnnotatedConstructor ctor = (AnnotatedConstructor)i$.next();
            if (this._creatorProperties == null) {
               this._creatorProperties = new LinkedList();
            }

            i = 0;

            for(len = ctor.getParameterCount(); i < len; ++i) {
               this._addCreatorParam(props, ctor.getParameter(i));
            }
         }

         i$ = this._classDef.getFactoryMethods().iterator();

         while(i$.hasNext()) {
            AnnotatedMethod factory = (AnnotatedMethod)i$.next();
            if (this._creatorProperties == null) {
               this._creatorProperties = new LinkedList();
            }

            i = 0;

            for(len = factory.getParameterCount(); i < len; ++i) {
               this._addCreatorParam(props, factory.getParameter(i));
            }
         }

      }
   }

   protected void _addCreatorParam(Map<String, POJOPropertyBuilder> props, AnnotatedParameter param) {
      String impl = this._annotationIntrospector.findImplicitPropertyName(param);
      if (impl == null) {
         impl = "";
      }

      PropertyName pn = this._annotationIntrospector.findNameForDeserialization(param);
      boolean expl = pn != null && !pn.isEmpty();
      if (!expl) {
         if (impl.isEmpty()) {
            return;
         }

         JsonCreator.Mode creatorMode = this._annotationIntrospector.findCreatorAnnotation(this._config, param.getOwner());
         if (creatorMode == null || creatorMode == JsonCreator.Mode.DISABLED) {
            return;
         }

         pn = PropertyName.construct(impl);
      }

      POJOPropertyBuilder prop = expl && impl.isEmpty() ? this._property(props, pn) : this._property(props, impl);
      prop.addCtor(param, pn, expl, true, false);
      this._creatorProperties.add(prop);
   }

   protected void _addMethods(Map<String, POJOPropertyBuilder> props) {
      AnnotationIntrospector ai = this._annotationIntrospector;
      Iterator i$ = this._classDef.memberMethods().iterator();

      while(i$.hasNext()) {
         AnnotatedMethod m = (AnnotatedMethod)i$.next();
         int argCount = m.getParameterCount();
         if (argCount == 0) {
            this._addGetterMethod(props, m, ai);
         } else if (argCount == 1) {
            this._addSetterMethod(props, m, ai);
         } else if (argCount == 2 && ai != null && Boolean.TRUE.equals(ai.hasAnySetter(m))) {
            if (this._anySetters == null) {
               this._anySetters = new LinkedList();
            }

            this._anySetters.add(m);
         }
      }

   }

   protected void _addGetterMethod(Map<String, POJOPropertyBuilder> props, AnnotatedMethod m, AnnotationIntrospector ai) {
      if (m.hasReturnType()) {
         if (Boolean.TRUE.equals(ai.hasAnyGetter(m))) {
            if (this._anyGetters == null) {
               this._anyGetters = new LinkedList();
            }

            this._anyGetters.add(m);
         } else if (Boolean.TRUE.equals(ai.hasAsValue(m))) {
            if (this._jsonValueAccessors == null) {
               this._jsonValueAccessors = new LinkedList();
            }

            this._jsonValueAccessors.add(m);
         } else {
            PropertyName pn = ai.findNameForSerialization(m);
            boolean nameExplicit = pn != null;
            String implName;
            boolean visible;
            if (!nameExplicit) {
               implName = ai.findImplicitPropertyName(m);
               if (implName == null) {
                  implName = BeanUtil.okNameForRegularGetter(m, m.getName(), this._stdBeanNaming);
               }

               if (implName == null) {
                  implName = BeanUtil.okNameForIsGetter(m, m.getName(), this._stdBeanNaming);
                  if (implName == null) {
                     return;
                  }

                  visible = this._visibilityChecker.isIsGetterVisible(m);
               } else {
                  visible = this._visibilityChecker.isGetterVisible(m);
               }
            } else {
               implName = ai.findImplicitPropertyName(m);
               if (implName == null) {
                  implName = BeanUtil.okNameForGetter(m, this._stdBeanNaming);
               }

               if (implName == null) {
                  implName = m.getName();
               }

               if (pn.isEmpty()) {
                  pn = this._propNameFromSimple(implName);
                  nameExplicit = false;
               }

               visible = true;
            }

            boolean ignore = ai.hasIgnoreMarker(m);
            this._property(props, implName).addGetter(m, pn, nameExplicit, visible, ignore);
         }
      }
   }

   protected void _addSetterMethod(Map<String, POJOPropertyBuilder> props, AnnotatedMethod m, AnnotationIntrospector ai) {
      PropertyName pn = ai == null ? null : ai.findNameForDeserialization(m);
      boolean nameExplicit = pn != null;
      String implName;
      boolean visible;
      if (!nameExplicit) {
         implName = ai == null ? null : ai.findImplicitPropertyName(m);
         if (implName == null) {
            implName = BeanUtil.okNameForMutator(m, this._mutatorPrefix, this._stdBeanNaming);
         }

         if (implName == null) {
            return;
         }

         visible = this._visibilityChecker.isSetterVisible(m);
      } else {
         implName = ai == null ? null : ai.findImplicitPropertyName(m);
         if (implName == null) {
            implName = BeanUtil.okNameForMutator(m, this._mutatorPrefix, this._stdBeanNaming);
         }

         if (implName == null) {
            implName = m.getName();
         }

         if (pn.isEmpty()) {
            pn = this._propNameFromSimple(implName);
            nameExplicit = false;
         }

         visible = true;
      }

      boolean ignore = ai == null ? false : ai.hasIgnoreMarker(m);
      this._property(props, implName).addSetter(m, pn, nameExplicit, visible, ignore);
   }

   protected void _addInjectables(Map<String, POJOPropertyBuilder> props) {
      AnnotationIntrospector ai = this._annotationIntrospector;
      Iterator i$ = this._classDef.fields().iterator();

      while(i$.hasNext()) {
         AnnotatedField f = (AnnotatedField)i$.next();
         this._doAddInjectable(ai.findInjectableValue(f), f);
      }

      i$ = this._classDef.memberMethods().iterator();

      while(i$.hasNext()) {
         AnnotatedMethod m = (AnnotatedMethod)i$.next();
         if (m.getParameterCount() == 1) {
            this._doAddInjectable(ai.findInjectableValue(m), m);
         }
      }

   }

   protected void _doAddInjectable(JacksonInject.Value injectable, AnnotatedMember m) {
      if (injectable != null) {
         Object id = injectable.getId();
         if (this._injectables == null) {
            this._injectables = new LinkedHashMap();
         }

         AnnotatedMember prev = (AnnotatedMember)this._injectables.put(id, m);
         if (prev != null && prev.getClass() == m.getClass()) {
            String type = id.getClass().getName();
            throw new IllegalArgumentException("Duplicate injectable value with id '" + String.valueOf(id) + "' (of type " + type + ")");
         }
      }
   }

   private PropertyName _propNameFromSimple(String simpleName) {
      return PropertyName.construct(simpleName, (String)null);
   }

   protected void _removeUnwantedProperties(Map<String, POJOPropertyBuilder> props) {
      Iterator it = props.values().iterator();

      while(it.hasNext()) {
         POJOPropertyBuilder prop = (POJOPropertyBuilder)it.next();
         if (!prop.anyVisible()) {
            it.remove();
         } else if (prop.anyIgnorals()) {
            if (!prop.isExplicitlyIncluded()) {
               it.remove();
               this._collectIgnorals(prop.getName());
            } else {
               prop.removeIgnored();
               if (!this._forSerialization && !prop.couldDeserialize()) {
                  this._collectIgnorals(prop.getName());
               }
            }
         }
      }

   }

   protected void _removeUnwantedAccessor(Map<String, POJOPropertyBuilder> props) {
      boolean inferMutators = this._config.isEnabled(MapperFeature.INFER_PROPERTY_MUTATORS);
      Iterator it = props.values().iterator();

      while(it.hasNext()) {
         POJOPropertyBuilder prop = (POJOPropertyBuilder)it.next();
         JsonProperty.Access acc = prop.removeNonVisible(inferMutators);
         if (!this._forSerialization && acc == JsonProperty.Access.READ_ONLY) {
            this._collectIgnorals(prop.getName());
         }
      }

   }

   private void _collectIgnorals(String name) {
      if (!this._forSerialization) {
         if (this._ignoredPropertyNames == null) {
            this._ignoredPropertyNames = new HashSet();
         }

         this._ignoredPropertyNames.add(name);
      }

   }

   protected void _renameProperties(Map<String, POJOPropertyBuilder> props) {
      Iterator<Entry<String, POJOPropertyBuilder>> it = props.entrySet().iterator();
      LinkedList renamed = null;

      POJOPropertyBuilder prop;
      while(it.hasNext()) {
         Entry<String, POJOPropertyBuilder> entry = (Entry)it.next();
         prop = (POJOPropertyBuilder)entry.getValue();
         Collection<PropertyName> l = prop.findExplicitNames();
         if (!l.isEmpty()) {
            it.remove();
            if (renamed == null) {
               renamed = new LinkedList();
            }

            if (l.size() == 1) {
               PropertyName n = (PropertyName)l.iterator().next();
               renamed.add(prop.withName(n));
            } else {
               renamed.addAll(prop.explode(l));
            }
         }
      }

      if (renamed != null) {
         for(Iterator i$ = renamed.iterator(); i$.hasNext(); this._updateCreatorProperty(prop, this._creatorProperties)) {
            prop = (POJOPropertyBuilder)i$.next();
            String name = prop.getName();
            POJOPropertyBuilder old = (POJOPropertyBuilder)props.get(name);
            if (old == null) {
               props.put(name, prop);
            } else {
               old.addAll(prop);
            }
         }
      }

   }

   protected void _renameUsing(Map<String, POJOPropertyBuilder> propMap, PropertyNamingStrategy naming) {
      POJOPropertyBuilder[] props = (POJOPropertyBuilder[])propMap.values().toArray(new POJOPropertyBuilder[propMap.size()]);
      propMap.clear();
      POJOPropertyBuilder[] arr$ = props;
      int len$ = props.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         POJOPropertyBuilder prop = arr$[i$];
         PropertyName fullName = prop.getFullName();
         String rename = null;
         if (!prop.isExplicitlyNamed() || this._config.isEnabled(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING)) {
            if (this._forSerialization) {
               if (prop.hasGetter()) {
                  rename = naming.nameForGetterMethod(this._config, prop.getGetter(), fullName.getSimpleName());
               } else if (prop.hasField()) {
                  rename = naming.nameForField(this._config, prop.getField(), fullName.getSimpleName());
               }
            } else if (prop.hasSetter()) {
               rename = naming.nameForSetterMethod(this._config, prop.getSetter(), fullName.getSimpleName());
            } else if (prop.hasConstructorParameter()) {
               rename = naming.nameForConstructorParameter(this._config, prop.getConstructorParameter(), fullName.getSimpleName());
            } else if (prop.hasField()) {
               rename = naming.nameForField(this._config, prop.getField(), fullName.getSimpleName());
            } else if (prop.hasGetter()) {
               rename = naming.nameForGetterMethod(this._config, prop.getGetter(), fullName.getSimpleName());
            }
         }

         String simpleName;
         if (rename != null && !fullName.hasSimpleName(rename)) {
            prop = prop.withSimpleName(rename);
            simpleName = rename;
         } else {
            simpleName = fullName.getSimpleName();
         }

         POJOPropertyBuilder old = (POJOPropertyBuilder)propMap.get(simpleName);
         if (old == null) {
            propMap.put(simpleName, prop);
         } else {
            old.addAll(prop);
         }

         this._updateCreatorProperty(prop, this._creatorProperties);
      }

   }

   protected void _renameWithWrappers(Map<String, POJOPropertyBuilder> props) {
      Iterator<Entry<String, POJOPropertyBuilder>> it = props.entrySet().iterator();
      LinkedList renamed = null;

      POJOPropertyBuilder prop;
      while(it.hasNext()) {
         Entry<String, POJOPropertyBuilder> entry = (Entry)it.next();
         prop = (POJOPropertyBuilder)entry.getValue();
         AnnotatedMember member = prop.getPrimaryMember();
         if (member != null) {
            PropertyName wrapperName = this._annotationIntrospector.findWrapperName(member);
            if (wrapperName != null && wrapperName.hasSimpleName() && !wrapperName.equals(prop.getFullName())) {
               if (renamed == null) {
                  renamed = new LinkedList();
               }

               prop = prop.withName(wrapperName);
               renamed.add(prop);
               it.remove();
            }
         }
      }

      if (renamed != null) {
         Iterator i$ = renamed.iterator();

         while(i$.hasNext()) {
            prop = (POJOPropertyBuilder)i$.next();
            String name = prop.getName();
            POJOPropertyBuilder old = (POJOPropertyBuilder)props.get(name);
            if (old == null) {
               props.put(name, prop);
            } else {
               old.addAll(prop);
            }
         }
      }

   }

   protected void _sortProperties(Map<String, POJOPropertyBuilder> props) {
      AnnotationIntrospector intr = this._annotationIntrospector;
      Boolean alpha = intr.findSerializationSortAlphabetically(this._classDef);
      boolean sort;
      if (alpha == null) {
         sort = this._config.shouldSortPropertiesAlphabetically();
      } else {
         sort = alpha;
      }

      String[] propertyOrder = intr.findSerializationPropertyOrder(this._classDef);
      if (sort || this._creatorProperties != null || propertyOrder != null) {
         int size = props.size();
         Object all;
         if (sort) {
            all = new TreeMap();
         } else {
            all = new LinkedHashMap(size + size);
         }

         Iterator i$ = props.values().iterator();

         while(i$.hasNext()) {
            POJOPropertyBuilder prop = (POJOPropertyBuilder)i$.next();
            ((Map)all).put(prop.getName(), prop);
         }

         Map<String, POJOPropertyBuilder> ordered = new LinkedHashMap(size + size);
         String name;
         if (propertyOrder != null) {
            String[] arr$ = propertyOrder;
            int len$ = propertyOrder.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               name = arr$[i$];
               POJOPropertyBuilder w = (POJOPropertyBuilder)((Map)all).get(name);
               if (w == null) {
                  Iterator i$ = props.values().iterator();

                  while(i$.hasNext()) {
                     POJOPropertyBuilder prop = (POJOPropertyBuilder)i$.next();
                     if (name.equals(prop.getInternalName())) {
                        w = prop;
                        name = prop.getName();
                        break;
                     }
                  }
               }

               if (w != null) {
                  ordered.put(name, w);
               }
            }
         }

         if (this._creatorProperties != null) {
            Object cr;
            if (!sort) {
               cr = this._creatorProperties;
            } else {
               TreeMap<String, POJOPropertyBuilder> sorted = new TreeMap();
               Iterator i$ = this._creatorProperties.iterator();

               while(i$.hasNext()) {
                  POJOPropertyBuilder prop = (POJOPropertyBuilder)i$.next();
                  sorted.put(prop.getName(), prop);
               }

               cr = sorted.values();
            }

            Iterator i$ = ((Collection)cr).iterator();

            while(i$.hasNext()) {
               POJOPropertyBuilder prop = (POJOPropertyBuilder)i$.next();
               name = prop.getName();
               if (((Map)all).containsKey(name)) {
                  ordered.put(name, prop);
               }
            }
         }

         ordered.putAll((Map)all);
         props.clear();
         props.putAll(ordered);
      }
   }

   protected void reportProblem(String msg, Object... args) {
      if (args.length > 0) {
         msg = String.format(msg, args);
      }

      throw new IllegalArgumentException("Problem with definition of " + this._classDef + ": " + msg);
   }

   protected POJOPropertyBuilder _property(Map<String, POJOPropertyBuilder> props, PropertyName name) {
      String simpleName = name.getSimpleName();
      POJOPropertyBuilder prop = (POJOPropertyBuilder)props.get(simpleName);
      if (prop == null) {
         prop = new POJOPropertyBuilder(this._config, this._annotationIntrospector, this._forSerialization, name);
         props.put(simpleName, prop);
      }

      return prop;
   }

   protected POJOPropertyBuilder _property(Map<String, POJOPropertyBuilder> props, String implName) {
      POJOPropertyBuilder prop = (POJOPropertyBuilder)props.get(implName);
      if (prop == null) {
         prop = new POJOPropertyBuilder(this._config, this._annotationIntrospector, this._forSerialization, PropertyName.construct(implName));
         props.put(implName, prop);
      }

      return prop;
   }

   private PropertyNamingStrategy _findNamingStrategy() {
      Object namingDef = this._annotationIntrospector.findNamingStrategy(this._classDef);
      if (namingDef == null) {
         return this._config.getPropertyNamingStrategy();
      } else if (namingDef instanceof PropertyNamingStrategy) {
         return (PropertyNamingStrategy)namingDef;
      } else if (!(namingDef instanceof Class)) {
         throw new IllegalStateException("AnnotationIntrospector returned PropertyNamingStrategy definition of type " + namingDef.getClass().getName() + "; expected type PropertyNamingStrategy or Class<PropertyNamingStrategy> instead");
      } else {
         Class<?> namingClass = (Class)namingDef;
         if (namingClass == PropertyNamingStrategy.class) {
            return null;
         } else if (!PropertyNamingStrategy.class.isAssignableFrom(namingClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + namingClass.getName() + "; expected Class<PropertyNamingStrategy>");
         } else {
            HandlerInstantiator hi = this._config.getHandlerInstantiator();
            if (hi != null) {
               PropertyNamingStrategy pns = hi.namingStrategyInstance(this._config, this._classDef, namingClass);
               if (pns != null) {
                  return pns;
               }
            }

            return (PropertyNamingStrategy)ClassUtil.createInstance(namingClass, this._config.canOverrideAccessModifiers());
         }
      }
   }

   protected void _updateCreatorProperty(POJOPropertyBuilder prop, List<POJOPropertyBuilder> creatorProperties) {
      if (creatorProperties != null) {
         int i = 0;

         for(int len = creatorProperties.size(); i < len; ++i) {
            if (((POJOPropertyBuilder)creatorProperties.get(i)).getInternalName().equals(prop.getInternalName())) {
               creatorProperties.set(i, prop);
               break;
            }
         }
      }

   }
}
