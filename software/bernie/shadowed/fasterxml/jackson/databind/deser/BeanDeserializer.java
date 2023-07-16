package software.bernie.shadowed.fasterxml.jackson.databind.deser;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.BeanAsArrayDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.TokenBuffer;

public class BeanDeserializer extends BeanDeserializerBase implements Serializable {
   private static final long serialVersionUID = 1L;
   protected transient Exception _nullFromCreator;
   private transient volatile NameTransformer _currentlyTransforming;

   public BeanDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
      super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
   }

   protected BeanDeserializer(BeanDeserializerBase src) {
      super(src, src._ignoreAllUnknown);
   }

   protected BeanDeserializer(BeanDeserializerBase src, boolean ignoreAllUnknown) {
      super(src, ignoreAllUnknown);
   }

   protected BeanDeserializer(BeanDeserializerBase src, NameTransformer unwrapper) {
      super(src, unwrapper);
   }

   public BeanDeserializer(BeanDeserializerBase src, ObjectIdReader oir) {
      super(src, oir);
   }

   public BeanDeserializer(BeanDeserializerBase src, Set<String> ignorableProps) {
      super(src, ignorableProps);
   }

   public BeanDeserializer(BeanDeserializerBase src, BeanPropertyMap props) {
      super(src, props);
   }

   public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer transformer) {
      if (this.getClass() != BeanDeserializer.class) {
         return this;
      } else if (this._currentlyTransforming == transformer) {
         return this;
      } else {
         this._currentlyTransforming = transformer;

         BeanDeserializer var2;
         try {
            var2 = new BeanDeserializer(this, transformer);
         } finally {
            this._currentlyTransforming = null;
         }

         return var2;
      }
   }

   public BeanDeserializer withObjectIdReader(ObjectIdReader oir) {
      return new BeanDeserializer(this, oir);
   }

   public BeanDeserializer withIgnorableProperties(Set<String> ignorableProps) {
      return new BeanDeserializer(this, ignorableProps);
   }

   public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
      return new BeanDeserializer(this, props);
   }

   protected BeanDeserializerBase asArrayDeserializer() {
      SettableBeanProperty[] props = this._beanProperties.getPropertiesInInsertionOrder();
      return new BeanAsArrayDeserializer(this, props);
   }

   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.isExpectedStartObjectToken()) {
         if (this._vanillaProcessing) {
            return this.vanillaDeserialize(p, ctxt, p.nextToken());
         } else {
            p.nextToken();
            return this._objectIdReader != null ? this.deserializeWithObjectId(p, ctxt) : this.deserializeFromObject(p, ctxt);
         }
      } else {
         return this._deserializeOther(p, ctxt, p.getCurrentToken());
      }
   }

   protected final Object _deserializeOther(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
      switch(t) {
      case VALUE_STRING:
         return this.deserializeFromString(p, ctxt);
      case VALUE_NUMBER_INT:
         return this.deserializeFromNumber(p, ctxt);
      case VALUE_NUMBER_FLOAT:
         return this.deserializeFromDouble(p, ctxt);
      case VALUE_EMBEDDED_OBJECT:
         return this.deserializeFromEmbedded(p, ctxt);
      case VALUE_TRUE:
      case VALUE_FALSE:
         return this.deserializeFromBoolean(p, ctxt);
      case VALUE_NULL:
         return this.deserializeFromNull(p, ctxt);
      case START_ARRAY:
         return this.deserializeFromArray(p, ctxt);
      case FIELD_NAME:
      case END_OBJECT:
         if (this._vanillaProcessing) {
            return this.vanillaDeserialize(p, ctxt, t);
         } else {
            if (this._objectIdReader != null) {
               return this.deserializeWithObjectId(p, ctxt);
            }

            return this.deserializeFromObject(p, ctxt);
         }
      default:
         return ctxt.handleUnexpectedToken(this.handledType(), p);
      }
   }

   /** @deprecated */
   @Deprecated
   protected Object _missingToken(JsonParser p, DeserializationContext ctxt) throws IOException {
      throw ctxt.endOfInputException(this.handledType());
   }

   public Object deserialize(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
      p.setCurrentValue(bean);
      if (this._injectables != null) {
         this.injectValues(ctxt, bean);
      }

      if (this._unwrappedPropertyHandler != null) {
         return this.deserializeWithUnwrapped(p, ctxt, bean);
      } else if (this._externalTypeIdHandler != null) {
         return this.deserializeWithExternalTypeId(p, ctxt, bean);
      } else {
         String propName;
         if (p.isExpectedStartObjectToken()) {
            propName = p.nextFieldName();
            if (propName == null) {
               return bean;
            }
         } else {
            if (!p.hasTokenId(5)) {
               return bean;
            }

            propName = p.getCurrentName();
         }

         if (this._needViewProcesing) {
            Class<?> view = ctxt.getActiveView();
            if (view != null) {
               return this.deserializeWithView(p, ctxt, bean, view);
            }
         }

         do {
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
               try {
                  prop.deserializeAndSet(p, ctxt, bean);
               } catch (Exception var7) {
                  this.wrapAndThrow(var7, bean, propName, ctxt);
               }
            } else {
               this.handleUnknownVanilla(p, ctxt, bean, propName);
            }
         } while((propName = p.nextFieldName()) != null);

         return bean;
      }
   }

   private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
      Object bean = this._valueInstantiator.createUsingDefault(ctxt);
      p.setCurrentValue(bean);
      if (p.hasTokenId(5)) {
         String propName = p.getCurrentName();

         do {
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
               try {
                  prop.deserializeAndSet(p, ctxt, bean);
               } catch (Exception var8) {
                  this.wrapAndThrow(var8, bean, propName, ctxt);
               }
            } else {
               this.handleUnknownVanilla(p, ctxt, bean, propName);
            }
         } while((propName = p.nextFieldName()) != null);
      }

      return bean;
   }

   public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._objectIdReader != null && this._objectIdReader.maySerializeAsObject() && p.hasTokenId(5) && this._objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p)) {
         return this.deserializeFromObjectId(p, ctxt);
      } else {
         Object bean;
         if (this._nonStandardCreation) {
            if (this._unwrappedPropertyHandler != null) {
               return this.deserializeWithUnwrapped(p, ctxt);
            } else if (this._externalTypeIdHandler != null) {
               return this.deserializeWithExternalTypeId(p, ctxt);
            } else {
               bean = this.deserializeFromObjectUsingNonDefault(p, ctxt);
               if (this._injectables != null) {
                  this.injectValues(ctxt, bean);
               }

               return bean;
            }
         } else {
            bean = this._valueInstantiator.createUsingDefault(ctxt);
            p.setCurrentValue(bean);
            if (p.canReadObjectId()) {
               Object id = p.getObjectId();
               if (id != null) {
                  this._handleTypedObjectId(p, ctxt, bean, id);
               }
            }

            if (this._injectables != null) {
               this.injectValues(ctxt, bean);
            }

            if (this._needViewProcesing) {
               Class<?> view = ctxt.getActiveView();
               if (view != null) {
                  return this.deserializeWithView(p, ctxt, bean, view);
               }
            }

            if (p.hasTokenId(5)) {
               String propName = p.getCurrentName();

               do {
                  p.nextToken();
                  SettableBeanProperty prop = this._beanProperties.find(propName);
                  if (prop != null) {
                     try {
                        prop.deserializeAndSet(p, ctxt, bean);
                     } catch (Exception var7) {
                        this.wrapAndThrow(var7, bean, propName, ctxt);
                     }
                  } else {
                     this.handleUnknownVanilla(p, ctxt, bean, propName);
                  }
               } while((propName = p.nextFieldName()) != null);
            }

            return bean;
         }
      }
   }

   protected Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException {
      PropertyBasedCreator creator = this._propertyBasedCreator;
      PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
      TokenBuffer unknown = null;
      Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
      JsonToken t = p.getCurrentToken();

      ArrayList referrings;
      for(referrings = null; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
         String propName = p.getCurrentName();
         p.nextToken();
         if (!buffer.readIdProperty(propName)) {
            SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (creatorProp != null) {
               if (activeView != null && !creatorProp.visibleInView(activeView)) {
                  p.skipChildren();
               } else {
                  Object value = this._deserializeWithErrorWrapping(p, ctxt, creatorProp);
                  if (buffer.assignParameter(creatorProp, value)) {
                     p.nextToken();

                     Object bean;
                     try {
                        bean = creator.build(ctxt, buffer);
                     } catch (Exception var14) {
                        bean = this.wrapInstantiationProblem(var14, ctxt);
                     }

                     if (bean == null) {
                        return ctxt.handleInstantiationProblem(this.handledType(), (Object)null, this._creatorReturnedNullException());
                     }

                     p.setCurrentValue(bean);
                     if (bean.getClass() != this._beanType.getRawClass()) {
                        return this.handlePolymorphic(p, ctxt, bean, unknown);
                     }

                     if (unknown != null) {
                        bean = this.handleUnknownProperties(ctxt, bean, unknown);
                     }

                     return this.deserialize(p, ctxt, bean);
                  }
               }
            } else {
               SettableBeanProperty prop = this._beanProperties.find(propName);
               if (prop != null) {
                  try {
                     buffer.bufferProperty(prop, this._deserializeWithErrorWrapping(p, ctxt, prop));
                  } catch (UnresolvedForwardReference var17) {
                     BeanDeserializer.BeanReferring referring = this.handleUnresolvedReference(ctxt, prop, buffer, var17);
                     if (referrings == null) {
                        referrings = new ArrayList();
                     }

                     referrings.add(referring);
                  }
               } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                  this.handleIgnoredProperty(p, ctxt, this.handledType(), propName);
               } else if (this._anySetter != null) {
                  try {
                     buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
                  } catch (Exception var16) {
                     this.wrapAndThrow(var16, this._beanType.getRawClass(), propName, ctxt);
                  }
               } else {
                  if (unknown == null) {
                     unknown = new TokenBuffer(p, ctxt);
                  }

                  unknown.writeFieldName(propName);
                  unknown.copyCurrentStructure(p);
               }
            }
         }
      }

      Object bean;
      try {
         bean = creator.build(ctxt, buffer);
      } catch (Exception var15) {
         this.wrapInstantiationProblem(var15, ctxt);
         bean = null;
      }

      if (referrings != null) {
         Iterator i$ = referrings.iterator();

         while(i$.hasNext()) {
            BeanDeserializer.BeanReferring referring = (BeanDeserializer.BeanReferring)i$.next();
            referring.setBean(bean);
         }
      }

      if (unknown != null) {
         if (bean.getClass() != this._beanType.getRawClass()) {
            return this.handlePolymorphic((JsonParser)null, ctxt, bean, unknown);
         } else {
            return this.handleUnknownProperties(ctxt, bean, unknown);
         }
      } else {
         return bean;
      }
   }

   private BeanDeserializer.BeanReferring handleUnresolvedReference(DeserializationContext ctxt, SettableBeanProperty prop, PropertyValueBuffer buffer, UnresolvedForwardReference reference) throws JsonMappingException {
      BeanDeserializer.BeanReferring referring = new BeanDeserializer.BeanReferring(ctxt, reference, prop.getType(), buffer, prop);
      reference.getRoid().appendReferring(referring);
      return referring;
   }

   protected final Object _deserializeWithErrorWrapping(JsonParser p, DeserializationContext ctxt, SettableBeanProperty prop) throws IOException {
      try {
         return prop.deserialize(p, ctxt);
      } catch (Exception var5) {
         this.wrapAndThrow(var5, this._beanType.getRawClass(), prop.getName(), ctxt);
         return null;
      }
   }

   protected Object deserializeFromNull(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.requiresCustomCodec()) {
         TokenBuffer tb = new TokenBuffer(p, ctxt);
         tb.writeEndObject();
         JsonParser p2 = tb.asParser(p);
         p2.nextToken();
         Object ob = this._vanillaProcessing ? this.vanillaDeserialize(p2, ctxt, JsonToken.END_OBJECT) : this.deserializeFromObject(p2, ctxt);
         p2.close();
         return ob;
      } else {
         return ctxt.handleUnexpectedToken(this.handledType(), p);
      }
   }

   protected final Object deserializeWithView(JsonParser p, DeserializationContext ctxt, Object bean, Class<?> activeView) throws IOException {
      if (p.hasTokenId(5)) {
         String propName = p.getCurrentName();

         do {
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
               if (!prop.visibleInView(activeView)) {
                  p.skipChildren();
               } else {
                  try {
                     prop.deserializeAndSet(p, ctxt, bean);
                  } catch (Exception var8) {
                     this.wrapAndThrow(var8, bean, propName, ctxt);
                  }
               }
            } else {
               this.handleUnknownVanilla(p, ctxt, bean, propName);
            }
         } while((propName = p.nextFieldName()) != null);
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
         p.setCurrentValue(bean);
         if (this._injectables != null) {
            this.injectValues(ctxt, bean);
         }

         Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;

         for(String propName = p.hasTokenId(5) ? p.getCurrentName() : null; propName != null; propName = p.nextFieldName()) {
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
               if (activeView != null && !prop.visibleInView(activeView)) {
                  p.skipChildren();
               } else {
                  try {
                     prop.deserializeAndSet(p, ctxt, bean);
                  } catch (Exception var11) {
                     this.wrapAndThrow(var11, bean, propName, ctxt);
                  }
               }
            } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
               this.handleIgnoredProperty(p, ctxt, bean, propName);
            } else if (this._anySetter == null) {
               tokens.writeFieldName(propName);
               tokens.copyCurrentStructure(p);
            } else {
               TokenBuffer b2 = TokenBuffer.asCopyOfValue(p);
               tokens.writeFieldName(propName);
               tokens.append(b2);

               try {
                  this._anySetter.deserializeAndSet(b2.asParserOnFirstToken(), ctxt, bean, propName);
               } catch (Exception var10) {
                  this.wrapAndThrow(var10, bean, propName, ctxt);
               }
            }
         }

         tokens.writeEndObject();
         this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
         return bean;
      }
   }

   protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.START_OBJECT) {
         t = p.nextToken();
      }

      TokenBuffer tokens = new TokenBuffer(p, ctxt);
      tokens.writeStartObject();

      for(Class activeView = this._needViewProcesing ? ctxt.getActiveView() : null; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
         String propName = p.getCurrentName();
         SettableBeanProperty prop = this._beanProperties.find(propName);
         p.nextToken();
         if (prop != null) {
            if (activeView != null && !prop.visibleInView(activeView)) {
               p.skipChildren();
            } else {
               try {
                  prop.deserializeAndSet(p, ctxt, bean);
               } catch (Exception var12) {
                  this.wrapAndThrow(var12, bean, propName, ctxt);
               }
            }
         } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
            this.handleIgnoredProperty(p, ctxt, bean, propName);
         } else if (this._anySetter == null) {
            tokens.writeFieldName(propName);
            tokens.copyCurrentStructure(p);
         } else {
            TokenBuffer b2 = TokenBuffer.asCopyOfValue(p);
            tokens.writeFieldName(propName);
            tokens.append(b2);

            try {
               this._anySetter.deserializeAndSet(b2.asParserOnFirstToken(), ctxt, bean, propName);
            } catch (Exception var11) {
               this.wrapAndThrow(var11, bean, propName, ctxt);
            }
         }
      }

      tokens.writeEndObject();
      this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
      return bean;
   }

   protected Object deserializeUsingPropertyBasedWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
      PropertyBasedCreator creator = this._propertyBasedCreator;
      PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
      TokenBuffer tokens = new TokenBuffer(p, ctxt);
      tokens.writeStartObject();

      for(JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
         String propName = p.getCurrentName();
         p.nextToken();
         SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
         if (creatorProp != null) {
            if (buffer.assignParameter(creatorProp, this._deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
               t = p.nextToken();

               Object bean;
               try {
                  bean = creator.build(ctxt, buffer);
               } catch (Exception var12) {
                  bean = this.wrapInstantiationProblem(var12, ctxt);
               }

               p.setCurrentValue(bean);

               while(t == JsonToken.FIELD_NAME) {
                  p.nextToken();
                  tokens.copyCurrentStructure(p);
                  t = p.nextToken();
               }

               tokens.writeEndObject();
               if (bean.getClass() != this._beanType.getRawClass()) {
                  ctxt.reportInputMismatch((BeanProperty)creatorProp, "Cannot create polymorphic instances with unwrapped values");
                  return null;
               }

               return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
            }
         } else if (!buffer.readIdProperty(propName)) {
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
               buffer.bufferProperty(prop, this._deserializeWithErrorWrapping(p, ctxt, prop));
            } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
               this.handleIgnoredProperty(p, ctxt, this.handledType(), propName);
            } else if (this._anySetter == null) {
               tokens.writeFieldName(propName);
               tokens.copyCurrentStructure(p);
            } else {
               TokenBuffer b2 = TokenBuffer.asCopyOfValue(p);
               tokens.writeFieldName(propName);
               tokens.append(b2);

               try {
                  buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(b2.asParserOnFirstToken(), ctxt));
               } catch (Exception var14) {
                  this.wrapAndThrow(var14, this._beanType.getRawClass(), propName, ctxt);
               }
            }
         }
      }

      Object bean;
      try {
         bean = creator.build(ctxt, buffer);
      } catch (Exception var13) {
         this.wrapInstantiationProblem(var13, ctxt);
         return null;
      }

      return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
   }

   protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._propertyBasedCreator != null) {
         return this.deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt);
      } else {
         return this._delegateDeserializer != null ? this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt)) : this.deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
      }
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
                  prop.deserializeAndSet(p, ctxt, bean);
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
      ExternalTypeHandler ext = this._externalTypeIdHandler.start();
      PropertyBasedCreator creator = this._propertyBasedCreator;
      PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
      TokenBuffer tokens = new TokenBuffer(p, ctxt);
      tokens.writeStartObject();
      JsonToken t = p.getCurrentToken();

      Object bean;
      while(true) {
         if (t != JsonToken.FIELD_NAME) {
            try {
               return ext.complete(p, ctxt, buffer, creator);
            } catch (Exception var12) {
               return this.wrapInstantiationProblem(var12, ctxt);
            }
         }

         String propName = p.getCurrentName();
         p.nextToken();
         SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
         if (creatorProp != null) {
            if (!ext.handlePropertyValue(p, ctxt, propName, (Object)null) && buffer.assignParameter(creatorProp, this._deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
               t = p.nextToken();

               try {
                  bean = creator.build(ctxt, buffer);
                  break;
               } catch (Exception var13) {
                  this.wrapAndThrow(var13, this._beanType.getRawClass(), propName, ctxt);
               }
            }
         } else if (!buffer.readIdProperty(propName)) {
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
               buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
            } else if (!ext.handlePropertyValue(p, ctxt, propName, (Object)null)) {
               if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                  this.handleIgnoredProperty(p, ctxt, this.handledType(), propName);
               } else if (this._anySetter != null) {
                  buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
               }
            }
         }

         t = p.nextToken();
      }

      while(t == JsonToken.FIELD_NAME) {
         p.nextToken();
         tokens.copyCurrentStructure(p);
         t = p.nextToken();
      }

      return bean.getClass() != this._beanType.getRawClass() ? ctxt.reportBadDefinition(this._beanType, String.format("Cannot create polymorphic instances with external type ids (%s -> %s)", this._beanType, bean.getClass())) : ext.complete(p, ctxt, bean);
   }

   protected Exception _creatorReturnedNullException() {
      if (this._nullFromCreator == null) {
         this._nullFromCreator = new NullPointerException("JSON Creator returned null");
      }

      return this._nullFromCreator;
   }

   static class BeanReferring extends ReadableObjectId.Referring {
      private final DeserializationContext _context;
      private final SettableBeanProperty _prop;
      private Object _bean;

      BeanReferring(DeserializationContext ctxt, UnresolvedForwardReference ref, JavaType valueType, PropertyValueBuffer buffer, SettableBeanProperty prop) {
         super(ref, valueType);
         this._context = ctxt;
         this._prop = prop;
      }

      public void setBean(Object bean) {
         this._bean = bean;
      }

      public void handleResolvedForwardReference(Object id, Object value) throws IOException {
         if (this._bean == null) {
            this._context.reportInputMismatch((BeanProperty)this._prop, "Cannot resolve ObjectId forward reference using property '%s' (of type %s): Bean not yet resolved", this._prop.getName(), this._prop.getDeclaringClass().getName());
         }

         this._prop.set(this._bean, value);
      }
   }
}
