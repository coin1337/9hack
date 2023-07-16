package software.bernie.shadowed.fasterxml.jackson.databind.deser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonIgnoreProperties;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdGenerator;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdGenerators;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdResolver;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.AbstractTypeResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyMetadata;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ErrorThrowingDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.FieldProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.MethodProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.SetterlessProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.ThrowableDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedField;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;

public class BeanDeserializerFactory extends BasicDeserializerFactory implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final Class<?>[] INIT_CAUSE_PARAMS = new Class[]{Throwable.class};
   protected static final Set<String> DEFAULT_NO_DESER_CLASS_NAMES;
   protected Set<String> _cfgIllegalClassNames;
   public static final BeanDeserializerFactory instance;

   public BeanDeserializerFactory(DeserializerFactoryConfig config) {
      super(config);
      this._cfgIllegalClassNames = DEFAULT_NO_DESER_CLASS_NAMES;
   }

   public DeserializerFactory withConfig(DeserializerFactoryConfig config) {
      if (this._factoryConfig == config) {
         return this;
      } else {
         ClassUtil.verifyMustOverride(BeanDeserializerFactory.class, this, "withConfig");
         return new BeanDeserializerFactory(config);
      }
   }

   public JsonDeserializer<Object> createBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      JsonDeserializer<Object> custom = this._findCustomBeanDeserializer(type, config, beanDesc);
      if (custom != null) {
         return custom;
      } else if (type.isThrowable()) {
         return this.buildThrowableDeserializer(ctxt, type, beanDesc);
      } else {
         if (type.isAbstract() && !type.isPrimitive() && !type.isEnumType()) {
            JavaType concreteType = this.materializeAbstractType(ctxt, type, beanDesc);
            if (concreteType != null) {
               beanDesc = config.introspect(concreteType);
               return this.buildBeanDeserializer(ctxt, concreteType, beanDesc);
            }
         }

         JsonDeserializer<Object> deser = this.findStdDeserializer(ctxt, type, beanDesc);
         if (deser != null) {
            return deser;
         } else if (!this.isPotentialBeanType(type.getRawClass())) {
            return null;
         } else {
            this.checkIllegalTypes(ctxt, type, beanDesc);
            return this.buildBeanDeserializer(ctxt, type, beanDesc);
         }
      }
   }

   public JsonDeserializer<Object> createBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription beanDesc, Class<?> builderClass) throws JsonMappingException {
      JavaType builderType = ctxt.constructType(builderClass);
      BeanDescription builderDesc = ctxt.getConfig().introspectForBuilder(builderType);
      return this.buildBuilderBasedDeserializer(ctxt, valueType, builderDesc);
   }

   protected JsonDeserializer<?> findStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      JsonDeserializer<?> deser = this.findDefaultDeserializer(ctxt, type, beanDesc);
      BeanDeserializerModifier mod;
      if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
         for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); deser = mod.modifyDeserializer(ctxt.getConfig(), beanDesc, deser)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      return deser;
   }

   protected JavaType materializeAbstractType(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      Iterator i$ = this._factoryConfig.abstractTypeResolvers().iterator();

      JavaType concrete;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         AbstractTypeResolver r = (AbstractTypeResolver)i$.next();
         concrete = r.resolveAbstractType(ctxt.getConfig(), beanDesc);
      } while(concrete == null);

      return concrete;
   }

   public JsonDeserializer<Object> buildBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      ValueInstantiator valueInstantiator;
      try {
         valueInstantiator = this.findValueInstantiator(ctxt, beanDesc);
      } catch (NoClassDefFoundError var10) {
         return new ErrorThrowingDeserializer(var10);
      } catch (IllegalArgumentException var11) {
         throw InvalidDefinitionException.from((JsonParser)ctxt.getParser(), var11.getMessage(), beanDesc, (BeanPropertyDefinition)null);
      }

      BeanDeserializerBuilder builder = this.constructBeanDeserializerBuilder(ctxt, beanDesc);
      builder.setValueInstantiator(valueInstantiator);
      this.addBeanProps(ctxt, beanDesc, builder);
      this.addObjectIdReader(ctxt, beanDesc, builder);
      this.addBackReferenceProperties(ctxt, beanDesc, builder);
      this.addInjectables(ctxt, beanDesc, builder);
      DeserializationConfig config = ctxt.getConfig();
      BeanDeserializerModifier mod;
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); builder = mod.updateBuilder(config, beanDesc, builder)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      Object deserializer;
      if (type.isAbstract() && !valueInstantiator.canInstantiate()) {
         deserializer = builder.buildAbstract();
      } else {
         deserializer = builder.build();
      }

      BeanDeserializerModifier mod;
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); deserializer = mod.modifyDeserializer(config, beanDesc, (JsonDeserializer)deserializer)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      return (JsonDeserializer)deserializer;
   }

   protected JsonDeserializer<Object> buildBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription builderDesc) throws JsonMappingException {
      ValueInstantiator valueInstantiator;
      try {
         valueInstantiator = this.findValueInstantiator(ctxt, builderDesc);
      } catch (NoClassDefFoundError var13) {
         return new ErrorThrowingDeserializer(var13);
      } catch (IllegalArgumentException var14) {
         throw InvalidDefinitionException.from((JsonParser)ctxt.getParser(), var14.getMessage(), builderDesc, (BeanPropertyDefinition)null);
      }

      DeserializationConfig config = ctxt.getConfig();
      BeanDeserializerBuilder builder = this.constructBeanDeserializerBuilder(ctxt, builderDesc);
      builder.setValueInstantiator(valueInstantiator);
      this.addBeanProps(ctxt, builderDesc, builder);
      this.addObjectIdReader(ctxt, builderDesc, builder);
      this.addBackReferenceProperties(ctxt, builderDesc, builder);
      this.addInjectables(ctxt, builderDesc, builder);
      JsonPOJOBuilder.Value builderConfig = builderDesc.findPOJOBuilderConfig();
      String buildMethodName = builderConfig == null ? "build" : builderConfig.buildMethodName;
      AnnotatedMethod buildMethod = builderDesc.findMethod(buildMethodName, (Class[])null);
      if (buildMethod != null && config.canOverrideAccessModifiers()) {
         ClassUtil.checkAndFixAccess(buildMethod.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
      }

      builder.setPOJOBuilder(buildMethod, builderConfig);
      BeanDeserializerModifier mod;
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); builder = mod.updateBuilder(config, builderDesc, builder)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      JsonDeserializer<?> deserializer = builder.buildBuilderBased(valueType, buildMethodName);
      BeanDeserializerModifier mod;
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); deserializer = mod.modifyDeserializer(config, builderDesc, deserializer)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      return deserializer;
   }

   protected void addObjectIdReader(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
      ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
      if (objectIdInfo != null) {
         Class<?> implClass = objectIdInfo.getGeneratorType();
         ObjectIdResolver resolver = ctxt.objectIdResolverInstance(beanDesc.getClassInfo(), objectIdInfo);
         JavaType idType;
         SettableBeanProperty idProp;
         Object gen;
         if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
            PropertyName propName = objectIdInfo.getPropertyName();
            idProp = builder.findProperty(propName);
            if (idProp == null) {
               throw new IllegalArgumentException("Invalid Object Id definition for " + beanDesc.getBeanClass().getName() + ": cannot find property with name '" + propName + "'");
            }

            idType = idProp.getType();
            gen = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
         } else {
            JavaType type = ctxt.constructType(implClass);
            idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
            idProp = null;
            gen = ctxt.objectIdGeneratorInstance(beanDesc.getClassInfo(), objectIdInfo);
         }

         JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
         builder.setObjectIdReader(ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), (ObjectIdGenerator)gen, deser, idProp, resolver));
      }
   }

   public JsonDeserializer<Object> buildThrowableDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      BeanDeserializerBuilder builder = this.constructBeanDeserializerBuilder(ctxt, beanDesc);
      builder.setValueInstantiator(this.findValueInstantiator(ctxt, beanDesc));
      this.addBeanProps(ctxt, beanDesc, builder);
      AnnotatedMethod am = beanDesc.findMethod("initCause", INIT_CAUSE_PARAMS);
      if (am != null) {
         SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), am, new PropertyName("cause"));
         SettableBeanProperty prop = this.constructSettableProperty(ctxt, beanDesc, propDef, am.getParameterType(0));
         if (prop != null) {
            builder.addOrReplaceProperty(prop, true);
         }
      }

      builder.addIgnorable("localizedMessage");
      builder.addIgnorable("suppressed");
      builder.addIgnorable("message");
      BeanDeserializerModifier mod;
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); builder = mod.updateBuilder(config, beanDesc, builder)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      JsonDeserializer<?> deserializer = builder.build();
      if (deserializer instanceof BeanDeserializer) {
         deserializer = new ThrowableDeserializer((BeanDeserializer)deserializer);
      }

      BeanDeserializerModifier mod;
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); deserializer = mod.modifyDeserializer(config, beanDesc, (JsonDeserializer)deserializer)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      return (JsonDeserializer)deserializer;
   }

   protected BeanDeserializerBuilder constructBeanDeserializerBuilder(DeserializationContext ctxt, BeanDescription beanDesc) {
      return new BeanDeserializerBuilder(beanDesc, ctxt);
   }

   protected void addBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
      boolean isConcrete = !beanDesc.getType().isAbstract();
      SettableBeanProperty[] creatorProps = isConcrete ? builder.getValueInstantiator().getFromObjectArguments(ctxt.getConfig()) : null;
      boolean hasCreatorProps = creatorProps != null;
      JsonIgnoreProperties.Value ignorals = ctxt.getConfig().getDefaultPropertyIgnorals(beanDesc.getBeanClass(), beanDesc.getClassInfo());
      Set ignored;
      if (ignorals != null) {
         boolean ignoreAny = ignorals.getIgnoreUnknown();
         builder.setIgnoreUnknownProperties(ignoreAny);
         ignored = ignorals.findIgnoredForDeserialization();
         Iterator i$ = ignored.iterator();

         while(i$.hasNext()) {
            String propName = (String)i$.next();
            builder.addIgnorable(propName);
         }
      } else {
         ignored = Collections.emptySet();
      }

      AnnotatedMember anySetter = beanDesc.findAnySetterAccessor();
      if (anySetter != null) {
         builder.setAnySetter(this.constructAnySetter(ctxt, beanDesc, anySetter));
      } else {
         Collection<String> ignored2 = beanDesc.getIgnoredPropertyNames();
         if (ignored2 != null) {
            Iterator i$ = ignored2.iterator();

            while(i$.hasNext()) {
               String propName = (String)i$.next();
               builder.addIgnorable(propName);
            }
         }
      }

      boolean useGettersAsSetters = ctxt.isEnabled(MapperFeature.USE_GETTERS_AS_SETTERS) && ctxt.isEnabled(MapperFeature.AUTO_DETECT_GETTERS);
      List<BeanPropertyDefinition> propDefs = this.filterBeanProps(ctxt, beanDesc, builder, beanDesc.findProperties(), ignored);
      BeanDeserializerModifier mod;
      Iterator i$;
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); propDefs = mod.updateProperties(ctxt.getConfig(), beanDesc, propDefs)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      i$ = propDefs.iterator();

      while(true) {
         while(true) {
            while(i$.hasNext()) {
               BeanPropertyDefinition propDef = (BeanPropertyDefinition)i$.next();
               SettableBeanProperty prop = null;
               AnnotatedMethod getter;
               JavaType propertyType;
               if (propDef.hasSetter()) {
                  getter = propDef.getSetter();
                  propertyType = getter.getParameterType(0);
                  prop = this.constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
               } else if (propDef.hasField()) {
                  AnnotatedField field = propDef.getField();
                  propertyType = field.getType();
                  prop = this.constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
               } else {
                  getter = propDef.getGetter();
                  if (getter != null) {
                     if (useGettersAsSetters && this._isSetterlessType(getter.getRawType())) {
                        prop = this.constructSetterlessProperty(ctxt, beanDesc, propDef);
                     } else if (!propDef.hasConstructorParameter()) {
                        PropertyMetadata md = propDef.getMetadata();
                        if (md.getMergeInfo() != null) {
                           prop = this.constructSetterlessProperty(ctxt, beanDesc, propDef);
                        }
                     }
                  }
               }

               if (hasCreatorProps && propDef.hasConstructorParameter()) {
                  String name = propDef.getName();
                  CreatorProperty cprop = null;
                  int len$;
                  if (creatorProps != null) {
                     SettableBeanProperty[] arr$ = creatorProps;
                     int len$ = creatorProps.length;

                     for(len$ = 0; len$ < len$; ++len$) {
                        SettableBeanProperty cp = arr$[len$];
                        if (name.equals(cp.getName()) && cp instanceof CreatorProperty) {
                           cprop = (CreatorProperty)cp;
                           break;
                        }
                     }
                  }

                  if (cprop == null) {
                     List<String> n = new ArrayList();
                     SettableBeanProperty[] arr$ = creatorProps;
                     len$ = creatorProps.length;

                     for(int i$ = 0; i$ < len$; ++i$) {
                        SettableBeanProperty cp = arr$[i$];
                        n.add(cp.getName());
                     }

                     ctxt.reportBadPropertyDefinition(beanDesc, propDef, "Could not find creator property with name '%s' (known Creator properties: %s)", name, n);
                  } else {
                     if (prop != null) {
                        cprop.setFallbackSetter(prop);
                     }

                     Class<?>[] views = propDef.findViews();
                     if (views == null) {
                        views = beanDesc.findDefaultViews();
                     }

                     cprop.setViews(views);
                     builder.addCreatorProperty(cprop);
                  }
               } else if (prop != null) {
                  Class<?>[] views = propDef.findViews();
                  if (views == null) {
                     views = beanDesc.findDefaultViews();
                  }

                  prop.setViews(views);
                  builder.addProperty(prop);
               }
            }

            return;
         }
      }
   }

   private boolean _isSetterlessType(Class<?> rawType) {
      return Collection.class.isAssignableFrom(rawType) || Map.class.isAssignableFrom(rawType);
   }

   protected List<BeanPropertyDefinition> filterBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder, List<BeanPropertyDefinition> propDefsIn, Set<String> ignored) throws JsonMappingException {
      ArrayList<BeanPropertyDefinition> result = new ArrayList(Math.max(4, propDefsIn.size()));
      HashMap<Class<?>, Boolean> ignoredTypes = new HashMap();
      Iterator i$ = propDefsIn.iterator();

      while(true) {
         while(true) {
            BeanPropertyDefinition property;
            String name;
            do {
               if (!i$.hasNext()) {
                  return result;
               }

               property = (BeanPropertyDefinition)i$.next();
               name = property.getName();
            } while(ignored.contains(name));

            if (!property.hasConstructorParameter()) {
               Class<?> rawPropertyType = property.getRawPrimaryType();
               if (rawPropertyType != null && this.isIgnorableType(ctxt.getConfig(), property, rawPropertyType, ignoredTypes)) {
                  builder.addIgnorable(name);
                  continue;
               }
            }

            result.add(property);
         }
      }
   }

   protected void addBackReferenceProperties(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
      List<BeanPropertyDefinition> refProps = beanDesc.findBackReferences();
      if (refProps != null) {
         Iterator i$ = refProps.iterator();

         while(i$.hasNext()) {
            BeanPropertyDefinition refProp = (BeanPropertyDefinition)i$.next();
            String refName = refProp.findReferenceName();
            builder.addBackReferenceProperty(refName, this.constructSettableProperty(ctxt, beanDesc, refProp, refProp.getPrimaryType()));
         }
      }

   }

   /** @deprecated */
   @Deprecated
   protected void addReferenceProperties(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
      this.addBackReferenceProperties(ctxt, beanDesc, builder);
   }

   protected void addInjectables(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
      Map<Object, AnnotatedMember> raw = beanDesc.findInjectables();
      if (raw != null) {
         Iterator i$ = raw.entrySet().iterator();

         while(i$.hasNext()) {
            Entry<Object, AnnotatedMember> entry = (Entry)i$.next();
            AnnotatedMember m = (AnnotatedMember)entry.getValue();
            builder.addInjectable(PropertyName.construct(m.getName()), m.getType(), beanDesc.getClassAnnotations(), m, entry.getKey());
         }
      }

   }

   protected SettableAnyProperty constructAnySetter(DeserializationContext ctxt, BeanDescription beanDesc, AnnotatedMember mutator) throws JsonMappingException {
      BeanProperty.Std prop;
      JavaType keyType;
      JavaType valueType;
      if (mutator instanceof AnnotatedMethod) {
         AnnotatedMethod am = (AnnotatedMethod)mutator;
         keyType = am.getParameterType(0);
         valueType = am.getParameterType(1);
         valueType = this.resolveMemberAndTypeAnnotations(ctxt, mutator, valueType);
         prop = new BeanProperty.Std(PropertyName.construct(mutator.getName()), valueType, (PropertyName)null, mutator, PropertyMetadata.STD_OPTIONAL);
      } else {
         if (!(mutator instanceof AnnotatedField)) {
            return (SettableAnyProperty)ctxt.reportBadDefinition(beanDesc.getType(), String.format("Unrecognized mutator type for any setter: %s", mutator.getClass()));
         }

         AnnotatedField af = (AnnotatedField)mutator;
         JavaType mapType = af.getType();
         mapType = this.resolveMemberAndTypeAnnotations(ctxt, mutator, mapType);
         keyType = mapType.getKeyType();
         valueType = mapType.getContentType();
         prop = new BeanProperty.Std(PropertyName.construct(mutator.getName()), mapType, (PropertyName)null, mutator, PropertyMetadata.STD_OPTIONAL);
      }

      KeyDeserializer keyDeser = this.findKeyDeserializerFromAnnotation(ctxt, mutator);
      if (keyDeser == null) {
         keyDeser = (KeyDeserializer)keyType.getValueHandler();
      }

      if (keyDeser == null) {
         keyDeser = ctxt.findKeyDeserializer(keyType, prop);
      } else if (keyDeser instanceof ContextualKeyDeserializer) {
         keyDeser = ((ContextualKeyDeserializer)keyDeser).createContextual(ctxt, prop);
      }

      JsonDeserializer<Object> deser = this.findContentDeserializerFromAnnotation(ctxt, mutator);
      if (deser == null) {
         deser = (JsonDeserializer)valueType.getValueHandler();
      }

      if (deser != null) {
         deser = ctxt.handlePrimaryContextualization(deser, prop, valueType);
      }

      TypeDeserializer typeDeser = (TypeDeserializer)valueType.getTypeHandler();
      return new SettableAnyProperty(prop, mutator, valueType, keyDeser, deser, typeDeser);
   }

   protected SettableBeanProperty constructSettableProperty(DeserializationContext ctxt, BeanDescription beanDesc, BeanPropertyDefinition propDef, JavaType propType0) throws JsonMappingException {
      AnnotatedMember mutator = propDef.getNonConstructorMutator();
      if (mutator == null) {
         ctxt.reportBadPropertyDefinition(beanDesc, propDef, "No non-constructor mutator available");
      }

      JavaType type = this.resolveMemberAndTypeAnnotations(ctxt, mutator, propType0);
      TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
      Object prop;
      if (mutator instanceof AnnotatedMethod) {
         prop = new MethodProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedMethod)mutator);
      } else {
         prop = new FieldProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedField)mutator);
      }

      JsonDeserializer<?> deser = this.findDeserializerFromAnnotation(ctxt, mutator);
      if (deser == null) {
         deser = (JsonDeserializer)type.getValueHandler();
      }

      if (deser != null) {
         deser = ctxt.handlePrimaryContextualization(deser, (BeanProperty)prop, type);
         prop = ((SettableBeanProperty)prop).withValueDeserializer(deser);
      }

      AnnotationIntrospector.ReferenceProperty ref = propDef.findReferenceType();
      if (ref != null && ref.isManagedReference()) {
         ((SettableBeanProperty)prop).setManagedReferenceName(ref.getName());
      }

      ObjectIdInfo objectIdInfo = propDef.findObjectIdInfo();
      if (objectIdInfo != null) {
         ((SettableBeanProperty)prop).setObjectIdInfo(objectIdInfo);
      }

      return (SettableBeanProperty)prop;
   }

   protected SettableBeanProperty constructSetterlessProperty(DeserializationContext ctxt, BeanDescription beanDesc, BeanPropertyDefinition propDef) throws JsonMappingException {
      AnnotatedMethod getter = propDef.getGetter();
      JavaType type = this.resolveMemberAndTypeAnnotations(ctxt, getter, getter.getType());
      TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
      SettableBeanProperty prop = new SetterlessProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), getter);
      JsonDeserializer<?> deser = this.findDeserializerFromAnnotation(ctxt, getter);
      if (deser == null) {
         deser = (JsonDeserializer)type.getValueHandler();
      }

      if (deser != null) {
         deser = ctxt.handlePrimaryContextualization(deser, (BeanProperty)prop, type);
         prop = ((SettableBeanProperty)prop).withValueDeserializer(deser);
      }

      return (SettableBeanProperty)prop;
   }

   protected boolean isPotentialBeanType(Class<?> type) {
      String typeStr = ClassUtil.canBeABeanType(type);
      if (typeStr != null) {
         throw new IllegalArgumentException("Cannot deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
      } else if (ClassUtil.isProxyType(type)) {
         throw new IllegalArgumentException("Cannot deserialize Proxy class " + type.getName() + " as a Bean");
      } else {
         typeStr = ClassUtil.isLocalType(type, true);
         if (typeStr != null) {
            throw new IllegalArgumentException("Cannot deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
         } else {
            return true;
         }
      }
   }

   protected boolean isIgnorableType(DeserializationConfig config, BeanPropertyDefinition propDef, Class<?> type, Map<Class<?>, Boolean> ignoredTypes) {
      Boolean status = (Boolean)ignoredTypes.get(type);
      if (status != null) {
         return status;
      } else {
         if (type != String.class && !type.isPrimitive()) {
            status = config.getConfigOverride(type).getIsIgnoredType();
            if (status == null) {
               BeanDescription desc = config.introspectClassAnnotations(type);
               status = config.getAnnotationIntrospector().isIgnorableType(desc.getClassInfo());
               if (status == null) {
                  status = Boolean.FALSE;
               }
            }
         } else {
            status = Boolean.FALSE;
         }

         ignoredTypes.put(type, status);
         return status;
      }
   }

   protected void checkIllegalTypes(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      String full = type.getRawClass().getName();
      if (this._cfgIllegalClassNames.contains(full)) {
         ctxt.reportBadTypeDefinition(beanDesc, "Illegal type (%s) to deserialize: prevented for security reasons", full);
      }

   }

   static {
      Set<String> s = new HashSet();
      s.add("org.apache.commons.collections.functors.InvokerTransformer");
      s.add("org.apache.commons.collections.functors.InstantiateTransformer");
      s.add("org.apache.commons.collections4.functors.InvokerTransformer");
      s.add("org.apache.commons.collections4.functors.InstantiateTransformer");
      s.add("org.codehaus.groovy.runtime.ConvertedClosure");
      s.add("org.codehaus.groovy.runtime.MethodClosure");
      s.add("org.springframework.beans.factory.ObjectFactory");
      s.add("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");
      s.add("org.apache.xalan.xsltc.trax.TemplatesImpl");
      s.add("com.sun.rowset.JdbcRowSetImpl");
      DEFAULT_NO_DESER_CLASS_NAMES = Collections.unmodifiableSet(s);
      instance = new BeanDeserializerFactory(new DeserializerFactoryConfig());
   }
}
