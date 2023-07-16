package software.bernie.shadowed.fasterxml.jackson.databind.deser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicReference;
import software.bernie.shadowed.fasterxml.jackson.annotation.JacksonInject;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonCreator;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonIgnoreProperties;
import software.bernie.shadowed.fasterxml.jackson.core.JsonLocation;
import software.bernie.shadowed.fasterxml.jackson.databind.AbstractTypeResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyMetadata;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.CreatorCollector;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.ArrayBlockingQueueDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.DateDeserializers;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.EnumMapDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.EnumSetDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.JdkDeserializers;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.JsonLocationInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.MapDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.MapEntryDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.StdKeyDeserializers;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.StringCollectionDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.StringDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.TokenBufferDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.Annotated;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedClass;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.VisibilityChecker;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.NamedType;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ArrayType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.CollectionLikeType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.CollectionType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.MapLikeType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.MapType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ReferenceType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.EnumResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import software.bernie.shadowed.fasterxml.jackson.databind.util.TokenBuffer;

public abstract class BasicDeserializerFactory extends DeserializerFactory implements Serializable {
   private static final Class<?> CLASS_OBJECT = Object.class;
   private static final Class<?> CLASS_STRING = String.class;
   private static final Class<?> CLASS_CHAR_BUFFER = CharSequence.class;
   private static final Class<?> CLASS_ITERABLE = Iterable.class;
   private static final Class<?> CLASS_MAP_ENTRY = Entry.class;
   protected static final PropertyName UNWRAPPED_CREATOR_PARAM_NAME = new PropertyName("@JsonUnwrapped");
   static final HashMap<String, Class<? extends Map>> _mapFallbacks = new HashMap();
   static final HashMap<String, Class<? extends Collection>> _collectionFallbacks;
   protected final DeserializerFactoryConfig _factoryConfig;

   protected BasicDeserializerFactory(DeserializerFactoryConfig config) {
      this._factoryConfig = config;
   }

   public DeserializerFactoryConfig getFactoryConfig() {
      return this._factoryConfig;
   }

   protected abstract DeserializerFactory withConfig(DeserializerFactoryConfig var1);

   public final DeserializerFactory withAdditionalDeserializers(Deserializers additional) {
      return this.withConfig(this._factoryConfig.withAdditionalDeserializers(additional));
   }

   public final DeserializerFactory withAdditionalKeyDeserializers(KeyDeserializers additional) {
      return this.withConfig(this._factoryConfig.withAdditionalKeyDeserializers(additional));
   }

   public final DeserializerFactory withDeserializerModifier(BeanDeserializerModifier modifier) {
      return this.withConfig(this._factoryConfig.withDeserializerModifier(modifier));
   }

   public final DeserializerFactory withAbstractTypeResolver(AbstractTypeResolver resolver) {
      return this.withConfig(this._factoryConfig.withAbstractTypeResolver(resolver));
   }

   public final DeserializerFactory withValueInstantiators(ValueInstantiators instantiators) {
      return this.withConfig(this._factoryConfig.withValueInstantiators(instantiators));
   }

   public JavaType mapAbstractType(DeserializationConfig config, JavaType type) throws JsonMappingException {
      while(true) {
         JavaType next = this._mapAbstractType2(config, type);
         if (next == null) {
            return type;
         }

         Class<?> prevCls = type.getRawClass();
         Class<?> nextCls = next.getRawClass();
         if (prevCls == nextCls || !prevCls.isAssignableFrom(nextCls)) {
            throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former");
         }

         type = next;
      }
   }

   private JavaType _mapAbstractType2(DeserializationConfig config, JavaType type) throws JsonMappingException {
      Class<?> currClass = type.getRawClass();
      if (this._factoryConfig.hasAbstractTypeResolvers()) {
         Iterator i$ = this._factoryConfig.abstractTypeResolvers().iterator();

         while(i$.hasNext()) {
            AbstractTypeResolver resolver = (AbstractTypeResolver)i$.next();
            JavaType concrete = resolver.findTypeMapping(config, type);
            if (ClassUtil.rawClass(concrete) != currClass) {
               return concrete;
            }
         }
      }

      return null;
   }

   public ValueInstantiator findValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      ValueInstantiator instantiator = null;
      AnnotatedClass ac = beanDesc.getClassInfo();
      Object instDef = ctxt.getAnnotationIntrospector().findValueInstantiator(ac);
      if (instDef != null) {
         instantiator = this._valueInstantiatorInstance(config, ac, instDef);
      }

      if (instantiator == null) {
         instantiator = this._findStdValueInstantiator(config, beanDesc);
         if (instantiator == null) {
            instantiator = this._constructDefaultValueInstantiator(ctxt, beanDesc);
         }
      }

      if (this._factoryConfig.hasValueInstantiators()) {
         Iterator i$ = this._factoryConfig.valueInstantiators().iterator();

         while(i$.hasNext()) {
            ValueInstantiators insts = (ValueInstantiators)i$.next();
            instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
            if (instantiator == null) {
               ctxt.reportBadTypeDefinition(beanDesc, "Broken registered ValueInstantiators (of type %s): returned null ValueInstantiator", insts.getClass().getName());
            }
         }
      }

      if (instantiator.getIncompleteParameter() != null) {
         AnnotatedParameter nonAnnotatedParam = instantiator.getIncompleteParameter();
         AnnotatedWithParams ctor = nonAnnotatedParam.getOwner();
         throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of constructor " + ctor + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
      } else {
         return instantiator;
      }
   }

   private ValueInstantiator _findStdValueInstantiator(DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
      return beanDesc.getBeanClass() == JsonLocation.class ? new JsonLocationInstantiator() : null;
   }

   protected ValueInstantiator _constructDefaultValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
      CreatorCollector creators = new CreatorCollector(beanDesc, ctxt.getConfig());
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      DeserializationConfig config = ctxt.getConfig();
      VisibilityChecker<?> vchecker = config.getDefaultVisibilityChecker(beanDesc.getBeanClass(), beanDesc.getClassInfo());
      Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorDefs = this._findCreatorsFromProperties(ctxt, beanDesc);
      this._addDeserializerFactoryMethods(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
      if (beanDesc.getType().isConcrete()) {
         this._addDeserializerConstructors(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
      }

      return creators.constructValueInstantiator(config);
   }

   protected Map<AnnotatedWithParams, BeanPropertyDefinition[]> _findCreatorsFromProperties(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
      Map<AnnotatedWithParams, BeanPropertyDefinition[]> result = Collections.emptyMap();
      Iterator i$ = beanDesc.findProperties().iterator();

      while(i$.hasNext()) {
         BeanPropertyDefinition propDef = (BeanPropertyDefinition)i$.next();

         BeanPropertyDefinition[] defs;
         int index;
         for(Iterator it = propDef.getConstructorParameters(); it.hasNext(); defs[index] = propDef) {
            AnnotatedParameter param = (AnnotatedParameter)it.next();
            AnnotatedWithParams owner = param.getOwner();
            defs = (BeanPropertyDefinition[])((Map)result).get(owner);
            index = param.getIndex();
            if (defs == null) {
               if (((Map)result).isEmpty()) {
                  result = new LinkedHashMap();
               }

               defs = new BeanPropertyDefinition[owner.getParameterCount()];
               ((Map)result).put(owner, defs);
            } else if (defs[index] != null) {
               throw new IllegalStateException("Conflict: parameter #" + index + " of " + owner + " bound to more than one property; " + defs[index] + " vs " + propDef);
            }
         }
      }

      return (Map)result;
   }

   public ValueInstantiator _valueInstantiatorInstance(DeserializationConfig config, Annotated annotated, Object instDef) throws JsonMappingException {
      if (instDef == null) {
         return null;
      } else if (instDef instanceof ValueInstantiator) {
         return (ValueInstantiator)instDef;
      } else if (!(instDef instanceof Class)) {
         throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
      } else {
         Class<?> instClass = (Class)instDef;
         if (ClassUtil.isBogusClass(instClass)) {
            return null;
         } else if (!ValueInstantiator.class.isAssignableFrom(instClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>");
         } else {
            HandlerInstantiator hi = config.getHandlerInstantiator();
            if (hi != null) {
               ValueInstantiator inst = hi.valueInstantiatorInstance(config, annotated, instClass);
               if (inst != null) {
                  return inst;
               }
            }

            return (ValueInstantiator)ClassUtil.createInstance(instClass, config.canOverrideAccessModifiers());
         }
      }
   }

   protected void _addDeserializerConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams) throws JsonMappingException {
      AnnotatedConstructor defaultCtor = beanDesc.findDefaultConstructor();
      if (defaultCtor != null && (!creators.hasDefaultCreator() || this._hasCreatorAnnotation(ctxt, defaultCtor))) {
         creators.setDefaultCreator(defaultCtor);
      }

      boolean isNonStaticInnerClass = beanDesc.isNonStaticInnerClass();
      if (!isNonStaticInnerClass) {
         List<AnnotatedConstructor> implicitCtors = null;
         Iterator i$ = beanDesc.getConstructors().iterator();

         while(true) {
            while(i$.hasNext()) {
               AnnotatedConstructor ctor = (AnnotatedConstructor)i$.next();
               JsonCreator.Mode creatorMode = intr.findCreatorAnnotation(ctxt.getConfig(), ctor);
               boolean isCreator = creatorMode != null && creatorMode != JsonCreator.Mode.DISABLED;
               BeanPropertyDefinition[] propDefs = (BeanPropertyDefinition[])creatorParams.get(ctor);
               int argCount = ctor.getParameterCount();
               if (argCount != 1) {
                  AnnotatedParameter nonAnnotatedParam = null;
                  SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
                  int explicitNameCount = 0;
                  int implicitWithCreatorCount = 0;
                  int injectCount = 0;

                  int i;
                  for(i = 0; i < argCount; ++i) {
                     AnnotatedParameter param = ctor.getParameter(i);
                     BeanPropertyDefinition propDef = propDefs == null ? null : propDefs[i];
                     JacksonInject.Value injectId = intr.findInjectableValue(param);
                     PropertyName name = propDef == null ? null : propDef.getFullName();
                     if (propDef != null && propDef.isExplicitlyNamed()) {
                        ++explicitNameCount;
                        properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
                     } else if (injectId != null) {
                        ++injectCount;
                        properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
                     } else {
                        NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
                        if (unwrapper != null) {
                           this._reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
                        } else if (isCreator && name != null && !name.isEmpty()) {
                           ++implicitWithCreatorCount;
                           properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
                        } else if (nonAnnotatedParam == null) {
                           nonAnnotatedParam = param;
                        }
                     }
                  }

                  i = explicitNameCount + implicitWithCreatorCount;
                  if (isCreator || explicitNameCount > 0 || injectCount > 0) {
                     if (i + injectCount == argCount) {
                        creators.addPropertyCreator(ctor, isCreator, properties);
                        continue;
                     }

                     if (explicitNameCount == 0 && injectCount + 1 == argCount) {
                        creators.addDelegatingCreator(ctor, isCreator, properties);
                        continue;
                     }

                     PropertyName impl = this._findImplicitParamName(nonAnnotatedParam, intr);
                     if (impl == null || impl.isEmpty()) {
                        int ix = nonAnnotatedParam.getIndex();
                        throw new IllegalArgumentException("Argument #" + ix + " of constructor " + ctor + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
                     }
                  }

                  if (!creators.hasDefaultCreator()) {
                     if (implicitCtors == null) {
                        implicitCtors = new LinkedList();
                     }

                     implicitCtors.add(ctor);
                  }
               } else {
                  BeanPropertyDefinition argDef = propDefs == null ? null : propDefs[0];
                  boolean useProps = this._checkIfCreatorPropertyBased(intr, ctor, argDef, creatorMode);
                  if (useProps) {
                     SettableBeanProperty[] properties = new SettableBeanProperty[1];
                     PropertyName name = argDef == null ? null : argDef.getFullName();
                     AnnotatedParameter arg = ctor.getParameter(0);
                     properties[0] = this.constructCreatorProperty(ctxt, beanDesc, name, 0, arg, intr.findInjectableValue(arg));
                     creators.addPropertyCreator(ctor, isCreator, properties);
                  } else {
                     this._handleSingleArgumentConstructor(ctxt, beanDesc, vchecker, intr, creators, ctor, isCreator, vchecker.isCreatorVisible((AnnotatedMember)ctor));
                     if (argDef != null) {
                        ((POJOPropertyBuilder)argDef).removeConstructors();
                     }
                  }
               }
            }

            if (implicitCtors != null && !creators.hasDelegatingCreator() && !creators.hasPropertyBasedCreator()) {
               this._checkImplicitlyNamedConstructors(ctxt, beanDesc, vchecker, intr, creators, implicitCtors);
            }

            return;
         }
      }
   }

   protected void _checkImplicitlyNamedConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, List<AnnotatedConstructor> implicitCtors) throws JsonMappingException {
      AnnotatedConstructor found = null;
      SettableBeanProperty[] foundProps = null;
      Iterator i$ = implicitCtors.iterator();

      int len$;
      label48:
      while(i$.hasNext()) {
         AnnotatedConstructor ctor = (AnnotatedConstructor)i$.next();
         if (vchecker.isCreatorVisible((AnnotatedMember)ctor)) {
            len$ = ctor.getParameterCount();
            SettableBeanProperty[] properties = new SettableBeanProperty[len$];

            for(int i = 0; i < len$; ++i) {
               AnnotatedParameter param = ctor.getParameter(i);
               PropertyName name = this._findParamName(param, intr);
               if (name == null || name.isEmpty()) {
                  continue label48;
               }

               properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, param.getIndex(), param, (JacksonInject.Value)null);
            }

            if (found != null) {
               found = null;
               break;
            }

            found = ctor;
            foundProps = properties;
         }
      }

      if (found != null) {
         creators.addPropertyCreator(found, false, foundProps);
         BasicBeanDescription bbd = (BasicBeanDescription)beanDesc;
         SettableBeanProperty[] arr$ = foundProps;
         len$ = foundProps.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            SettableBeanProperty prop = arr$[i$];
            PropertyName pn = ((SettableBeanProperty)prop).getFullName();
            if (!bbd.hasProperty(pn)) {
               BeanPropertyDefinition newDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), ((SettableBeanProperty)prop).getMember(), pn);
               bbd.addProperty(newDef);
            }
         }
      }

   }

   protected boolean _handleSingleArgumentConstructor(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, AnnotatedConstructor ctor, boolean isCreator, boolean isVisible) throws JsonMappingException {
      Class<?> type = ctor.getRawParameterType(0);
      if (type != String.class && type != CharSequence.class) {
         if (type != Integer.TYPE && type != Integer.class) {
            if (type != Long.TYPE && type != Long.class) {
               if (type != Double.TYPE && type != Double.class) {
                  if (type != Boolean.TYPE && type != Boolean.class) {
                     if (isCreator) {
                        creators.addDelegatingCreator(ctor, isCreator, (SettableBeanProperty[])null);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     if (isCreator || isVisible) {
                        creators.addBooleanCreator(ctor, isCreator);
                     }

                     return true;
                  }
               } else {
                  if (isCreator || isVisible) {
                     creators.addDoubleCreator(ctor, isCreator);
                  }

                  return true;
               }
            } else {
               if (isCreator || isVisible) {
                  creators.addLongCreator(ctor, isCreator);
               }

               return true;
            }
         } else {
            if (isCreator || isVisible) {
               creators.addIntCreator(ctor, isCreator);
            }

            return true;
         }
      } else {
         if (isCreator || isVisible) {
            creators.addStringCreator(ctor, isCreator);
         }

         return true;
      }
   }

   protected void _addDeserializerFactoryMethods(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      Iterator i$ = beanDesc.getFactoryMethods().iterator();

      while(true) {
         while(true) {
            AnnotatedMethod factory;
            boolean isCreator;
            int argCount;
            int explicitNameCount;
            int injectCount;
            int i;
            AnnotatedParameter nonAnnotatedParam;
            SettableBeanProperty[] properties;
            do {
               BeanPropertyDefinition[] propDefs;
               label82:
               while(true) {
                  while(i$.hasNext()) {
                     factory = (AnnotatedMethod)i$.next();
                     JsonCreator.Mode creatorMode = intr.findCreatorAnnotation(ctxt.getConfig(), factory);
                     isCreator = creatorMode != null && creatorMode != JsonCreator.Mode.DISABLED;
                     argCount = factory.getParameterCount();
                     if (argCount != 0) {
                        propDefs = (BeanPropertyDefinition[])creatorParams.get(factory);
                        if (argCount == 1) {
                           BeanPropertyDefinition argDef = propDefs == null ? null : propDefs[0];
                           boolean useProps = this._checkIfCreatorPropertyBased(intr, factory, argDef, creatorMode);
                           if (useProps) {
                              break label82;
                           }

                           this._handleSingleArgumentFactory(config, beanDesc, vchecker, intr, creators, factory, isCreator);
                           if (argDef != null) {
                              ((POJOPropertyBuilder)argDef).removeConstructors();
                           }
                        } else {
                           if (!isCreator) {
                              continue;
                           }
                           break label82;
                        }
                     } else if (isCreator) {
                        creators.setDefaultCreator(factory);
                     }
                  }

                  return;
               }

               nonAnnotatedParam = null;
               properties = new SettableBeanProperty[argCount];
               int implicitNameCount = 0;
               explicitNameCount = 0;
               injectCount = 0;

               for(i = 0; i < argCount; ++i) {
                  AnnotatedParameter param = factory.getParameter(i);
                  BeanPropertyDefinition propDef = propDefs == null ? null : propDefs[i];
                  JacksonInject.Value injectable = intr.findInjectableValue(param);
                  PropertyName name = propDef == null ? null : propDef.getFullName();
                  if (propDef != null && propDef.isExplicitlyNamed()) {
                     ++explicitNameCount;
                     properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
                  } else if (injectable != null) {
                     ++injectCount;
                     properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
                  } else {
                     NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
                     if (unwrapper != null) {
                        this._reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
                     } else if (isCreator && name != null && !name.isEmpty()) {
                        ++implicitNameCount;
                        properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
                     } else if (nonAnnotatedParam == null) {
                        nonAnnotatedParam = param;
                     }
                  }
               }

               i = explicitNameCount + implicitNameCount;
            } while(!isCreator && explicitNameCount <= 0 && injectCount <= 0);

            if (i + injectCount != argCount) {
               if (explicitNameCount != 0 || injectCount + 1 != argCount) {
                  throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of factory method " + factory + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
               }

               creators.addDelegatingCreator(factory, isCreator, properties);
            } else {
               creators.addPropertyCreator(factory, isCreator, properties);
            }
         }
      }
   }

   protected boolean _handleSingleArgumentFactory(DeserializationConfig config, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, AnnotatedMethod factory, boolean isCreator) throws JsonMappingException {
      Class<?> type = factory.getRawParameterType(0);
      if (type != String.class && type != CharSequence.class) {
         if (type != Integer.TYPE && type != Integer.class) {
            if (type != Long.TYPE && type != Long.class) {
               if (type != Double.TYPE && type != Double.class) {
                  if (type != Boolean.TYPE && type != Boolean.class) {
                     if (isCreator) {
                        creators.addDelegatingCreator(factory, isCreator, (SettableBeanProperty[])null);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     if (isCreator || vchecker.isCreatorVisible((AnnotatedMember)factory)) {
                        creators.addBooleanCreator(factory, isCreator);
                     }

                     return true;
                  }
               } else {
                  if (isCreator || vchecker.isCreatorVisible((AnnotatedMember)factory)) {
                     creators.addDoubleCreator(factory, isCreator);
                  }

                  return true;
               }
            } else {
               if (isCreator || vchecker.isCreatorVisible((AnnotatedMember)factory)) {
                  creators.addLongCreator(factory, isCreator);
               }

               return true;
            }
         } else {
            if (isCreator || vchecker.isCreatorVisible((AnnotatedMember)factory)) {
               creators.addIntCreator(factory, isCreator);
            }

            return true;
         }
      } else {
         if (isCreator || vchecker.isCreatorVisible((AnnotatedMember)factory)) {
            creators.addStringCreator(factory, isCreator);
         }

         return true;
      }
   }

   protected void _reportUnwrappedCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, AnnotatedParameter param) throws JsonMappingException {
      ctxt.reportBadDefinition(beanDesc.getType(), String.format("Cannot define Creator parameter %d as `@JsonUnwrapped`: combination not yet supported", param.getIndex()));
   }

   protected SettableBeanProperty constructCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, PropertyName name, int index, AnnotatedParameter param, JacksonInject.Value injectable) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      PropertyMetadata metadata;
      if (intr == null) {
         metadata = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
      } else {
         Boolean b = intr.hasRequiredMarker(param);
         String desc = intr.findPropertyDescription(param);
         Integer idx = intr.findPropertyIndex(param);
         String def = intr.findPropertyDefaultValue(param);
         metadata = PropertyMetadata.construct(b, desc, idx, def);
      }

      JavaType type = this.resolveMemberAndTypeAnnotations(ctxt, param, param.getType());
      BeanProperty.Std property = new BeanProperty.Std(name, type, intr.findWrapperName(param), param, metadata);
      TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
      if (typeDeser == null) {
         typeDeser = this.findTypeDeserializer(config, type);
      }

      Object injectableValueId = injectable == null ? null : injectable.getId();
      SettableBeanProperty prop = new CreatorProperty(name, type, property.getWrapperName(), typeDeser, beanDesc.getClassAnnotations(), param, index, injectableValueId, metadata);
      JsonDeserializer<?> deser = this.findDeserializerFromAnnotation(ctxt, param);
      if (deser == null) {
         deser = (JsonDeserializer)type.getValueHandler();
      }

      if (deser != null) {
         deser = ctxt.handlePrimaryContextualization(deser, (BeanProperty)prop, type);
         prop = ((SettableBeanProperty)prop).withValueDeserializer(deser);
      }

      return (SettableBeanProperty)prop;
   }

   protected PropertyName _findParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
      if (param != null && intr != null) {
         PropertyName name = intr.findNameForDeserialization(param);
         if (name != null) {
            return name;
         }

         String str = intr.findImplicitPropertyName(param);
         if (str != null && !str.isEmpty()) {
            return PropertyName.construct(str);
         }
      }

      return null;
   }

   protected PropertyName _findImplicitParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
      String str = intr.findImplicitPropertyName(param);
      return str != null && !str.isEmpty() ? PropertyName.construct(str) : null;
   }

   protected boolean _checkIfCreatorPropertyBased(AnnotationIntrospector intr, AnnotatedWithParams creator, BeanPropertyDefinition propDef, JsonCreator.Mode creatorMode) {
      if (creatorMode == JsonCreator.Mode.PROPERTIES) {
         return true;
      } else if (creatorMode == JsonCreator.Mode.DELEGATING) {
         return false;
      } else if ((propDef == null || !propDef.isExplicitlyNamed()) && intr.findInjectableValue(creator.getParameter(0)) == null) {
         if (propDef != null) {
            String implName = propDef.getName();
            if (implName != null && !implName.isEmpty() && propDef.couldSerialize()) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public JsonDeserializer<?> createArrayDeserializer(DeserializationContext ctxt, ArrayType type, BeanDescription beanDesc) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      JavaType elemType = type.getContentType();
      JsonDeserializer<Object> contentDeser = (JsonDeserializer)elemType.getValueHandler();
      TypeDeserializer elemTypeDeser = (TypeDeserializer)elemType.getTypeHandler();
      if (elemTypeDeser == null) {
         elemTypeDeser = this.findTypeDeserializer(config, elemType);
      }

      JsonDeserializer<?> deser = this._findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
      if (deser == null) {
         if (contentDeser == null) {
            Class<?> raw = elemType.getRawClass();
            if (elemType.isPrimitive()) {
               return PrimitiveArrayDeserializers.forType(raw);
            }

            if (raw == String.class) {
               return StringArrayDeserializer.instance;
            }
         }

         deser = new ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
      }

      BeanDeserializerModifier mod;
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); deser = mod.modifyArrayDeserializer(config, type, beanDesc, (JsonDeserializer)deser)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      return (JsonDeserializer)deser;
   }

   public JsonDeserializer<?> createCollectionDeserializer(DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc) throws JsonMappingException {
      JavaType contentType = type.getContentType();
      JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
      DeserializationConfig config = ctxt.getConfig();
      TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
      if (contentTypeDeser == null) {
         contentTypeDeser = this.findTypeDeserializer(config, contentType);
      }

      JsonDeserializer<?> deser = this._findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
      if (deser == null) {
         Class<?> collectionClass = type.getRawClass();
         if (contentDeser == null && EnumSet.class.isAssignableFrom(collectionClass)) {
            deser = new EnumSetDeserializer(contentType, (JsonDeserializer)null);
         }
      }

      if (deser == null) {
         if (type.isInterface() || type.isAbstract()) {
            CollectionType implType = this._mapAbstractCollectionType(type, config);
            if (implType == null) {
               if (type.getTypeHandler() == null) {
                  throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Collection type " + type);
               }

               deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
            } else {
               type = implType;
               beanDesc = config.introspectForCreation(implType);
            }
         }

         if (deser == null) {
            ValueInstantiator inst = this.findValueInstantiator(ctxt, beanDesc);
            if (!inst.canCreateUsingDefault() && type.hasRawClass(ArrayBlockingQueue.class)) {
               return new ArrayBlockingQueueDeserializer(type, contentDeser, contentTypeDeser, inst);
            }

            if (contentType.hasRawClass(String.class)) {
               deser = new StringCollectionDeserializer(type, contentDeser, inst);
            } else {
               deser = new CollectionDeserializer(type, contentDeser, contentTypeDeser, inst);
            }
         }
      }

      BeanDeserializerModifier mod;
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); deser = mod.modifyCollectionDeserializer(config, type, beanDesc, (JsonDeserializer)deser)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      return (JsonDeserializer)deser;
   }

   protected CollectionType _mapAbstractCollectionType(JavaType type, DeserializationConfig config) {
      Class<?> collectionClass = type.getRawClass();
      collectionClass = (Class)_collectionFallbacks.get(collectionClass.getName());
      return collectionClass == null ? null : (CollectionType)config.constructSpecializedType(type, collectionClass);
   }

   public JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationContext ctxt, CollectionLikeType type, BeanDescription beanDesc) throws JsonMappingException {
      JavaType contentType = type.getContentType();
      JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
      DeserializationConfig config = ctxt.getConfig();
      TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
      if (contentTypeDeser == null) {
         contentTypeDeser = this.findTypeDeserializer(config, contentType);
      }

      JsonDeserializer<?> deser = this._findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
      BeanDeserializerModifier mod;
      if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
         for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      return deser;
   }

   public JsonDeserializer<?> createMapDeserializer(DeserializationContext ctxt, MapType type, BeanDescription beanDesc) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      JavaType keyType = type.getKeyType();
      JavaType contentType = type.getContentType();
      JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
      KeyDeserializer keyDes = (KeyDeserializer)keyType.getValueHandler();
      TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
      if (contentTypeDeser == null) {
         contentTypeDeser = this.findTypeDeserializer(config, contentType);
      }

      JsonDeserializer<?> deser = this._findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
      if (deser == null) {
         Class<?> mapClass = type.getRawClass();
         ValueInstantiator inst;
         if (EnumMap.class.isAssignableFrom(mapClass)) {
            if (mapClass == EnumMap.class) {
               inst = null;
            } else {
               inst = this.findValueInstantiator(ctxt, beanDesc);
            }

            Class<?> kt = keyType.getRawClass();
            if (kt == null || !kt.isEnum()) {
               throw new IllegalArgumentException("Cannot construct EnumMap; generic (key) type not available");
            }

            deser = new EnumMapDeserializer(type, inst, (KeyDeserializer)null, contentDeser, contentTypeDeser, (NullValueProvider)null);
         }

         if (deser == null) {
            if (type.isInterface() || type.isAbstract()) {
               Class<? extends Map> fallback = (Class)_mapFallbacks.get(mapClass.getName());
               if (fallback != null) {
                  type = (MapType)config.constructSpecializedType(type, fallback);
                  beanDesc = config.introspectForCreation(type);
               } else {
                  if (type.getTypeHandler() == null) {
                     throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Map type " + type);
                  }

                  deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
               }
            }

            if (deser == null) {
               inst = this.findValueInstantiator(ctxt, beanDesc);
               MapDeserializer md = new MapDeserializer(type, inst, keyDes, contentDeser, contentTypeDeser);
               JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc.getClassInfo());
               Set<String> ignored = ignorals == null ? null : ignorals.findIgnoredForDeserialization();
               md.setIgnorableProperties(ignored);
               deser = md;
            }
         }
      }

      BeanDeserializerModifier mod;
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); deser = mod.modifyMapDeserializer(config, type, beanDesc, (JsonDeserializer)deser)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      return (JsonDeserializer)deser;
   }

   public JsonDeserializer<?> createMapLikeDeserializer(DeserializationContext ctxt, MapLikeType type, BeanDescription beanDesc) throws JsonMappingException {
      JavaType keyType = type.getKeyType();
      JavaType contentType = type.getContentType();
      DeserializationConfig config = ctxt.getConfig();
      JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
      KeyDeserializer keyDes = (KeyDeserializer)keyType.getValueHandler();
      TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
      if (contentTypeDeser == null) {
         contentTypeDeser = this.findTypeDeserializer(config, contentType);
      }

      JsonDeserializer<?> deser = this._findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
      BeanDeserializerModifier mod;
      if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
         for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      return deser;
   }

   public JsonDeserializer<?> createEnumDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      Class<?> enumClass = type.getRawClass();
      JsonDeserializer<?> deser = this._findCustomEnumDeserializer(enumClass, config, beanDesc);
      if (deser == null) {
         ValueInstantiator valueInstantiator = this._constructDefaultValueInstantiator(ctxt, beanDesc);
         SettableBeanProperty[] creatorProps = valueInstantiator == null ? null : valueInstantiator.getFromObjectArguments(ctxt.getConfig());
         Iterator i$ = beanDesc.getFactoryMethods().iterator();

         while(i$.hasNext()) {
            AnnotatedMethod factory = (AnnotatedMethod)i$.next();
            if (this._hasCreatorAnnotation(ctxt, factory)) {
               if (factory.getParameterCount() == 0) {
                  deser = EnumDeserializer.deserializerForNoArgsCreator(config, enumClass, factory);
                  break;
               }

               Class<?> returnType = factory.getRawReturnType();
               if (returnType.isAssignableFrom(enumClass)) {
                  deser = EnumDeserializer.deserializerForCreator(config, enumClass, factory, valueInstantiator, creatorProps);
                  break;
               }
            }
         }

         if (deser == null) {
            deser = new EnumDeserializer(this.constructEnumResolver(enumClass, config, beanDesc.findJsonValueAccessor()), config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS));
         }
      }

      BeanDeserializerModifier mod;
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); deser = mod.modifyEnumDeserializer(config, type, beanDesc, (JsonDeserializer)deser)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      return (JsonDeserializer)deser;
   }

   public JsonDeserializer<?> createTreeDeserializer(DeserializationConfig config, JavaType nodeType, BeanDescription beanDesc) throws JsonMappingException {
      Class<? extends JsonNode> nodeClass = nodeType.getRawClass();
      JsonDeserializer<?> custom = this._findCustomTreeNodeDeserializer(nodeClass, config, beanDesc);
      return custom != null ? custom : JsonNodeDeserializer.getDeserializer(nodeClass);
   }

   public JsonDeserializer<?> createReferenceDeserializer(DeserializationContext ctxt, ReferenceType type, BeanDescription beanDesc) throws JsonMappingException {
      JavaType contentType = type.getContentType();
      JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
      DeserializationConfig config = ctxt.getConfig();
      TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
      if (contentTypeDeser == null) {
         contentTypeDeser = this.findTypeDeserializer(config, contentType);
      }

      JsonDeserializer<?> deser = this._findCustomReferenceDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
      if (deser == null && type.isTypeOrSubTypeOf(AtomicReference.class)) {
         Class<?> rawType = type.getRawClass();
         ValueInstantiator inst;
         if (rawType == AtomicReference.class) {
            inst = null;
         } else {
            inst = this.findValueInstantiator(ctxt, beanDesc);
         }

         return new AtomicReferenceDeserializer(type, inst, contentTypeDeser, contentDeser);
      } else {
         BeanDeserializerModifier mod;
         if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); deser = mod.modifyReferenceDeserializer(config, type, beanDesc, deser)) {
               mod = (BeanDeserializerModifier)i$.next();
            }
         }

         return deser;
      }
   }

   public TypeDeserializer findTypeDeserializer(DeserializationConfig config, JavaType baseType) throws JsonMappingException {
      BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
      AnnotatedClass ac = bean.getClassInfo();
      AnnotationIntrospector ai = config.getAnnotationIntrospector();
      TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
      Collection<NamedType> subtypes = null;
      if (b == null) {
         b = config.getDefaultTyper(baseType);
         if (b == null) {
            return null;
         }
      } else {
         subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, ac);
      }

      if (b.getDefaultImpl() == null && baseType.isAbstract()) {
         JavaType defaultType = this.mapAbstractType(config, baseType);
         if (defaultType != null && !defaultType.hasRawClass(baseType.getRawClass())) {
            b = b.defaultImpl(defaultType.getRawClass());
         }
      }

      return b.buildTypeDeserializer(config, baseType, subtypes);
   }

   protected JsonDeserializer<?> findOptionalStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      return OptionalHandlerFactory.instance.findDeserializer(type, ctxt.getConfig(), beanDesc);
   }

   public KeyDeserializer createKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      KeyDeserializer deser = null;
      if (this._factoryConfig.hasKeyDeserializers()) {
         BeanDescription beanDesc = config.introspectClassAnnotations(type.getRawClass());
         Iterator i$ = this._factoryConfig.keyDeserializers().iterator();

         while(i$.hasNext()) {
            KeyDeserializers d = (KeyDeserializers)i$.next();
            deser = d.findKeyDeserializer(type, config, beanDesc);
            if (deser != null) {
               break;
            }
         }
      }

      if (deser == null) {
         if (type.isEnumType()) {
            deser = this._createEnumKeyDeserializer(ctxt, type);
         } else {
            deser = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
         }
      }

      BeanDeserializerModifier mod;
      if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
         for(Iterator i$ = this._factoryConfig.deserializerModifiers().iterator(); i$.hasNext(); deser = mod.modifyKeyDeserializer(config, type, deser)) {
            mod = (BeanDeserializerModifier)i$.next();
         }
      }

      return deser;
   }

   private KeyDeserializer _createEnumKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      Class<?> enumClass = type.getRawClass();
      BeanDescription beanDesc = config.introspect(type);
      KeyDeserializer des = this.findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
      if (des != null) {
         return des;
      } else {
         JsonDeserializer<?> custom = this._findCustomEnumDeserializer(enumClass, config, beanDesc);
         if (custom != null) {
            return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, custom);
         } else {
            JsonDeserializer<?> valueDesForKey = this.findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
            if (valueDesForKey != null) {
               return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, valueDesForKey);
            } else {
               EnumResolver enumRes = this.constructEnumResolver(enumClass, config, beanDesc.findJsonValueAccessor());
               Iterator i$ = beanDesc.getFactoryMethods().iterator();

               AnnotatedMethod factory;
               do {
                  if (!i$.hasNext()) {
                     return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes);
                  }

                  factory = (AnnotatedMethod)i$.next();
               } while(!this._hasCreatorAnnotation(ctxt, factory));

               int argCount = factory.getParameterCount();
               if (argCount == 1) {
                  Class<?> returnType = factory.getRawReturnType();
                  if (returnType.isAssignableFrom(enumClass)) {
                     if (factory.getRawParameterType(0) != String.class) {
                        throw new IllegalArgumentException("Parameter #0 type for factory method (" + factory + ") not suitable, must be java.lang.String");
                     }

                     if (config.canOverrideAccessModifiers()) {
                        ClassUtil.checkAndFixAccess(factory.getMember(), ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                     }

                     return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory);
                  }
               }

               throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
            }
         }
      }
   }

   public TypeDeserializer findPropertyTypeDeserializer(DeserializationConfig config, JavaType baseType, AnnotatedMember annotated) throws JsonMappingException {
      AnnotationIntrospector ai = config.getAnnotationIntrospector();
      TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, annotated, baseType);
      if (b == null) {
         return this.findTypeDeserializer(config, baseType);
      } else {
         Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, annotated, baseType);
         return b.buildTypeDeserializer(config, baseType, subtypes);
      }
   }

   public TypeDeserializer findPropertyContentTypeDeserializer(DeserializationConfig config, JavaType containerType, AnnotatedMember propertyEntity) throws JsonMappingException {
      AnnotationIntrospector ai = config.getAnnotationIntrospector();
      TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, propertyEntity, containerType);
      JavaType contentType = containerType.getContentType();
      if (b == null) {
         return this.findTypeDeserializer(config, contentType);
      } else {
         Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, propertyEntity, contentType);
         return b.buildTypeDeserializer(config, contentType, subtypes);
      }
   }

   public JsonDeserializer<?> findDefaultDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      Class<?> rawType = type.getRawClass();
      JavaType vt;
      JavaType elemType;
      if (rawType == CLASS_OBJECT) {
         DeserializationConfig config = ctxt.getConfig();
         if (this._factoryConfig.hasAbstractTypeResolvers()) {
            vt = this._findRemappedType(config, List.class);
            elemType = this._findRemappedType(config, Map.class);
         } else {
            elemType = null;
            vt = null;
         }

         return new UntypedObjectDeserializer(vt, elemType);
      } else if (rawType != CLASS_STRING && rawType != CLASS_CHAR_BUFFER) {
         if (rawType == CLASS_ITERABLE) {
            TypeFactory tf = ctxt.getTypeFactory();
            JavaType[] tps = tf.findTypeParameters(type, CLASS_ITERABLE);
            elemType = tps != null && tps.length == 1 ? tps[0] : TypeFactory.unknownType();
            CollectionType ct = tf.constructCollectionType(Collection.class, elemType);
            return this.createCollectionDeserializer(ctxt, ct, beanDesc);
         } else if (rawType == CLASS_MAP_ENTRY) {
            JavaType kt = type.containedTypeOrUnknown(0);
            vt = type.containedTypeOrUnknown(1);
            TypeDeserializer vts = (TypeDeserializer)vt.getTypeHandler();
            if (vts == null) {
               vts = this.findTypeDeserializer(ctxt.getConfig(), vt);
            }

            JsonDeserializer<Object> valueDeser = (JsonDeserializer)vt.getValueHandler();
            KeyDeserializer keyDes = (KeyDeserializer)kt.getValueHandler();
            return new MapEntryDeserializer(type, keyDes, valueDeser, vts);
         } else {
            String clsName = rawType.getName();
            JsonDeserializer deser;
            if (rawType.isPrimitive() || clsName.startsWith("java.")) {
               deser = NumberDeserializers.find(rawType, clsName);
               if (deser == null) {
                  deser = DateDeserializers.find(rawType, clsName);
               }

               if (deser != null) {
                  return deser;
               }
            }

            if (rawType == TokenBuffer.class) {
               return new TokenBufferDeserializer();
            } else {
               deser = this.findOptionalStdDeserializer(ctxt, type, beanDesc);
               return deser != null ? deser : JdkDeserializers.find(rawType, clsName);
            }
         }
      } else {
         return StringDeserializer.instance;
      }
   }

   protected JavaType _findRemappedType(DeserializationConfig config, Class<?> rawType) throws JsonMappingException {
      JavaType type = this.mapAbstractType(config, config.constructType(rawType));
      return type != null && !type.hasRawClass(rawType) ? type : null;
   }

   protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(Class<? extends JsonNode> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
      Iterator i$ = this._factoryConfig.deserializers().iterator();

      JsonDeserializer deser;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         Deserializers d = (Deserializers)i$.next();
         deser = d.findTreeNodeDeserializer(type, config, beanDesc);
      } while(deser == null);

      return deser;
   }

   protected JsonDeserializer<?> _findCustomReferenceDeserializer(ReferenceType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
      Iterator i$ = this._factoryConfig.deserializers().iterator();

      JsonDeserializer deser;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         Deserializers d = (Deserializers)i$.next();
         deser = d.findReferenceDeserializer(type, config, beanDesc, contentTypeDeserializer, contentDeserializer);
      } while(deser == null);

      return deser;
   }

   protected JsonDeserializer<Object> _findCustomBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
      Iterator i$ = this._factoryConfig.deserializers().iterator();

      JsonDeserializer deser;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         Deserializers d = (Deserializers)i$.next();
         deser = d.findBeanDeserializer(type, config, beanDesc);
      } while(deser == null);

      return deser;
   }

   protected JsonDeserializer<?> _findCustomArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      Iterator i$ = this._factoryConfig.deserializers().iterator();

      JsonDeserializer deser;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         Deserializers d = (Deserializers)i$.next();
         deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
      } while(deser == null);

      return deser;
   }

   protected JsonDeserializer<?> _findCustomCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      Iterator i$ = this._factoryConfig.deserializers().iterator();

      JsonDeserializer deser;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         Deserializers d = (Deserializers)i$.next();
         deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
      } while(deser == null);

      return deser;
   }

   protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      Iterator i$ = this._factoryConfig.deserializers().iterator();

      JsonDeserializer deser;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         Deserializers d = (Deserializers)i$.next();
         deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
      } while(deser == null);

      return deser;
   }

   protected JsonDeserializer<?> _findCustomEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
      Iterator i$ = this._factoryConfig.deserializers().iterator();

      JsonDeserializer deser;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         Deserializers d = (Deserializers)i$.next();
         deser = d.findEnumDeserializer(type, config, beanDesc);
      } while(deser == null);

      return deser;
   }

   protected JsonDeserializer<?> _findCustomMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      Iterator i$ = this._factoryConfig.deserializers().iterator();

      JsonDeserializer deser;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         Deserializers d = (Deserializers)i$.next();
         deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
      } while(deser == null);

      return deser;
   }

   protected JsonDeserializer<?> _findCustomMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      Iterator i$ = this._factoryConfig.deserializers().iterator();

      JsonDeserializer deser;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         Deserializers d = (Deserializers)i$.next();
         deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
      } while(deser == null);

      return deser;
   }

   protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (intr != null) {
         Object deserDef = intr.findDeserializer(ann);
         if (deserDef != null) {
            return ctxt.deserializerInstance(ann, deserDef);
         }
      }

      return null;
   }

   protected KeyDeserializer findKeyDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (intr != null) {
         Object deserDef = intr.findKeyDeserializer(ann);
         if (deserDef != null) {
            return ctxt.keyDeserializerInstance(ann, deserDef);
         }
      }

      return null;
   }

   protected JsonDeserializer<Object> findContentDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (intr != null) {
         Object deserDef = intr.findContentDeserializer(ann);
         if (deserDef != null) {
            return ctxt.deserializerInstance(ann, deserDef);
         }
      }

      return null;
   }

   protected JavaType resolveMemberAndTypeAnnotations(DeserializationContext ctxt, AnnotatedMember member, JavaType type) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (intr == null) {
         return (JavaType)type;
      } else {
         if (((JavaType)type).isMapLikeType()) {
            JavaType keyType = ((JavaType)type).getKeyType();
            if (keyType != null) {
               Object kdDef = intr.findKeyDeserializer(member);
               KeyDeserializer kd = ctxt.keyDeserializerInstance(member, kdDef);
               if (kd != null) {
                  type = ((MapLikeType)type).withKeyValueHandler(kd);
                  keyType = ((JavaType)type).getKeyType();
               }
            }
         }

         if (((JavaType)type).hasContentType()) {
            Object cdDef = intr.findContentDeserializer(member);
            JsonDeserializer<?> cd = ctxt.deserializerInstance(member, cdDef);
            if (cd != null) {
               type = ((JavaType)type).withContentValueHandler(cd);
            }

            TypeDeserializer contentTypeDeser = this.findPropertyContentTypeDeserializer(ctxt.getConfig(), (JavaType)type, member);
            if (contentTypeDeser != null) {
               type = ((JavaType)type).withContentTypeHandler(contentTypeDeser);
            }
         }

         TypeDeserializer valueTypeDeser = this.findPropertyTypeDeserializer(ctxt.getConfig(), (JavaType)type, member);
         if (valueTypeDeser != null) {
            type = ((JavaType)type).withTypeHandler(valueTypeDeser);
         }

         JavaType type = intr.refineDeserializationType(ctxt.getConfig(), member, (JavaType)type);
         return type;
      }
   }

   protected EnumResolver constructEnumResolver(Class<?> enumClass, DeserializationConfig config, AnnotatedMember jsonValueAccessor) {
      if (jsonValueAccessor != null) {
         if (config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(jsonValueAccessor.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
         }

         return EnumResolver.constructUnsafeUsingMethod(enumClass, jsonValueAccessor, config.getAnnotationIntrospector());
      } else {
         return EnumResolver.constructUnsafe(enumClass, config.getAnnotationIntrospector());
      }
   }

   protected boolean _hasCreatorAnnotation(DeserializationContext ctxt, Annotated ann) {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (intr == null) {
         return false;
      } else {
         JsonCreator.Mode mode = intr.findCreatorAnnotation(ctxt.getConfig(), ann);
         return mode != null && mode != JsonCreator.Mode.DISABLED;
      }
   }

   /** @deprecated */
   @Deprecated
   protected JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      return intr == null ? type : intr.refineDeserializationType(ctxt.getConfig(), a, type);
   }

   /** @deprecated */
   @Deprecated
   protected JavaType resolveType(DeserializationContext ctxt, BeanDescription beanDesc, JavaType type, AnnotatedMember member) throws JsonMappingException {
      return this.resolveMemberAndTypeAnnotations(ctxt, member, type);
   }

   /** @deprecated */
   @Deprecated
   protected AnnotatedMethod _findJsonValueFor(DeserializationConfig config, JavaType enumType) {
      if (enumType == null) {
         return null;
      } else {
         BeanDescription beanDesc = config.introspect(enumType);
         return beanDesc.findJsonValueMethod();
      }
   }

   static {
      _mapFallbacks.put(Map.class.getName(), LinkedHashMap.class);
      _mapFallbacks.put(ConcurrentMap.class.getName(), ConcurrentHashMap.class);
      _mapFallbacks.put(SortedMap.class.getName(), TreeMap.class);
      _mapFallbacks.put(NavigableMap.class.getName(), TreeMap.class);
      _mapFallbacks.put(ConcurrentNavigableMap.class.getName(), ConcurrentSkipListMap.class);
      _collectionFallbacks = new HashMap();
      _collectionFallbacks.put(Collection.class.getName(), ArrayList.class);
      _collectionFallbacks.put(List.class.getName(), ArrayList.class);
      _collectionFallbacks.put(Set.class.getName(), HashSet.class);
      _collectionFallbacks.put(SortedSet.class.getName(), TreeSet.class);
      _collectionFallbacks.put(Queue.class.getName(), LinkedList.class);
      _collectionFallbacks.put("java.util.Deque", LinkedList.class);
      _collectionFallbacks.put("java.util.NavigableSet", TreeSet.class);
   }
}
