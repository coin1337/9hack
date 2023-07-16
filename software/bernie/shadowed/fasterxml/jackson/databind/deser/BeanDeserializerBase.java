package software.bernie.shadowed.fasterxml.jackson.databind.deser;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonIgnoreProperties;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonTypeInfo;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdGenerator;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdGenerators;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdResolver;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyMetadata;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.InnerClassProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ManagedReferenceProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.MergingSettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ObjectIdReferenceProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.SetterlessProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.UnwrappedPropertyHandler;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ValueInjector;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.StdDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.exc.IgnoredPropertyException;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ClassKey;
import software.bernie.shadowed.fasterxml.jackson.databind.util.AccessPattern;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Converter;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.TokenBuffer;

public abstract class BeanDeserializerBase extends StdDeserializer<Object> implements ContextualDeserializer, ResolvableDeserializer, ValueInstantiator.Gettable, Serializable {
   private static final long serialVersionUID = 1L;
   protected static final PropertyName TEMP_PROPERTY_NAME = new PropertyName("#temporary-name");
   protected final JavaType _beanType;
   protected final JsonFormat.Shape _serializationShape;
   protected final ValueInstantiator _valueInstantiator;
   protected JsonDeserializer<Object> _delegateDeserializer;
   protected JsonDeserializer<Object> _arrayDelegateDeserializer;
   protected PropertyBasedCreator _propertyBasedCreator;
   protected boolean _nonStandardCreation;
   protected boolean _vanillaProcessing;
   protected final BeanPropertyMap _beanProperties;
   protected final ValueInjector[] _injectables;
   protected SettableAnyProperty _anySetter;
   protected final Set<String> _ignorableProps;
   protected final boolean _ignoreAllUnknown;
   protected final boolean _needViewProcesing;
   protected final Map<String, SettableBeanProperty> _backRefs;
   protected transient HashMap<ClassKey, JsonDeserializer<Object>> _subDeserializers;
   protected UnwrappedPropertyHandler _unwrappedPropertyHandler;
   protected ExternalTypeHandler _externalTypeIdHandler;
   protected final ObjectIdReader _objectIdReader;

   protected BeanDeserializerBase(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
      super(beanDesc.getType());
      this._beanType = beanDesc.getType();
      this._valueInstantiator = builder.getValueInstantiator();
      this._beanProperties = properties;
      this._backRefs = backRefs;
      this._ignorableProps = ignorableProps;
      this._ignoreAllUnknown = ignoreAllUnknown;
      this._anySetter = builder.getAnySetter();
      List<ValueInjector> injectables = builder.getInjectables();
      this._injectables = injectables != null && !injectables.isEmpty() ? (ValueInjector[])injectables.toArray(new ValueInjector[injectables.size()]) : null;
      this._objectIdReader = builder.getObjectIdReader();
      this._nonStandardCreation = this._unwrappedPropertyHandler != null || this._valueInstantiator.canCreateUsingDelegate() || this._valueInstantiator.canCreateUsingArrayDelegate() || this._valueInstantiator.canCreateFromObjectWith() || !this._valueInstantiator.canCreateUsingDefault();
      JsonFormat.Value format = beanDesc.findExpectedFormat((JsonFormat.Value)null);
      this._serializationShape = format == null ? null : format.getShape();
      this._needViewProcesing = hasViews;
      this._vanillaProcessing = !this._nonStandardCreation && this._injectables == null && !this._needViewProcesing && this._objectIdReader == null;
   }

   protected BeanDeserializerBase(BeanDeserializerBase src) {
      this(src, src._ignoreAllUnknown);
   }

   protected BeanDeserializerBase(BeanDeserializerBase src, boolean ignoreAllUnknown) {
      super(src._beanType);
      this._beanType = src._beanType;
      this._valueInstantiator = src._valueInstantiator;
      this._delegateDeserializer = src._delegateDeserializer;
      this._propertyBasedCreator = src._propertyBasedCreator;
      this._beanProperties = src._beanProperties;
      this._backRefs = src._backRefs;
      this._ignorableProps = src._ignorableProps;
      this._ignoreAllUnknown = ignoreAllUnknown;
      this._anySetter = src._anySetter;
      this._injectables = src._injectables;
      this._objectIdReader = src._objectIdReader;
      this._nonStandardCreation = src._nonStandardCreation;
      this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
      this._needViewProcesing = src._needViewProcesing;
      this._serializationShape = src._serializationShape;
      this._vanillaProcessing = src._vanillaProcessing;
   }

   protected BeanDeserializerBase(BeanDeserializerBase src, NameTransformer unwrapper) {
      super(src._beanType);
      this._beanType = src._beanType;
      this._valueInstantiator = src._valueInstantiator;
      this._delegateDeserializer = src._delegateDeserializer;
      this._propertyBasedCreator = src._propertyBasedCreator;
      this._backRefs = src._backRefs;
      this._ignorableProps = src._ignorableProps;
      this._ignoreAllUnknown = unwrapper != null || src._ignoreAllUnknown;
      this._anySetter = src._anySetter;
      this._injectables = src._injectables;
      this._objectIdReader = src._objectIdReader;
      this._nonStandardCreation = src._nonStandardCreation;
      UnwrappedPropertyHandler uph = src._unwrappedPropertyHandler;
      if (unwrapper != null) {
         if (uph != null) {
            uph = uph.renameAll(unwrapper);
         }

         this._beanProperties = src._beanProperties.renameAll(unwrapper);
      } else {
         this._beanProperties = src._beanProperties;
      }

      this._unwrappedPropertyHandler = uph;
      this._needViewProcesing = src._needViewProcesing;
      this._serializationShape = src._serializationShape;
      this._vanillaProcessing = false;
   }

   public BeanDeserializerBase(BeanDeserializerBase src, ObjectIdReader oir) {
      super(src._beanType);
      this._beanType = src._beanType;
      this._valueInstantiator = src._valueInstantiator;
      this._delegateDeserializer = src._delegateDeserializer;
      this._propertyBasedCreator = src._propertyBasedCreator;
      this._backRefs = src._backRefs;
      this._ignorableProps = src._ignorableProps;
      this._ignoreAllUnknown = src._ignoreAllUnknown;
      this._anySetter = src._anySetter;
      this._injectables = src._injectables;
      this._nonStandardCreation = src._nonStandardCreation;
      this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
      this._needViewProcesing = src._needViewProcesing;
      this._serializationShape = src._serializationShape;
      this._objectIdReader = oir;
      if (oir == null) {
         this._beanProperties = src._beanProperties;
         this._vanillaProcessing = src._vanillaProcessing;
      } else {
         ObjectIdValueProperty idProp = new ObjectIdValueProperty(oir, PropertyMetadata.STD_REQUIRED);
         this._beanProperties = src._beanProperties.withProperty(idProp);
         this._vanillaProcessing = false;
      }

   }

   public BeanDeserializerBase(BeanDeserializerBase src, Set<String> ignorableProps) {
      super(src._beanType);
      this._beanType = src._beanType;
      this._valueInstantiator = src._valueInstantiator;
      this._delegateDeserializer = src._delegateDeserializer;
      this._propertyBasedCreator = src._propertyBasedCreator;
      this._backRefs = src._backRefs;
      this._ignorableProps = ignorableProps;
      this._ignoreAllUnknown = src._ignoreAllUnknown;
      this._anySetter = src._anySetter;
      this._injectables = src._injectables;
      this._nonStandardCreation = src._nonStandardCreation;
      this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
      this._needViewProcesing = src._needViewProcesing;
      this._serializationShape = src._serializationShape;
      this._vanillaProcessing = src._vanillaProcessing;
      this._objectIdReader = src._objectIdReader;
      this._beanProperties = src._beanProperties.withoutProperties(ignorableProps);
   }

   protected BeanDeserializerBase(BeanDeserializerBase src, BeanPropertyMap beanProps) {
      super(src._beanType);
      this._beanType = src._beanType;
      this._valueInstantiator = src._valueInstantiator;
      this._delegateDeserializer = src._delegateDeserializer;
      this._propertyBasedCreator = src._propertyBasedCreator;
      this._beanProperties = beanProps;
      this._backRefs = src._backRefs;
      this._ignorableProps = src._ignorableProps;
      this._ignoreAllUnknown = src._ignoreAllUnknown;
      this._anySetter = src._anySetter;
      this._injectables = src._injectables;
      this._objectIdReader = src._objectIdReader;
      this._nonStandardCreation = src._nonStandardCreation;
      this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
      this._needViewProcesing = src._needViewProcesing;
      this._serializationShape = src._serializationShape;
      this._vanillaProcessing = src._vanillaProcessing;
   }

   public abstract JsonDeserializer<Object> unwrappingDeserializer(NameTransformer var1);

   public abstract BeanDeserializerBase withObjectIdReader(ObjectIdReader var1);

   public abstract BeanDeserializerBase withIgnorableProperties(Set<String> var1);

   public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
      throw new UnsupportedOperationException("Class " + this.getClass().getName() + " does not override `withBeanProperties()`, needs to");
   }

   protected abstract BeanDeserializerBase asArrayDeserializer();

   public void resolve(DeserializationContext ctxt) throws JsonMappingException {
      ExternalTypeHandler.Builder extTypes = null;
      SettableBeanProperty[] creatorProps;
      if (this._valueInstantiator.canCreateFromObjectWith()) {
         creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
      } else {
         creatorProps = null;
      }

      UnwrappedPropertyHandler unwrapped = null;
      Iterator i$ = this._beanProperties.iterator();

      SettableBeanProperty origProp;
      while(i$.hasNext()) {
         origProp = (SettableBeanProperty)i$.next();
         if (!origProp.hasValueDeserializer()) {
            JsonDeserializer<?> deser = this.findConvertingDeserializer(ctxt, origProp);
            if (deser == null) {
               deser = ctxt.findNonContextualValueDeserializer(origProp.getType());
            }

            SettableBeanProperty newProp = origProp.withValueDeserializer(deser);
            this._replaceProperty(this._beanProperties, creatorProps, origProp, newProp);
         }
      }

      i$ = this._beanProperties.iterator();

      while(i$.hasNext()) {
         origProp = (SettableBeanProperty)i$.next();
         JsonDeserializer<?> deser = origProp.getValueDeserializer();
         deser = ctxt.handlePrimaryContextualization(deser, origProp, origProp.getType());
         SettableBeanProperty prop = origProp.withValueDeserializer(deser);
         prop = this._resolveManagedReferenceProperty(ctxt, prop);
         if (!(prop instanceof ManagedReferenceProperty)) {
            prop = this._resolvedObjectIdProperty(ctxt, prop);
         }

         NameTransformer xform = this._findPropertyUnwrapper(ctxt, prop);
         if (xform != null) {
            JsonDeserializer<Object> orig = prop.getValueDeserializer();
            JsonDeserializer<Object> unwrapping = orig.unwrappingDeserializer(xform);
            if (unwrapping != orig && unwrapping != null) {
               prop = prop.withValueDeserializer(unwrapping);
               if (unwrapped == null) {
                  unwrapped = new UnwrappedPropertyHandler();
               }

               unwrapped.addProperty(prop);
               this._beanProperties.remove(prop);
               continue;
            }
         }

         PropertyMetadata md = prop.getMetadata();
         prop = this._resolveMergeAndNullSettings(ctxt, prop, md);
         prop = this._resolveInnerClassValuedProperty(ctxt, prop);
         if (prop != origProp) {
            this._replaceProperty(this._beanProperties, creatorProps, origProp, prop);
         }

         if (prop.hasValueTypeDeserializer()) {
            TypeDeserializer typeDeser = prop.getValueTypeDeserializer();
            if (typeDeser.getTypeInclusion() == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
               if (extTypes == null) {
                  extTypes = ExternalTypeHandler.builder(this._beanType);
               }

               extTypes.addExternal(prop, typeDeser);
               this._beanProperties.remove(prop);
            }
         }
      }

      if (this._anySetter != null && !this._anySetter.hasValueDeserializer()) {
         this._anySetter = this._anySetter.withValueDeserializer(this.findDeserializer(ctxt, this._anySetter.getType(), this._anySetter.getProperty()));
      }

      JavaType delegateType;
      if (this._valueInstantiator.canCreateUsingDelegate()) {
         delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
         if (delegateType == null) {
            ctxt.reportBadDefinition(this._beanType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", this._beanType, this._valueInstantiator.getClass().getName()));
         }

         this._delegateDeserializer = this._findDelegateDeserializer(ctxt, delegateType, this._valueInstantiator.getDelegateCreator());
      }

      if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
         delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
         if (delegateType == null) {
            ctxt.reportBadDefinition(this._beanType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", this._beanType, this._valueInstantiator.getClass().getName()));
         }

         this._arrayDelegateDeserializer = this._findDelegateDeserializer(ctxt, delegateType, this._valueInstantiator.getArrayDelegateCreator());
      }

      if (creatorProps != null) {
         this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps, this._beanProperties);
      }

      if (extTypes != null) {
         this._externalTypeIdHandler = extTypes.build(this._beanProperties);
         this._nonStandardCreation = true;
      }

      this._unwrappedPropertyHandler = unwrapped;
      if (unwrapped != null) {
         this._nonStandardCreation = true;
      }

      this._vanillaProcessing = this._vanillaProcessing && !this._nonStandardCreation;
   }

   protected void _replaceProperty(BeanPropertyMap props, SettableBeanProperty[] creatorProps, SettableBeanProperty origProp, SettableBeanProperty newProp) {
      props.replace(newProp);
      if (creatorProps != null) {
         int i = 0;

         for(int len = creatorProps.length; i < len; ++i) {
            if (creatorProps[i] == origProp) {
               creatorProps[i] = newProp;
               return;
            }
         }
      }

   }

   private JsonDeserializer<Object> _findDelegateDeserializer(DeserializationContext ctxt, JavaType delegateType, AnnotatedWithParams delegateCreator) throws JsonMappingException {
      BeanProperty.Std property = new BeanProperty.Std(TEMP_PROPERTY_NAME, delegateType, (PropertyName)null, delegateCreator, PropertyMetadata.STD_OPTIONAL);
      TypeDeserializer td = (TypeDeserializer)delegateType.getTypeHandler();
      if (td == null) {
         td = ctxt.getConfig().findTypeDeserializer(delegateType);
      }

      JsonDeserializer<Object> dd = this.findDeserializer(ctxt, delegateType, property);
      if (td != null) {
         td = td.forProperty(property);
         return new TypeWrappedDeserializer(td, dd);
      } else {
         return dd;
      }
   }

   protected JsonDeserializer<Object> findConvertingDeserializer(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (intr != null) {
         Object convDef = intr.findDeserializationConverter(prop.getMember());
         if (convDef != null) {
            Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
            JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
            JsonDeserializer<?> deser = ctxt.findNonContextualValueDeserializer(delegateType);
            return new StdDelegatingDeserializer(conv, delegateType, deser);
         }
      }

      return null;
   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      ObjectIdReader oir = this._objectIdReader;
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      AnnotatedMember accessor = _neitherNull(property, intr) ? property.getMember() : null;
      if (accessor != null) {
         ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
         if (objectIdInfo != null) {
            objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
            Class<?> implClass = objectIdInfo.getGeneratorType();
            ObjectIdResolver resolver = ctxt.objectIdResolverInstance(accessor, objectIdInfo);
            JavaType idType;
            SettableBeanProperty idProp;
            Object idGen;
            if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
               PropertyName propName = objectIdInfo.getPropertyName();
               idProp = this.findProperty(propName);
               if (idProp == null) {
                  ctxt.reportBadDefinition(this._beanType, String.format("Invalid Object Id definition for %s: cannot find property with name '%s'", this.handledType().getName(), propName));
               }

               idType = idProp.getType();
               idGen = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
            } else {
               JavaType type = ctxt.constructType(implClass);
               idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
               idProp = null;
               idGen = ctxt.objectIdGeneratorInstance(accessor, objectIdInfo);
            }

            JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
            oir = ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), (ObjectIdGenerator)idGen, deser, idProp, resolver);
         }
      }

      BeanDeserializerBase contextual = this;
      if (oir != null && oir != this._objectIdReader) {
         contextual = this.withObjectIdReader(oir);
      }

      if (accessor != null) {
         JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(accessor);
         if (ignorals != null) {
            Set<String> ignored = ignorals.findIgnoredForDeserialization();
            if (!((Set)ignored).isEmpty()) {
               Set<String> prev = contextual._ignorableProps;
               if (prev != null && !prev.isEmpty()) {
                  ignored = new HashSet((Collection)ignored);
                  ((Set)ignored).addAll(prev);
               }

               contextual = contextual.withIgnorableProperties((Set)ignored);
            }
         }
      }

      JsonFormat.Value format = this.findFormatOverrides(ctxt, property, this.handledType());
      JsonFormat.Shape shape = null;
      if (format != null) {
         if (format.hasShape()) {
            shape = format.getShape();
         }

         Boolean B = format.getFeature(JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
         if (B != null) {
            BeanPropertyMap propsOrig = this._beanProperties;
            BeanPropertyMap props = propsOrig.withCaseInsensitivity(B);
            if (props != propsOrig) {
               contextual = contextual.withBeanProperties(props);
            }
         }
      }

      if (shape == null) {
         shape = this._serializationShape;
      }

      if (shape == JsonFormat.Shape.ARRAY) {
         contextual = contextual.asArrayDeserializer();
      }

      return contextual;
   }

   protected SettableBeanProperty _resolveManagedReferenceProperty(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
      String refName = prop.getManagedReferenceName();
      if (refName == null) {
         return prop;
      } else {
         JsonDeserializer<?> valueDeser = prop.getValueDeserializer();
         SettableBeanProperty backProp = valueDeser.findBackReference(refName);
         if (backProp == null) {
            ctxt.reportBadDefinition(this._beanType, String.format("Cannot handle managed/back reference '%s': no back reference property found from type %s", refName, prop.getType()));
         }

         JavaType referredType = this._beanType;
         JavaType backRefType = backProp.getType();
         boolean isContainer = prop.getType().isContainerType();
         if (!backRefType.getRawClass().isAssignableFrom(referredType.getRawClass())) {
            ctxt.reportBadDefinition(this._beanType, String.format("Cannot handle managed/back reference '%s': back reference type (%s) not compatible with managed type (%s)", refName, backRefType.getRawClass().getName(), referredType.getRawClass().getName()));
         }

         return new ManagedReferenceProperty(prop, refName, backProp, isContainer);
      }
   }

   protected SettableBeanProperty _resolvedObjectIdProperty(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
      ObjectIdInfo objectIdInfo = prop.getObjectIdInfo();
      JsonDeserializer<Object> valueDeser = prop.getValueDeserializer();
      ObjectIdReader objectIdReader = valueDeser == null ? null : valueDeser.getObjectIdReader();
      return (SettableBeanProperty)(objectIdInfo == null && objectIdReader == null ? prop : new ObjectIdReferenceProperty(prop, objectIdInfo));
   }

   protected NameTransformer _findPropertyUnwrapper(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
      AnnotatedMember am = prop.getMember();
      if (am != null) {
         NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(am);
         if (unwrapper != null) {
            if (prop instanceof CreatorProperty) {
               ctxt.reportBadDefinition(this.getValueType(), String.format("Cannot define Creator property \"%s\" as `@JsonUnwrapped`: combination not yet supported", prop.getName()));
            }

            return unwrapper;
         }
      }

      return null;
   }

   protected SettableBeanProperty _resolveInnerClassValuedProperty(DeserializationContext ctxt, SettableBeanProperty prop) {
      JsonDeserializer<Object> deser = prop.getValueDeserializer();
      if (deser instanceof BeanDeserializerBase) {
         BeanDeserializerBase bd = (BeanDeserializerBase)deser;
         ValueInstantiator vi = bd.getValueInstantiator();
         if (!vi.canCreateUsingDefault()) {
            Class<?> valueClass = prop.getType().getRawClass();
            Class<?> enclosing = ClassUtil.getOuterClass(valueClass);
            if (enclosing != null && enclosing == this._beanType.getRawClass()) {
               Constructor[] arr$ = valueClass.getConstructors();
               int len$ = arr$.length;

               for(int i$ = 0; i$ < len$; ++i$) {
                  Constructor<?> ctor = arr$[i$];
                  Class<?>[] paramTypes = ctor.getParameterTypes();
                  if (paramTypes.length == 1 && enclosing.equals(paramTypes[0])) {
                     if (ctxt.canOverrideAccessModifiers()) {
                        ClassUtil.checkAndFixAccess(ctor, ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                     }

                     return new InnerClassProperty(prop, ctor);
                  }
               }
            }
         }
      }

      return prop;
   }

   protected SettableBeanProperty _resolveMergeAndNullSettings(DeserializationContext ctxt, SettableBeanProperty prop, PropertyMetadata propMetadata) throws JsonMappingException {
      PropertyMetadata.MergeInfo merge = propMetadata.getMergeInfo();
      if (merge != null) {
         JsonDeserializer<?> valueDeser = ((SettableBeanProperty)prop).getValueDeserializer();
         Boolean mayMerge = valueDeser.supportsUpdate(ctxt.getConfig());
         if (mayMerge == null) {
            if (merge.fromDefaults) {
               return (SettableBeanProperty)prop;
            }
         } else if (!mayMerge) {
            if (!merge.fromDefaults) {
               ctxt.reportBadMerge(valueDeser);
            }

            return (SettableBeanProperty)prop;
         }

         AnnotatedMember accessor = merge.getter;
         accessor.fixAccess(ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
         if (!(prop instanceof SetterlessProperty)) {
            prop = MergingSettableBeanProperty.construct((SettableBeanProperty)prop, accessor);
         }
      }

      NullValueProvider nuller = this.findValueNullProvider(ctxt, (SettableBeanProperty)prop, propMetadata);
      if (nuller != null) {
         prop = ((SettableBeanProperty)prop).withNullProvider(nuller);
      }

      return (SettableBeanProperty)prop;
   }

   public AccessPattern getNullAccessPattern() {
      return AccessPattern.ALWAYS_NULL;
   }

   public AccessPattern getEmptyAccessPattern() {
      return AccessPattern.DYNAMIC;
   }

   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
      try {
         return this._valueInstantiator.createUsingDefault(ctxt);
      } catch (IOException var3) {
         return ClassUtil.throwAsMappingException(ctxt, var3);
      }
   }

   public boolean isCachable() {
      return true;
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return Boolean.TRUE;
   }

   public Class<?> handledType() {
      return this._beanType.getRawClass();
   }

   public ObjectIdReader getObjectIdReader() {
      return this._objectIdReader;
   }

   public boolean hasProperty(String propertyName) {
      return this._beanProperties.find(propertyName) != null;
   }

   public boolean hasViews() {
      return this._needViewProcesing;
   }

   public int getPropertyCount() {
      return this._beanProperties.size();
   }

   public Collection<Object> getKnownPropertyNames() {
      ArrayList<Object> names = new ArrayList();
      Iterator i$ = this._beanProperties.iterator();

      while(i$.hasNext()) {
         SettableBeanProperty prop = (SettableBeanProperty)i$.next();
         names.add(prop.getName());
      }

      return names;
   }

   /** @deprecated */
   @Deprecated
   public final Class<?> getBeanClass() {
      return this._beanType.getRawClass();
   }

   public JavaType getValueType() {
      return this._beanType;
   }

   public Iterator<SettableBeanProperty> properties() {
      if (this._beanProperties == null) {
         throw new IllegalStateException("Can only call after BeanDeserializer has been resolved");
      } else {
         return this._beanProperties.iterator();
      }
   }

   public Iterator<SettableBeanProperty> creatorProperties() {
      return this._propertyBasedCreator == null ? Collections.emptyList().iterator() : this._propertyBasedCreator.properties().iterator();
   }

   public SettableBeanProperty findProperty(PropertyName propertyName) {
      return this.findProperty(propertyName.getSimpleName());
   }

   public SettableBeanProperty findProperty(String propertyName) {
      SettableBeanProperty prop = this._beanProperties == null ? null : this._beanProperties.find(propertyName);
      if (_neitherNull(prop, this._propertyBasedCreator)) {
         prop = this._propertyBasedCreator.findCreatorProperty(propertyName);
      }

      return prop;
   }

   public SettableBeanProperty findProperty(int propertyIndex) {
      SettableBeanProperty prop = this._beanProperties == null ? null : this._beanProperties.find(propertyIndex);
      if (_neitherNull(prop, this._propertyBasedCreator)) {
         prop = this._propertyBasedCreator.findCreatorProperty(propertyIndex);
      }

      return prop;
   }

   public SettableBeanProperty findBackReference(String logicalName) {
      return this._backRefs == null ? null : (SettableBeanProperty)this._backRefs.get(logicalName);
   }

   public ValueInstantiator getValueInstantiator() {
      return this._valueInstantiator;
   }

   public void replaceProperty(SettableBeanProperty original, SettableBeanProperty replacement) {
      this._beanProperties.replace(replacement);
   }

   public abstract Object deserializeFromObject(JsonParser var1, DeserializationContext var2) throws IOException;

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      if (this._objectIdReader != null) {
         if (p.canReadObjectId()) {
            Object id = p.getObjectId();
            if (id != null) {
               Object ob = typeDeserializer.deserializeTypedFromObject(p, ctxt);
               return this._handleTypedObjectId(p, ctxt, ob, id);
            }
         }

         JsonToken t = p.getCurrentToken();
         if (t != null) {
            if (t.isScalarValue()) {
               return this.deserializeFromObjectId(p, ctxt);
            }

            if (t == JsonToken.START_OBJECT) {
               t = p.nextToken();
            }

            if (t == JsonToken.FIELD_NAME && this._objectIdReader.maySerializeAsObject() && this._objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p)) {
               return this.deserializeFromObjectId(p, ctxt);
            }
         }
      }

      return typeDeserializer.deserializeTypedFromObject(p, ctxt);
   }

   protected Object _handleTypedObjectId(JsonParser p, DeserializationContext ctxt, Object pojo, Object rawId) throws IOException {
      JsonDeserializer<Object> idDeser = this._objectIdReader.getDeserializer();
      Object id;
      if (idDeser.handledType() == rawId.getClass()) {
         id = rawId;
      } else {
         id = this._convertObjectId(p, ctxt, rawId, idDeser);
      }

      ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
      roid.bindItem(pojo);
      SettableBeanProperty idProp = this._objectIdReader.idProperty;
      return idProp != null ? idProp.setAndReturn(pojo, id) : pojo;
   }

   protected Object _convertObjectId(JsonParser p, DeserializationContext ctxt, Object rawId, JsonDeserializer<Object> idDeser) throws IOException {
      TokenBuffer buf = new TokenBuffer(p, ctxt);
      if (rawId instanceof String) {
         buf.writeString((String)rawId);
      } else if (rawId instanceof Long) {
         buf.writeNumber((Long)rawId);
      } else if (rawId instanceof Integer) {
         buf.writeNumber((Integer)rawId);
      } else {
         buf.writeObject(rawId);
      }

      JsonParser bufParser = buf.asParser();
      bufParser.nextToken();
      return idDeser.deserialize(bufParser, ctxt);
   }

   protected Object deserializeWithObjectId(JsonParser p, DeserializationContext ctxt) throws IOException {
      return this.deserializeFromObject(p, ctxt);
   }

   protected Object deserializeFromObjectId(JsonParser p, DeserializationContext ctxt) throws IOException {
      Object id = this._objectIdReader.readObjectReference(p, ctxt);
      ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
      Object pojo = roid.resolve();
      if (pojo == null) {
         throw new UnresolvedForwardReference(p, "Could not resolve Object Id [" + id + "] (for " + this._beanType + ").", p.getCurrentLocation(), roid);
      } else {
         return pojo;
      }
   }

   protected Object deserializeFromObjectUsingNonDefault(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
      if (delegateDeser != null) {
         return this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
      } else if (this._propertyBasedCreator != null) {
         return this._deserializeUsingPropertyBased(p, ctxt);
      } else {
         Class<?> raw = this._beanType.getRawClass();
         return ClassUtil.isNonStaticInnerClass(raw) ? ctxt.handleMissingInstantiator(raw, (ValueInstantiator)null, p, "can only instantiate non-static inner class by using default, no-argument constructor") : ctxt.handleMissingInstantiator(raw, this.getValueInstantiator(), p, "cannot deserialize from Object value (no delegate- or property-based Creator)");
      }
   }

   protected abstract Object _deserializeUsingPropertyBased(JsonParser var1, DeserializationContext var2) throws IOException;

   public Object deserializeFromNumber(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._objectIdReader != null) {
         return this.deserializeFromObjectId(p, ctxt);
      } else {
         JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
         JsonParser.NumberType nt = p.getNumberType();
         Object bean;
         if (nt == JsonParser.NumberType.INT) {
            if (delegateDeser != null && !this._valueInstantiator.canCreateFromInt()) {
               bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
               if (this._injectables != null) {
                  this.injectValues(ctxt, bean);
               }

               return bean;
            } else {
               return this._valueInstantiator.createFromInt(ctxt, p.getIntValue());
            }
         } else if (nt == JsonParser.NumberType.LONG) {
            if (delegateDeser != null && !this._valueInstantiator.canCreateFromInt()) {
               bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
               if (this._injectables != null) {
                  this.injectValues(ctxt, bean);
               }

               return bean;
            } else {
               return this._valueInstantiator.createFromLong(ctxt, p.getLongValue());
            }
         } else if (delegateDeser != null) {
            bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
               this.injectValues(ctxt, bean);
            }

            return bean;
         } else {
            return ctxt.handleMissingInstantiator(this.handledType(), this.getValueInstantiator(), p, "no suitable creator method found to deserialize from Number value (%s)", p.getNumberValue());
         }
      }
   }

   public Object deserializeFromString(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._objectIdReader != null) {
         return this.deserializeFromObjectId(p, ctxt);
      } else {
         JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
         if (delegateDeser != null && !this._valueInstantiator.canCreateFromString()) {
            Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
               this.injectValues(ctxt, bean);
            }

            return bean;
         } else {
            return this._valueInstantiator.createFromString(ctxt, p.getText());
         }
      }
   }

   public Object deserializeFromDouble(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonParser.NumberType t = p.getNumberType();
      JsonDeserializer delegateDeser;
      if (t != JsonParser.NumberType.DOUBLE && t != JsonParser.NumberType.FLOAT) {
         delegateDeser = this._delegateDeserializer();
         return delegateDeser != null ? this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt)) : ctxt.handleMissingInstantiator(this.handledType(), this.getValueInstantiator(), p, "no suitable creator method found to deserialize from Number value (%s)", p.getNumberValue());
      } else {
         delegateDeser = this._delegateDeserializer();
         if (delegateDeser != null && !this._valueInstantiator.canCreateFromDouble()) {
            Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
               this.injectValues(ctxt, bean);
            }

            return bean;
         } else {
            return this._valueInstantiator.createFromDouble(ctxt, p.getDoubleValue());
         }
      }
   }

   public Object deserializeFromBoolean(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
      if (delegateDeser != null && !this._valueInstantiator.canCreateFromBoolean()) {
         Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
         if (this._injectables != null) {
            this.injectValues(ctxt, bean);
         }

         return bean;
      } else {
         boolean value = p.getCurrentToken() == JsonToken.VALUE_TRUE;
         return this._valueInstantiator.createFromBoolean(ctxt, value);
      }
   }

   public Object deserializeFromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonDeserializer<Object> delegateDeser = this._arrayDelegateDeserializer;
      if (delegateDeser == null && (delegateDeser = this._delegateDeserializer) == null) {
         JsonToken t;
         if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            t = p.nextToken();
            if (t == JsonToken.END_ARRAY && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
               return null;
            } else {
               Object value = this.deserialize(p, ctxt);
               if (p.nextToken() != JsonToken.END_ARRAY) {
                  this.handleMissingEndArrayForSingle(p, ctxt);
               }

               return value;
            }
         } else if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
            t = p.nextToken();
            return t == JsonToken.END_ARRAY ? null : ctxt.handleUnexpectedToken(this.handledType(), JsonToken.START_ARRAY, p, (String)null);
         } else {
            return ctxt.handleUnexpectedToken(this.handledType(), p);
         }
      } else {
         Object bean = this._valueInstantiator.createUsingArrayDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
         if (this._injectables != null) {
            this.injectValues(ctxt, bean);
         }

         return bean;
      }
   }

   public Object deserializeFromEmbedded(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._objectIdReader != null) {
         return this.deserializeFromObjectId(p, ctxt);
      } else {
         JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
         Object value;
         if (delegateDeser != null && !this._valueInstantiator.canCreateFromString()) {
            value = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
               this.injectValues(ctxt, value);
            }

            return value;
         } else {
            value = p.getEmbeddedObject();
            if (value != null && !this._beanType.getClass().isInstance(value)) {
               value = ctxt.handleWeirdNativeValue(this._beanType, value, p);
            }

            return value;
         }
      }
   }

   private final JsonDeserializer<Object> _delegateDeserializer() {
      JsonDeserializer<Object> deser = this._delegateDeserializer;
      if (deser == null) {
         deser = this._arrayDelegateDeserializer;
      }

      return deser;
   }

   protected void injectValues(DeserializationContext ctxt, Object bean) throws IOException {
      ValueInjector[] arr$ = this._injectables;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         ValueInjector injector = arr$[i$];
         injector.inject(ctxt, bean);
      }

   }

   protected Object handleUnknownProperties(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException {
      unknownTokens.writeEndObject();
      JsonParser bufferParser = unknownTokens.asParser();

      while(bufferParser.nextToken() != JsonToken.END_OBJECT) {
         String propName = bufferParser.getCurrentName();
         bufferParser.nextToken();
         this.handleUnknownProperty(bufferParser, ctxt, bean, propName);
      }

      return bean;
   }

   protected void handleUnknownVanilla(JsonParser p, DeserializationContext ctxt, Object bean, String propName) throws IOException {
      if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
         this.handleIgnoredProperty(p, ctxt, bean, propName);
      } else if (this._anySetter != null) {
         try {
            this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
         } catch (Exception var6) {
            this.wrapAndThrow(var6, bean, propName, ctxt);
         }
      } else {
         this.handleUnknownProperty(p, ctxt, bean, propName);
      }

   }

   protected void handleUnknownProperty(JsonParser p, DeserializationContext ctxt, Object beanOrClass, String propName) throws IOException {
      if (this._ignoreAllUnknown) {
         p.skipChildren();
      } else {
         if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
            this.handleIgnoredProperty(p, ctxt, beanOrClass, propName);
         }

         super.handleUnknownProperty(p, ctxt, beanOrClass, propName);
      }
   }

   protected void handleIgnoredProperty(JsonParser p, DeserializationContext ctxt, Object beanOrClass, String propName) throws IOException {
      if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)) {
         throw IgnoredPropertyException.from(p, beanOrClass, propName, this.getKnownPropertyNames());
      } else {
         p.skipChildren();
      }
   }

   protected Object handlePolymorphic(JsonParser p, DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException {
      JsonDeserializer<Object> subDeser = this._findSubclassDeserializer(ctxt, bean, unknownTokens);
      if (subDeser != null) {
         if (unknownTokens != null) {
            unknownTokens.writeEndObject();
            JsonParser p2 = unknownTokens.asParser();
            p2.nextToken();
            bean = subDeser.deserialize(p2, ctxt, bean);
         }

         if (p != null) {
            bean = subDeser.deserialize(p, ctxt, bean);
         }

         return bean;
      } else {
         if (unknownTokens != null) {
            bean = this.handleUnknownProperties(ctxt, bean, unknownTokens);
         }

         if (p != null) {
            bean = this.deserialize(p, ctxt, bean);
         }

         return bean;
      }
   }

   protected JsonDeserializer<Object> _findSubclassDeserializer(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException {
      JsonDeserializer subDeser;
      synchronized(this) {
         subDeser = this._subDeserializers == null ? null : (JsonDeserializer)this._subDeserializers.get(new ClassKey(bean.getClass()));
      }

      if (subDeser != null) {
         return subDeser;
      } else {
         JavaType type = ctxt.constructType(bean.getClass());
         subDeser = ctxt.findRootValueDeserializer(type);
         if (subDeser != null) {
            synchronized(this) {
               if (this._subDeserializers == null) {
                  this._subDeserializers = new HashMap();
               }

               this._subDeserializers.put(new ClassKey(bean.getClass()), subDeser);
            }
         }

         return subDeser;
      }
   }

   public void wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt) throws IOException {
      throw JsonMappingException.wrapWithPath(this.throwOrReturnThrowable(t, ctxt), bean, fieldName);
   }

   private Throwable throwOrReturnThrowable(Throwable t, DeserializationContext ctxt) throws IOException {
      while(t instanceof InvocationTargetException && t.getCause() != null) {
         t = t.getCause();
      }

      ClassUtil.throwIfError(t);
      boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
      if (t instanceof IOException) {
         if (!wrap || !(t instanceof JsonProcessingException)) {
            throw (IOException)t;
         }
      } else if (!wrap) {
         ClassUtil.throwIfRTE(t);
      }

      return t;
   }

   protected Object wrapInstantiationProblem(Throwable t, DeserializationContext ctxt) throws IOException {
      while(t instanceof InvocationTargetException && t.getCause() != null) {
         t = t.getCause();
      }

      ClassUtil.throwIfError(t);
      if (t instanceof IOException) {
         throw (IOException)t;
      } else {
         boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
         if (!wrap) {
            ClassUtil.throwIfRTE(t);
         }

         return ctxt.handleInstantiationProblem(this._beanType.getRawClass(), (Object)null, t);
      }
   }
}
