package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonCreator;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeBindings;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Annotations;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Converter;

public class BasicBeanDescription extends BeanDescription {
   private static final Class<?>[] NO_VIEWS = new Class[0];
   protected final POJOPropertiesCollector _propCollector;
   protected final MapperConfig<?> _config;
   protected final AnnotationIntrospector _annotationIntrospector;
   protected final AnnotatedClass _classInfo;
   protected Class<?>[] _defaultViews;
   protected boolean _defaultViewsResolved;
   protected List<BeanPropertyDefinition> _properties;
   protected ObjectIdInfo _objectIdInfo;

   protected BasicBeanDescription(POJOPropertiesCollector coll, JavaType type, AnnotatedClass classDef) {
      super(type);
      this._propCollector = coll;
      this._config = coll.getConfig();
      if (this._config == null) {
         this._annotationIntrospector = null;
      } else {
         this._annotationIntrospector = this._config.getAnnotationIntrospector();
      }

      this._classInfo = classDef;
   }

   protected BasicBeanDescription(MapperConfig<?> config, JavaType type, AnnotatedClass classDef, List<BeanPropertyDefinition> props) {
      super(type);
      this._propCollector = null;
      this._config = config;
      if (this._config == null) {
         this._annotationIntrospector = null;
      } else {
         this._annotationIntrospector = this._config.getAnnotationIntrospector();
      }

      this._classInfo = classDef;
      this._properties = props;
   }

   protected BasicBeanDescription(POJOPropertiesCollector coll) {
      this(coll, coll.getType(), coll.getClassDef());
      this._objectIdInfo = coll.getObjectIdInfo();
   }

   public static BasicBeanDescription forDeserialization(POJOPropertiesCollector coll) {
      return new BasicBeanDescription(coll);
   }

   public static BasicBeanDescription forSerialization(POJOPropertiesCollector coll) {
      return new BasicBeanDescription(coll);
   }

   public static BasicBeanDescription forOtherUse(MapperConfig<?> config, JavaType type, AnnotatedClass ac) {
      return new BasicBeanDescription(config, type, ac, Collections.emptyList());
   }

   protected List<BeanPropertyDefinition> _properties() {
      if (this._properties == null) {
         this._properties = this._propCollector.getProperties();
      }

      return this._properties;
   }

   public boolean removeProperty(String propName) {
      Iterator it = this._properties().iterator();

      BeanPropertyDefinition prop;
      do {
         if (!it.hasNext()) {
            return false;
         }

         prop = (BeanPropertyDefinition)it.next();
      } while(!prop.getName().equals(propName));

      it.remove();
      return true;
   }

   public boolean addProperty(BeanPropertyDefinition def) {
      if (this.hasProperty(def.getFullName())) {
         return false;
      } else {
         this._properties().add(def);
         return true;
      }
   }

   public boolean hasProperty(PropertyName name) {
      return this.findProperty(name) != null;
   }

   public BeanPropertyDefinition findProperty(PropertyName name) {
      Iterator i$ = this._properties().iterator();

      BeanPropertyDefinition prop;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         prop = (BeanPropertyDefinition)i$.next();
      } while(!prop.hasName(name));

      return prop;
   }

   public AnnotatedClass getClassInfo() {
      return this._classInfo;
   }

   public ObjectIdInfo getObjectIdInfo() {
      return this._objectIdInfo;
   }

   public List<BeanPropertyDefinition> findProperties() {
      return this._properties();
   }

   /** @deprecated */
   @Deprecated
   public AnnotatedMethod findJsonValueMethod() {
      return this._propCollector == null ? null : this._propCollector.getJsonValueMethod();
   }

   public AnnotatedMember findJsonValueAccessor() {
      return this._propCollector == null ? null : this._propCollector.getJsonValueAccessor();
   }

   public Set<String> getIgnoredPropertyNames() {
      Set<String> ign = this._propCollector == null ? null : this._propCollector.getIgnoredPropertyNames();
      return ign == null ? Collections.emptySet() : ign;
   }

   public boolean hasKnownClassAnnotations() {
      return this._classInfo.hasAnnotations();
   }

   public Annotations getClassAnnotations() {
      return this._classInfo.getAnnotations();
   }

   /** @deprecated */
   @Deprecated
   public TypeBindings bindingsForBeanType() {
      return this._type.getBindings();
   }

   /** @deprecated */
   @Deprecated
   public JavaType resolveType(Type jdkType) {
      return jdkType == null ? null : this._config.getTypeFactory().constructType(jdkType, this._type.getBindings());
   }

   public AnnotatedConstructor findDefaultConstructor() {
      return this._classInfo.getDefaultConstructor();
   }

   public AnnotatedMember findAnySetterAccessor() throws IllegalArgumentException {
      if (this._propCollector != null) {
         AnnotatedMethod anyMethod = this._propCollector.getAnySetterMethod();
         if (anyMethod != null) {
            Class<?> type = anyMethod.getRawParameterType(0);
            if (type != String.class && type != Object.class) {
               throw new IllegalArgumentException(String.format("Invalid 'any-setter' annotation on method '%s()': first argument not of type String or Object, but %s", anyMethod.getName(), type.getName()));
            }

            return anyMethod;
         }

         AnnotatedMember anyField = this._propCollector.getAnySetterField();
         if (anyField != null) {
            Class<?> type = anyField.getRawType();
            if (!Map.class.isAssignableFrom(type)) {
               throw new IllegalArgumentException(String.format("Invalid 'any-setter' annotation on field '%s': type is not instance of java.util.Map", anyField.getName()));
            }

            return anyField;
         }
      }

      return null;
   }

   public Map<Object, AnnotatedMember> findInjectables() {
      return this._propCollector != null ? this._propCollector.getInjectables() : Collections.emptyMap();
   }

   public List<AnnotatedConstructor> getConstructors() {
      return this._classInfo.getConstructors();
   }

   public Object instantiateBean(boolean fixAccess) {
      AnnotatedConstructor ac = this._classInfo.getDefaultConstructor();
      if (ac == null) {
         return null;
      } else {
         if (fixAccess) {
            ac.fixAccess(this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
         }

         try {
            return ac.getAnnotated().newInstance();
         } catch (Exception var5) {
            Object t;
            for(t = var5; ((Throwable)t).getCause() != null; t = ((Throwable)t).getCause()) {
            }

            ClassUtil.throwIfError((Throwable)t);
            ClassUtil.throwIfRTE((Throwable)t);
            throw new IllegalArgumentException("Failed to instantiate bean of type " + this._classInfo.getAnnotated().getName() + ": (" + t.getClass().getName() + ") " + ((Throwable)t).getMessage(), (Throwable)t);
         }
      }
   }

   public AnnotatedMethod findMethod(String name, Class<?>[] paramTypes) {
      return this._classInfo.findMethod(name, paramTypes);
   }

   public JsonFormat.Value findExpectedFormat(JsonFormat.Value defValue) {
      JsonFormat.Value v;
      if (this._annotationIntrospector != null) {
         v = this._annotationIntrospector.findFormat(this._classInfo);
         if (v != null) {
            if (defValue == null) {
               defValue = v;
            } else {
               defValue = defValue.withOverrides(v);
            }
         }
      }

      v = this._config.getDefaultPropertyFormat(this._classInfo.getRawType());
      if (v != null) {
         if (defValue == null) {
            defValue = v;
         } else {
            defValue = defValue.withOverrides(v);
         }
      }

      return defValue;
   }

   public Class<?>[] findDefaultViews() {
      if (!this._defaultViewsResolved) {
         this._defaultViewsResolved = true;
         Class<?>[] def = this._annotationIntrospector == null ? null : this._annotationIntrospector.findViews(this._classInfo);
         if (def == null && !this._config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION)) {
            def = NO_VIEWS;
         }

         this._defaultViews = def;
      }

      return this._defaultViews;
   }

   public Converter<Object, Object> findSerializationConverter() {
      return this._annotationIntrospector == null ? null : this._createConverter(this._annotationIntrospector.findSerializationConverter(this._classInfo));
   }

   public JsonInclude.Value findPropertyInclusion(JsonInclude.Value defValue) {
      if (this._annotationIntrospector != null) {
         JsonInclude.Value incl = this._annotationIntrospector.findPropertyInclusion(this._classInfo);
         if (incl != null) {
            return defValue == null ? incl : defValue.withOverrides(incl);
         }
      }

      return defValue;
   }

   public AnnotatedMember findAnyGetter() throws IllegalArgumentException {
      AnnotatedMember anyGetter = this._propCollector == null ? null : this._propCollector.getAnyGetter();
      if (anyGetter != null) {
         Class<?> type = anyGetter.getRawType();
         if (!Map.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Invalid 'any-getter' annotation on method " + anyGetter.getName() + "(): return type is not instance of java.util.Map");
         }
      }

      return anyGetter;
   }

   public List<BeanPropertyDefinition> findBackReferences() {
      List<BeanPropertyDefinition> result = null;
      HashSet<String> names = null;
      Iterator i$ = this._properties().iterator();

      while(i$.hasNext()) {
         BeanPropertyDefinition property = (BeanPropertyDefinition)i$.next();
         AnnotationIntrospector.ReferenceProperty refDef = property.findReferenceType();
         if (refDef != null && refDef.isBackReference()) {
            String refName = refDef.getName();
            if (result == null) {
               result = new ArrayList();
               names = new HashSet();
               names.add(refName);
            } else if (!names.add(refName)) {
               throw new IllegalArgumentException("Multiple back-reference properties with name '" + refName + "'");
            }

            result.add(property);
         }
      }

      return result;
   }

   /** @deprecated */
   @Deprecated
   public Map<String, AnnotatedMember> findBackReferenceProperties() {
      List<BeanPropertyDefinition> props = this.findBackReferences();
      if (props == null) {
         return null;
      } else {
         Map<String, AnnotatedMember> result = new HashMap();
         Iterator i$ = props.iterator();

         while(i$.hasNext()) {
            BeanPropertyDefinition prop = (BeanPropertyDefinition)i$.next();
            result.put(prop.getName(), prop.getMutator());
         }

         return result;
      }
   }

   public List<AnnotatedMethod> getFactoryMethods() {
      List<AnnotatedMethod> candidates = this._classInfo.getFactoryMethods();
      if (candidates.isEmpty()) {
         return candidates;
      } else {
         ArrayList<AnnotatedMethod> result = new ArrayList();
         Iterator i$ = candidates.iterator();

         while(i$.hasNext()) {
            AnnotatedMethod am = (AnnotatedMethod)i$.next();
            if (this.isFactoryMethod(am)) {
               result.add(am);
            }
         }

         return result;
      }
   }

   public Constructor<?> findSingleArgConstructor(Class<?>... argTypes) {
      Iterator i$ = this._classInfo.getConstructors().iterator();

      while(true) {
         AnnotatedConstructor ac;
         do {
            if (!i$.hasNext()) {
               return null;
            }

            ac = (AnnotatedConstructor)i$.next();
         } while(ac.getParameterCount() != 1);

         Class<?> actArg = ac.getRawParameterType(0);
         Class[] arr$ = argTypes;
         int len$ = argTypes.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Class<?> expArg = arr$[i$];
            if (expArg == actArg) {
               return ac.getAnnotated();
            }
         }
      }
   }

   public Method findFactoryMethod(Class<?>... expArgTypes) {
      Iterator i$ = this._classInfo.getFactoryMethods().iterator();

      while(true) {
         AnnotatedMethod am;
         do {
            do {
               if (!i$.hasNext()) {
                  return null;
               }

               am = (AnnotatedMethod)i$.next();
            } while(!this.isFactoryMethod(am));
         } while(am.getParameterCount() != 1);

         Class<?> actualArgType = am.getRawParameterType(0);
         Class[] arr$ = expArgTypes;
         int len$ = expArgTypes.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Class<?> expArgType = arr$[i$];
            if (actualArgType.isAssignableFrom(expArgType)) {
               return am.getAnnotated();
            }
         }
      }
   }

   protected boolean isFactoryMethod(AnnotatedMethod am) {
      Class<?> rt = am.getRawReturnType();
      if (!this.getBeanClass().isAssignableFrom(rt)) {
         return false;
      } else {
         JsonCreator.Mode mode = this._annotationIntrospector.findCreatorAnnotation(this._config, am);
         if (mode != null && mode != JsonCreator.Mode.DISABLED) {
            return true;
         } else {
            String name = am.getName();
            if ("valueOf".equals(name) && am.getParameterCount() == 1) {
               return true;
            } else {
               if ("fromString".equals(name) && am.getParameterCount() == 1) {
                  Class<?> cls = am.getRawParameterType(0);
                  if (cls == String.class || CharSequence.class.isAssignableFrom(cls)) {
                     return true;
                  }
               }

               return false;
            }
         }
      }
   }

   /** @deprecated */
   @Deprecated
   protected PropertyName _findCreatorPropertyName(AnnotatedParameter param) {
      PropertyName name = this._annotationIntrospector.findNameForDeserialization(param);
      if (name == null || name.isEmpty()) {
         String str = this._annotationIntrospector.findImplicitPropertyName(param);
         if (str != null && !str.isEmpty()) {
            name = PropertyName.construct(str);
         }
      }

      return name;
   }

   public Class<?> findPOJOBuilder() {
      return this._annotationIntrospector == null ? null : this._annotationIntrospector.findPOJOBuilder(this._classInfo);
   }

   public JsonPOJOBuilder.Value findPOJOBuilderConfig() {
      return this._annotationIntrospector == null ? null : this._annotationIntrospector.findPOJOBuilderConfig(this._classInfo);
   }

   public Converter<Object, Object> findDeserializationConverter() {
      return this._annotationIntrospector == null ? null : this._createConverter(this._annotationIntrospector.findDeserializationConverter(this._classInfo));
   }

   public String findClassDescription() {
      return this._annotationIntrospector == null ? null : this._annotationIntrospector.findClassDescription(this._classInfo);
   }

   /** @deprecated */
   @Deprecated
   public LinkedHashMap<String, AnnotatedField> _findPropertyFields(Collection<String> ignoredProperties, boolean forSerialization) {
      LinkedHashMap<String, AnnotatedField> results = new LinkedHashMap();
      Iterator i$ = this._properties().iterator();

      while(true) {
         AnnotatedField f;
         String name;
         do {
            BeanPropertyDefinition property;
            do {
               if (!i$.hasNext()) {
                  return results;
               }

               property = (BeanPropertyDefinition)i$.next();
               f = property.getField();
            } while(f == null);

            name = property.getName();
         } while(ignoredProperties != null && ignoredProperties.contains(name));

         results.put(name, f);
      }
   }

   protected Converter<Object, Object> _createConverter(Object converterDef) {
      if (converterDef == null) {
         return null;
      } else if (converterDef instanceof Converter) {
         return (Converter)converterDef;
      } else if (!(converterDef instanceof Class)) {
         throw new IllegalStateException("AnnotationIntrospector returned Converter definition of type " + converterDef.getClass().getName() + "; expected type Converter or Class<Converter> instead");
      } else {
         Class<?> converterClass = (Class)converterDef;
         if (converterClass != Converter.None.class && !ClassUtil.isBogusClass(converterClass)) {
            if (!Converter.class.isAssignableFrom(converterClass)) {
               throw new IllegalStateException("AnnotationIntrospector returned Class " + converterClass.getName() + "; expected Class<Converter>");
            } else {
               HandlerInstantiator hi = this._config.getHandlerInstantiator();
               Converter<?, ?> conv = hi == null ? null : hi.converterInstance(this._config, this._classInfo, converterClass);
               if (conv == null) {
                  conv = (Converter)ClassUtil.createInstance(converterClass, this._config.canOverrideAccessModifiers());
               }

               return conv;
            }
         } else {
            return null;
         }
      }
   }
}
