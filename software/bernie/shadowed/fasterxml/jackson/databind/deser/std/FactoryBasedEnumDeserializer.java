package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ContextualDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ValueInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

class FactoryBasedEnumDeserializer extends StdDeserializer<Object> implements ContextualDeserializer {
   private static final long serialVersionUID = 1L;
   protected final JavaType _inputType;
   protected final boolean _hasArgs;
   protected final AnnotatedMethod _factory;
   protected final JsonDeserializer<?> _deser;
   protected final ValueInstantiator _valueInstantiator;
   protected final SettableBeanProperty[] _creatorProps;
   private transient PropertyBasedCreator _propCreator;

   public FactoryBasedEnumDeserializer(Class<?> cls, AnnotatedMethod f, JavaType paramType, ValueInstantiator valueInstantiator, SettableBeanProperty[] creatorProps) {
      super(cls);
      this._factory = f;
      this._hasArgs = true;
      this._inputType = paramType.hasRawClass(String.class) ? null : paramType;
      this._deser = null;
      this._valueInstantiator = valueInstantiator;
      this._creatorProps = creatorProps;
   }

   public FactoryBasedEnumDeserializer(Class<?> cls, AnnotatedMethod f) {
      super(cls);
      this._factory = f;
      this._hasArgs = false;
      this._inputType = null;
      this._deser = null;
      this._valueInstantiator = null;
      this._creatorProps = null;
   }

   protected FactoryBasedEnumDeserializer(FactoryBasedEnumDeserializer base, JsonDeserializer<?> deser) {
      super(base._valueClass);
      this._inputType = base._inputType;
      this._factory = base._factory;
      this._hasArgs = base._hasArgs;
      this._valueInstantiator = base._valueInstantiator;
      this._creatorProps = base._creatorProps;
      this._deser = deser;
   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      return this._deser == null && this._inputType != null && this._creatorProps == null ? new FactoryBasedEnumDeserializer(this, ctxt.findContextualValueDeserializer(this._inputType, property)) : this;
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return Boolean.FALSE;
   }

   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      Object value = null;
      Throwable t;
      if (this._deser != null) {
         value = this._deser.deserialize(p, ctxt);
      } else {
         if (!this._hasArgs) {
            p.skipChildren();

            try {
               return this._factory.call();
            } catch (Exception var6) {
               t = ClassUtil.throwRootCauseIfIOE(var6);
               return ctxt.handleInstantiationProblem(this._valueClass, (Object)null, t);
            }
         }

         JsonToken curr = p.getCurrentToken();
         if (curr != JsonToken.VALUE_STRING && curr != JsonToken.FIELD_NAME) {
            if (this._creatorProps != null && p.isExpectedStartObjectToken()) {
               if (this._propCreator == null) {
                  this._propCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, this._creatorProps, ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
               }

               p.nextToken();
               return this.deserializeEnumUsingPropertyBased(p, ctxt, this._propCreator);
            }

            value = p.getValueAsString();
         } else {
            value = p.getText();
         }
      }

      try {
         return this._factory.callOnWith(this._valueClass, value);
      } catch (Exception var7) {
         t = ClassUtil.throwRootCauseIfIOE(var7);
         return ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL) && t instanceof IllegalArgumentException ? null : ctxt.handleInstantiationProblem(this._valueClass, value, t);
      }
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return this._deser == null ? this.deserialize(p, ctxt) : typeDeserializer.deserializeTypedFromAny(p, ctxt);
   }

   protected Object deserializeEnumUsingPropertyBased(JsonParser p, DeserializationContext ctxt, PropertyBasedCreator creator) throws IOException {
      PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, (ObjectIdReader)null);

      for(JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
         String propName = p.getCurrentName();
         p.nextToken();
         SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
         if (creatorProp != null) {
            buffer.assignParameter(creatorProp, this._deserializeWithErrorWrapping(p, ctxt, creatorProp));
         } else if (buffer.readIdProperty(propName)) {
         }
      }

      return creator.build(ctxt, buffer);
   }

   protected final Object _deserializeWithErrorWrapping(JsonParser p, DeserializationContext ctxt, SettableBeanProperty prop) throws IOException {
      try {
         return prop.deserialize(p, ctxt);
      } catch (Exception var5) {
         this.wrapAndThrow(var5, this._valueClass.getClass(), prop.getName(), ctxt);
         return null;
      }
   }

   public void wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt) throws IOException {
      throw JsonMappingException.wrapWithPath(this.throwOrReturnThrowable(t, ctxt), bean, fieldName);
   }

   private Throwable throwOrReturnThrowable(Throwable t, DeserializationContext ctxt) throws IOException {
      t = ClassUtil.getRootCause(t);
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
}
