package software.bernie.shadowed.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import java.util.Set;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;

public class BeanAsArrayBuilderDeserializer extends BeanDeserializerBase {
   private static final long serialVersionUID = 1L;
   protected final BeanDeserializerBase _delegate;
   protected final SettableBeanProperty[] _orderedProperties;
   protected final AnnotatedMethod _buildMethod;
   protected final JavaType _targetType;

   public BeanAsArrayBuilderDeserializer(BeanDeserializerBase delegate, JavaType targetType, SettableBeanProperty[] ordered, AnnotatedMethod buildMethod) {
      super(delegate);
      this._delegate = delegate;
      this._targetType = targetType;
      this._orderedProperties = ordered;
      this._buildMethod = buildMethod;
   }

   public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
      return this._delegate.unwrappingDeserializer(unwrapper);
   }

   public BeanDeserializerBase withObjectIdReader(ObjectIdReader oir) {
      return new BeanAsArrayBuilderDeserializer(this._delegate.withObjectIdReader(oir), this._targetType, this._orderedProperties, this._buildMethod);
   }

   public BeanDeserializerBase withIgnorableProperties(Set<String> ignorableProps) {
      return new BeanAsArrayBuilderDeserializer(this._delegate.withIgnorableProperties(ignorableProps), this._targetType, this._orderedProperties, this._buildMethod);
   }

   public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
      return new BeanAsArrayBuilderDeserializer(this._delegate.withBeanProperties(props), this._targetType, this._orderedProperties, this._buildMethod);
   }

   protected BeanDeserializerBase asArrayDeserializer() {
      return this;
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return Boolean.FALSE;
   }

   protected final Object finishBuild(DeserializationContext ctxt, Object builder) throws IOException {
      try {
         return this._buildMethod.getMember().invoke(builder, (Object[])null);
      } catch (Exception var4) {
         return this.wrapInstantiationProblem(var4, ctxt);
      }
   }

   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (!p.isExpectedStartArrayToken()) {
         return this.finishBuild(ctxt, this._deserializeFromNonArray(p, ctxt));
      } else if (!this._vanillaProcessing) {
         return this.finishBuild(ctxt, this._deserializeNonVanilla(p, ctxt));
      } else {
         Object builder = this._valueInstantiator.createUsingDefault(ctxt);
         SettableBeanProperty[] props = this._orderedProperties;
         int i = 0;

         for(int propCount = props.length; p.nextToken() != JsonToken.END_ARRAY; ++i) {
            if (i == propCount) {
               if (!this._ignoreAllUnknown && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                  ctxt.reportInputMismatch(this.handledType(), "Unexpected JSON values; expected at most %d properties (in JSON Array)", propCount);
               }

               while(p.nextToken() != JsonToken.END_ARRAY) {
                  p.skipChildren();
               }

               return this.finishBuild(ctxt, builder);
            }

            SettableBeanProperty prop = props[i];
            if (prop != null) {
               try {
                  builder = prop.deserializeSetAndReturn(p, ctxt, builder);
               } catch (Exception var9) {
                  this.wrapAndThrow(var9, builder, prop.getName(), ctxt);
               }
            } else {
               p.skipChildren();
            }
         }

         return this.finishBuild(ctxt, builder);
      }
   }

   public Object deserialize(JsonParser p, DeserializationContext ctxt, Object value) throws IOException {
      return this._delegate.deserialize(p, ctxt, value);
   }

   public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
      return this._deserializeFromNonArray(p, ctxt);
   }

   protected Object _deserializeNonVanilla(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._nonStandardCreation) {
         return this.deserializeFromObjectUsingNonDefault(p, ctxt);
      } else {
         Object builder = this._valueInstantiator.createUsingDefault(ctxt);
         if (this._injectables != null) {
            this.injectValues(ctxt, builder);
         }

         Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
         SettableBeanProperty[] props = this._orderedProperties;
         int i = 0;
         int propCount = props.length;

         while(true) {
            while(p.nextToken() != JsonToken.END_ARRAY) {
               if (i == propCount) {
                  if (!this._ignoreAllUnknown && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                     ctxt.reportWrongTokenException((JsonDeserializer)this, JsonToken.END_ARRAY, "Unexpected JSON value(s); expected at most %d properties (in JSON Array)", propCount);
                  }

                  while(p.nextToken() != JsonToken.END_ARRAY) {
                     p.skipChildren();
                  }

                  return builder;
               }

               SettableBeanProperty prop = props[i];
               ++i;
               if (prop != null && (activeView == null || prop.visibleInView(activeView))) {
                  try {
                     prop.deserializeSetAndReturn(p, ctxt, builder);
                  } catch (Exception var10) {
                     this.wrapAndThrow(var10, builder, prop.getName(), ctxt);
                  }
               } else {
                  p.skipChildren();
               }
            }

            return builder;
         }
      }
   }

   protected final Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException {
      PropertyBasedCreator creator = this._propertyBasedCreator;
      PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
      SettableBeanProperty[] props = this._orderedProperties;
      int propCount = props.length;
      Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
      int i = 0;

      Object builder;
      for(builder = null; p.nextToken() != JsonToken.END_ARRAY; ++i) {
         SettableBeanProperty prop = i < propCount ? props[i] : null;
         if (prop == null) {
            p.skipChildren();
         } else if (activeView != null && !prop.visibleInView(activeView)) {
            p.skipChildren();
         } else if (builder != null) {
            try {
               builder = prop.deserializeSetAndReturn(p, ctxt, builder);
            } catch (Exception var15) {
               this.wrapAndThrow(var15, builder, prop.getName(), ctxt);
            }
         } else {
            String propName = prop.getName();
            SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (creatorProp != null) {
               if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
                  try {
                     builder = creator.build(ctxt, buffer);
                  } catch (Exception var16) {
                     this.wrapAndThrow(var16, this._beanType.getRawClass(), propName, ctxt);
                     continue;
                  }

                  if (builder.getClass() != this._beanType.getRawClass()) {
                     return ctxt.reportBadDefinition(this._beanType, String.format("Cannot support implicit polymorphic deserialization for POJOs-as-Arrays style: nominal type %s, actual type %s", this._beanType.getRawClass().getName(), builder.getClass().getName()));
                  }
               }
            } else if (!buffer.readIdProperty(propName)) {
               buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
            }
         }
      }

      if (builder == null) {
         try {
            builder = creator.build(ctxt, buffer);
         } catch (Exception var14) {
            return this.wrapInstantiationProblem(var14, ctxt);
         }
      }

      return builder;
   }

   protected Object _deserializeFromNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
      return ctxt.handleUnexpectedToken(this.handledType(), p.getCurrentToken(), p, "Cannot deserialize a POJO (of type %s) from non-Array representation (token: %s): type/property designed to be serialized as JSON Array", this._beanType.getRawClass().getName(), p.getCurrentToken());
   }
}
