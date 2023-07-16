package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import java.util.EnumSet;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
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
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ContextualDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;

public class EnumSetDeserializer extends StdDeserializer<EnumSet<?>> implements ContextualDeserializer {
   private static final long serialVersionUID = 1L;
   protected final JavaType _enumType;
   protected final Class<Enum> _enumClass;
   protected JsonDeserializer<Enum<?>> _enumDeserializer;
   protected final Boolean _unwrapSingle;

   public EnumSetDeserializer(JavaType enumType, JsonDeserializer<?> deser) {
      super(EnumSet.class);
      this._enumType = enumType;
      this._enumClass = enumType.getRawClass();
      if (!this._enumClass.isEnum()) {
         throw new IllegalArgumentException("Type " + enumType + " not Java Enum type");
      } else {
         this._enumDeserializer = deser;
         this._unwrapSingle = null;
      }
   }

   protected EnumSetDeserializer(EnumSetDeserializer base, JsonDeserializer<?> deser, Boolean unwrapSingle) {
      super((StdDeserializer)base);
      this._enumType = base._enumType;
      this._enumClass = base._enumClass;
      this._enumDeserializer = deser;
      this._unwrapSingle = unwrapSingle;
   }

   public EnumSetDeserializer withDeserializer(JsonDeserializer<?> deser) {
      return this._enumDeserializer == deser ? this : new EnumSetDeserializer(this, deser, this._unwrapSingle);
   }

   public EnumSetDeserializer withResolved(JsonDeserializer<?> deser, Boolean unwrapSingle) {
      return this._unwrapSingle == unwrapSingle && this._enumDeserializer == deser ? this : new EnumSetDeserializer(this, deser, unwrapSingle);
   }

   public boolean isCachable() {
      return this._enumType.getValueHandler() == null;
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return Boolean.TRUE;
   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      Boolean unwrapSingle = this.findFormatFeature(ctxt, property, EnumSet.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      JsonDeserializer<?> deser = this._enumDeserializer;
      if (deser == null) {
         deser = ctxt.findContextualValueDeserializer(this._enumType, property);
      } else {
         deser = ctxt.handleSecondaryContextualization(deser, property, this._enumType);
      }

      return this.withResolved(deser, unwrapSingle);
   }

   public EnumSet<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      EnumSet result = this.constructSet();
      return !p.isExpectedStartArrayToken() ? this.handleNonArray(p, ctxt, result) : this._deserialize(p, ctxt, result);
   }

   public EnumSet<?> deserialize(JsonParser p, DeserializationContext ctxt, EnumSet<?> result) throws IOException {
      return !p.isExpectedStartArrayToken() ? this.handleNonArray(p, ctxt, result) : this._deserialize(p, ctxt, result);
   }

   protected final EnumSet<?> _deserialize(JsonParser p, DeserializationContext ctxt, EnumSet result) throws IOException {
      try {
         JsonToken t;
         while((t = p.nextToken()) != JsonToken.END_ARRAY) {
            if (t == JsonToken.VALUE_NULL) {
               return (EnumSet)ctxt.handleUnexpectedToken(this._enumClass, p);
            }

            Enum<?> value = (Enum)this._enumDeserializer.deserialize(p, ctxt);
            if (value != null) {
               result.add(value);
            }
         }

         return result;
      } catch (Exception var6) {
         throw JsonMappingException.wrapWithPath(var6, result, result.size());
      }
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
      return typeDeserializer.deserializeTypedFromArray(p, ctxt);
   }

   private EnumSet constructSet() {
      return EnumSet.noneOf(this._enumClass);
   }

   protected EnumSet<?> handleNonArray(JsonParser p, DeserializationContext ctxt, EnumSet result) throws IOException {
      boolean canWrap = this._unwrapSingle == Boolean.TRUE || this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      if (!canWrap) {
         return (EnumSet)ctxt.handleUnexpectedToken(EnumSet.class, p);
      } else if (p.hasToken(JsonToken.VALUE_NULL)) {
         return (EnumSet)ctxt.handleUnexpectedToken(this._enumClass, p);
      } else {
         try {
            Enum<?> value = (Enum)this._enumDeserializer.deserialize(p, ctxt);
            if (value != null) {
               result.add(value);
            }

            return result;
         } catch (Exception var6) {
            throw JsonMappingException.wrapWithPath(var6, result, result.size());
         }
      }
   }
}
