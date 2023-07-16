package software.bernie.shadowed.fasterxml.jackson.databind.deser;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.BeanAsArrayBuilderDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.TokenBuffer;

public class BuilderBasedDeserializer extends BeanDeserializerBase {
   private static final long serialVersionUID = 1L;
   protected final AnnotatedMethod _buildMethod;
   protected final JavaType _targetType;

   public BuilderBasedDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, JavaType targetType, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
      super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
      this._targetType = targetType;
      this._buildMethod = builder.getBuildMethod();
      if (this._objectIdReader != null) {
         throw new IllegalArgumentException("Cannot use Object Id with Builder-based deserialization (type " + beanDesc.getType() + ")");
      }
   }

   /** @deprecated */
   @Deprecated
   public BuilderBasedDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
      this(builder, beanDesc, beanDesc.getType(), properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
   }

   protected BuilderBasedDeserializer(BuilderBasedDeserializer src) {
      this(src, src._ignoreAllUnknown);
   }

   protected BuilderBasedDeserializer(BuilderBasedDeserializer src, boolean ignoreAllUnknown) {
      super(src, ignoreAllUnknown);
      this._buildMethod = src._buildMethod;
      this._targetType = src._targetType;
   }

   protected BuilderBasedDeserializer(BuilderBasedDeserializer src, NameTransformer unwrapper) {
      super(src, (NameTransformer)unwrapper);
      this._buildMethod = src._buildMethod;
      this._targetType = src._targetType;
   }

   public BuilderBasedDeserializer(BuilderBasedDeserializer src, ObjectIdReader oir) {
      super(src, (ObjectIdReader)oir);
      this._buildMethod = src._buildMethod;
      this._targetType = src._targetType;
   }

   public BuilderBasedDeserializer(BuilderBasedDeserializer src, Set<String> ignorableProps) {
      super(src, (Set)ignorableProps);
      this._buildMethod = src._buildMethod;
      this._targetType = src._targetType;
   }

   public BuilderBasedDeserializer(BuilderBasedDeserializer src, BeanPropertyMap props) {
      super(src, (BeanPropertyMap)props);
      this._buildMethod = src._buildMethod;
      this._targetType = src._targetType;
   }

   public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
      return new BuilderBasedDeserializer(this, unwrapper);
   }

   public BeanDeserializerBase withObjectIdReader(ObjectIdReader oir) {
      return new BuilderBasedDeserializer(this, oir);
   }

   public BeanDeserializerBase withIgnorableProperties(Set<String> ignorableProps) {
      return new BuilderBasedDeserializer(this, ignorableProps);
   }

   public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
      return new BuilderBasedDeserializer(this, props);
   }

   protected BeanDeserializerBase asArrayDeserializer() {
      SettableBeanProperty[] props = this._beanProperties.getPropertiesInInsertionOrder();
      return new BeanAsArrayBuilderDeserializer(this, this._targetType, props, this._buildMethod);
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return Boolean.FALSE;
   }

   protected final Object finishBuild(DeserializationContext ctxt, Object builder) throws IOException {
      if (null == this._buildMethod) {
         return builder;
      } else {
         try {
            return this._buildMethod.getMember().invoke(builder, (Object[])null);
         } catch (Exception var4) {
            return this.wrapInstantiationProblem(var4, ctxt);
         }
      }
   }

   public final Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.isExpectedStartObjectToken()) {
         JsonToken t = p.nextToken();
         if (this._vanillaProcessing) {
            return this.finishBuild(ctxt, this.vanillaDeserialize(p, ctxt, t));
         } else {
            Object builder = this.deserializeFromObject(p, ctxt);
            return this.finishBuild(ctxt, builder);
         }
      } else {
         switch(p.getCurrentTokenId()) {
         case 2:
         case 5:
            return this.finishBuild(ctxt, this.deserializeFromObject(p, ctxt));
         case 3:
            return this.finishBuild(ctxt, this.deserializeFromArray(p, ctxt));
         case 4:
         case 11:
         default:
            return ctxt.handleUnexpectedToken(this.handledType(), p);
         case 6:
            return this.finishBuild(ctxt, this.deserializeFromString(p, ctxt));
         case 7:
            return this.finishBuild(ctxt, this.deserializeFromNumber(p, ctxt));
         case 8:
            return this.finishBuild(ctxt, this.deserializeFromDouble(p, ctxt));
         case 9:
         case 10:
            return this.finishBuild(ctxt, this.deserializeFromBoolean(p, ctxt));
         case 12:
            return p.getEmbeddedObject();
         }
      }
   }

   public Object deserialize(JsonParser p, DeserializationContext ctxt, Object value) throws IOException {
      JavaType valueType = this._targetType;
      Class<?> builderRawType = this.handledType();
      Class<?> instRawType = value.getClass();
      return builderRawType.isAssignableFrom(instRawType) ? ctxt.reportBadDefinition(valueType, String.format("Deserialization of %s by passing existing Builder (%s) instance not supported", valueType, builderRawType.getName())) : ctxt.reportBadDefinition(valueType, String.format("Deserialization of %s by passing existing instance (of %s) not supported", valueType, instRawType.getName()));
   }

   private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
      Object bean;
      for(bean = this._valueInstantiator.createUsingDefault(ctxt); p.getCurrentToken() != JsonToken.END_OBJECT; p.nextToken()) {
         String propName = p.getCurrentName();
         p.nextToken();
         SettableBeanProperty prop = this._beanProperties.find(propName);
         if (prop != null) {
            try {
               bean = prop.deserializeSetAndReturn(p, ctxt, bean);
            } catch (Exception var8) {
               this.wrapAndThrow(var8, bean, propName, ctxt);
            }
         } else {
            this.handleUnknownVanilla(p, ctxt, bean, propName);
         }
      }

      return bean;
   }

   public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._nonStandardCreation) {
         if (this._unwrappedPropertyHandler != null) {
            return this.deserializeWithUnwrapped(p, ctxt);
         } else {
            return this._externalTypeIdHandler != null ? this.deserializeWithExternalTypeId(p, ctxt) : this.deserializeFromObjectUsingNonDefault(p, ctxt);
         }
      } else {
         Object bean = this._valueInstantiator.createUsingDefault(ctxt);
         if (this._injectables != null) {
            this.injectValues(ctxt, bean);
         }

         if (this._needViewProcesing) {
            Class<?> view = ctxt.getActiveView();
            if (view != null) {
               return this.deserializeWithView(p, ctxt, bean, view);
            }
         }

         for(; p.getCurrentToken() != JsonToken.END_OBJECT; p.nextToken()) {
            String propName = p.getCurrentName();
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
               try {
                  bean = prop.deserializeSetAndReturn(p, ctxt, bean);
               } catch (Exception var7) {
                  this.wrapAndThrow(var7, bean, propName, ctxt);
               }
            } else {
               this.handleUnknownVanilla(p, ctxt, bean, propName);
            }
         }

         return bean;
      }
   }

   protected final Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException {
      PropertyBasedCreator creator = this._propertyBasedCreator;
      PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
      Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
      TokenBuffer unknown = null;
      JsonToken t = p.getCurrentToken();

      Object builder;
      while(true) {
         if (t != JsonToken.FIELD_NAME) {
            Object builder;
            try {
               builder = creator.build(ctxt, buffer);
            } catch (Exception var12) {
               builder = this.wrapInstantiationProblem(var12, ctxt);
            }

            if (unknown != null) {
               if (builder.getClass() != this._beanType.getRawClass()) {
                  return this.handlePolymorphic((JsonParser)null, ctxt, builder, unknown);
               }

               return this.handleUnknownProperties(ctxt, builder, unknown);
            }

            return builder;
         }

         String propName = p.getCurrentName();
         p.nextToken();
         SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
         if (creatorProp != null) {
            if (activeView != null && !creatorProp.visibleInView(activeView)) {
               p.skipChildren();
            } else if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
               p.nextToken();

               try {
                  builder = creator.build(ctxt, buffer);
                  break;
               } catch (Exception var13) {
                  this.wrapAndThrow(var13, this._beanType.getRawClass(), propName, ctxt);
               }
            }
         } else if (!buffer.readIdProperty(propName)) {
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
               buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
            } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
               this.handleIgnoredProperty(p, ctxt, this.handledType(), propName);
            } else if (this._anySetter != null) {
               buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
            } else {
               if (unknown == null) {
                  unknown = new TokenBuffer(p, ctxt);
               }

               unknown.writeFieldName(propName);
               unknown.copyCurrentStructure(p);
            }
         }

         t = p.nextToken();
      }

      if (builder.getClass() != this._beanType.getRawClass()) {
         return this.handlePolymorphic(p, ctxt, builder, unknown);
      } else {
         if (unknown != null) {
            builder = this.handleUnknownProperties(ctxt, builder, unknown);
         }

         return this._deserialize(p, ctxt, builder);
      }
   }

   protected final Object _deserialize(JsonParser p, DeserializationContext ctxt, Object builder) throws IOException {
      if (this._injectables != null) {
         this.injectValues(ctxt, builder);
      }

      if (this._unwrappedPropertyHandler != null) {
         if (p.hasToken(JsonToken.START_OBJECT)) {
            p.nextToken();
         }

         TokenBuffer tokens = new TokenBuffer(p, ctxt);
         tokens.writeStartObject();
         return this.deserializeWithUnwrapped(p, ctxt, builder, tokens);
      } else if (this._externalTypeIdHandler != null) {
         return this.deserializeWithExternalTypeId(p, ctxt, builder);
      } else {
         if (this._needViewProcesing) {
            Class<?> view = ctxt.getActiveView();
            if (view != null) {
               return this.deserializeWithView(p, ctxt, builder, view);
            }
         }

         JsonToken t = p.getCurrentToken();
         if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
         }

         for(; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
            String propName = p.getCurrentName();
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
               try {
                  builder = prop.deserializeSetAndReturn(p, ctxt, builder);
               } catch (Exception var8) {
                  this.wrapAndThrow(var8, builder, propName, ctxt);
               }
            } else {
               this.handleUnknownVanilla(p, ctxt, this.handledType(), propName);
            }
         }

         return builder;
      }
   }

   protected final Object deserializeWithView(JsonParser p, DeserializationContext ctxt, Object bean, Class<?> activeView) throws IOException {
      for(JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
         String propName = p.getCurrentName();
         p.nextToken();
         SettableBeanProperty prop = this._beanProperties.find(propName);
         if (prop != null) {
            if (!prop.visibleInView(activeView)) {
               p.skipChildren();
            } else {
               try {
                  bean = prop.deserializeSetAndReturn(p, ctxt, bean);
               } catch (Exception var9) {
                  this.wrapAndThrow(var9, bean, propName, ctxt);
               }
            }
         } else {
            this.handleUnknownVanilla(p, ctxt, bean, propName);
         }
      }

      return bean;
   }

   protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._delegateDeserializer != null) {
         return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
      } else if (this._propertyBasedCreator != null) {
         return this.deserializeUsingPropertyBasedWithUnwrapped(p, ctxt);
      } else {
         TokenBuffer tokens = new TokenBuffer(p, ctxt);
         tokens.writeStartObject();
         Object bean = this._valueInstantiator.createUsingDefault(ctxt);
         if (this._injectables != null) {
            this.injectValues(ctxt, bean);
         }

         for(Class activeView = this._needViewProcesing ? ctxt.getActiveView() : null; p.getCurrentToken() != JsonToken.END_OBJECT; p.nextToken()) {
            String propName = p.getCurrentName();
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
               if (activeView != null && !prop.visibleInView(activeView)) {
                  p.skipChildren();
               } else {
                  try {
                     bean = prop.deserializeSetAndReturn(p, ctxt, bean);
                  } catch (Exception var10) {
                     this.wrapAndThrow(var10, bean, propName, ctxt);
                  }
               }
            } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
               this.handleIgnoredProperty(p, ctxt, bean, propName);
            } else {
               tokens.writeFieldName(propName);
               tokens.copyCurrentStructure(p);
               if (this._anySetter != null) {
                  try {
                     this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
                  } catch (Exception var9) {
                     this.wrapAndThrow(var9, bean, propName, ctxt);
                  }
               }
            }
         }

         tokens.writeEndObject();
         return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
      }
   }

   protected Object deserializeUsingPropertyBasedWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
      PropertyBasedCreator creator = this._propertyBasedCreator;
      PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
      TokenBuffer tokens = new TokenBuffer(p, ctxt);
      tokens.writeStartObject();
      Object builder = null;

      for(JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
         String propName = p.getCurrentName();
         p.nextToken();
         SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
         if (creatorProp != null) {
            if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
               t = p.nextToken();

               try {
                  builder = creator.build(ctxt, buffer);
                  return builder.getClass() != this._beanType.getRawClass() ? this.handlePolymorphic(p, ctxt, builder, tokens) : this.deserializeWithUnwrapped(p, ctxt, builder, tokens);
               } catch (Exception var12) {
                  this.wrapAndThrow(var12, this._beanType.getRawClass(), propName, ctxt);
               }
            }
         } else if (!buffer.readIdProperty(propName)) {
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
               buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
            } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
               this.handleIgnoredProperty(p, ctxt, this.handledType(), propName);
            } else {
               tokens.writeFieldName(propName);
               tokens.copyCurrentStructure(p);
               if (this._anySetter != null) {
                  buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
               }
            }
         }
      }

      if (builder == null) {
         try {
            builder = creator.build(ctxt, buffer);
         } catch (Exception var11) {
            return this.wrapInstantiationProblem(var11, ctxt);
         }
      }

      return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, builder, tokens);
   }

   protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt, Object builder, TokenBuffer tokens) throws IOException {
      Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;

      for(JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
         String propName = p.getCurrentName();
         SettableBeanProperty prop = this._beanProperties.find(propName);
         p.nextToken();
         if (prop != null) {
            if (activeView != null && !prop.visibleInView(activeView)) {
               p.skipChildren();
            } else {
               try {
                  builder = prop.deserializeSetAndReturn(p, ctxt, builder);
               } catch (Exception var10) {
                  this.wrapAndThrow(var10, builder, propName, ctxt);
               }
            }
         } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
            this.handleIgnoredProperty(p, ctxt, builder, propName);
         } else {
            tokens.writeFieldName(propName);
            tokens.copyCurrentStructure(p);
            if (this._anySetter != null) {
               this._anySetter.deserializeAndSet(p, ctxt, builder, propName);
            }
         }
      }

      tokens.writeEndObject();
      return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, builder, tokens);
   }

   protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
      return this._propertyBasedCreator != null ? this.deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt) : this.deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
   }

   protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
      Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
      ExternalTypeHandler ext = this._externalTypeIdHandler.start();

      for(JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
         String propName = p.getCurrentName();
         t = p.nextToken();
         SettableBeanProperty prop = this._beanProperties.find(propName);
         if (prop != null) {
            if (t.isScalarValue()) {
               ext.handleTypePropertyValue(p, ctxt, propName, bean);
            }

            if (activeView != null && !prop.visibleInView(activeView)) {
               p.skipChildren();
            } else {
               try {
                  bean = prop.deserializeSetAndReturn(p, ctxt, bean);
               } catch (Exception var11) {
                  this.wrapAndThrow(var11, bean, propName, ctxt);
               }
            }
         } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
            this.handleIgnoredProperty(p, ctxt, bean, propName);
         } else if (!ext.handlePropertyValue(p, ctxt, propName, bean)) {
            if (this._anySetter != null) {
               try {
                  this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
               } catch (Exception var10) {
                  this.wrapAndThrow(var10, bean, propName, ctxt);
               }
            } else {
               this.handleUnknownProperty(p, ctxt, bean, propName);
            }
         }
      }

      return ext.complete(p, ctxt, bean);
   }

   protected Object deserializeUsingPropertyBasedWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
      JavaType t = this._targetType;
      return ctxt.reportBadDefinition(t, String.format("Deserialization (of %s) with Builder, External type id, @JsonCreator not yet implemented", t));
   }
}
