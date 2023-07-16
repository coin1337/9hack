package software.bernie.shadowed.fasterxml.jackson.databind.jsontype.impl;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonTypeInfo;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.util.JsonParserSequence;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.util.TokenBuffer;

public class AsPropertyTypeDeserializer extends AsArrayTypeDeserializer {
   private static final long serialVersionUID = 1L;
   protected final JsonTypeInfo.As _inclusion;

   public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl) {
      this(bt, idRes, typePropertyName, typeIdVisible, defaultImpl, JsonTypeInfo.As.PROPERTY);
   }

   public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl, JsonTypeInfo.As inclusion) {
      super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
      this._inclusion = inclusion;
   }

   public AsPropertyTypeDeserializer(AsPropertyTypeDeserializer src, BeanProperty property) {
      super(src, property);
      this._inclusion = src._inclusion;
   }

   public TypeDeserializer forProperty(BeanProperty prop) {
      return prop == this._property ? this : new AsPropertyTypeDeserializer(this, prop);
   }

   public JsonTypeInfo.As getTypeInclusion() {
      return this._inclusion;
   }

   public Object deserializeTypedFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.canReadTypeId()) {
         Object typeId = p.getTypeId();
         if (typeId != null) {
            return this._deserializeWithNativeTypeId(p, ctxt, typeId);
         }
      }

      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.START_OBJECT) {
         t = p.nextToken();
      } else if (t != JsonToken.FIELD_NAME) {
         return this._deserializeTypedUsingDefaultImpl(p, ctxt, (TokenBuffer)null);
      }

      TokenBuffer tb;
      for(tb = null; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
         String name = p.getCurrentName();
         p.nextToken();
         if (name.equals(this._typePropertyName)) {
            return this._deserializeTypedForId(p, ctxt, tb);
         }

         if (tb == null) {
            tb = new TokenBuffer(p, ctxt);
         }

         tb.writeFieldName(name);
         tb.copyCurrentStructure(p);
      }

      return this._deserializeTypedUsingDefaultImpl(p, ctxt, tb);
   }

   protected Object _deserializeTypedForId(JsonParser p, DeserializationContext ctxt, TokenBuffer tb) throws IOException {
      String typeId = ((JsonParser)p).getText();
      JsonDeserializer<Object> deser = this._findDeserializer(ctxt, typeId);
      if (this._typeIdVisible) {
         if (tb == null) {
            tb = new TokenBuffer((JsonParser)p, ctxt);
         }

         tb.writeFieldName(((JsonParser)p).getCurrentName());
         tb.writeString(typeId);
      }

      if (tb != null) {
         ((JsonParser)p).clearCurrentToken();
         p = JsonParserSequence.createFlattened(false, tb.asParser((JsonParser)p), (JsonParser)p);
      }

      ((JsonParser)p).nextToken();
      return deser.deserialize((JsonParser)p, ctxt);
   }

   protected Object _deserializeTypedUsingDefaultImpl(JsonParser p, DeserializationContext ctxt, TokenBuffer tb) throws IOException {
      JsonDeserializer<Object> deser = this._findDefaultImplDeserializer(ctxt);
      if (deser == null) {
         Object result = TypeDeserializer.deserializeIfNatural(p, ctxt, this._baseType);
         if (result != null) {
            return result;
         }

         if (p.isExpectedStartArrayToken()) {
            return super.deserializeTypedFromAny(p, ctxt);
         }

         String msg;
         if (p.hasToken(JsonToken.VALUE_STRING) && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            msg = p.getText().trim();
            if (msg.isEmpty()) {
               return null;
            }
         }

         msg = String.format("missing type id property '%s'", this._typePropertyName);
         if (this._property != null) {
            msg = String.format("%s (for POJO property '%s')", msg, this._property.getName());
         }

         JavaType t = this._handleMissingTypeId(ctxt, msg);
         if (t == null) {
            return null;
         }

         deser = ctxt.findContextualValueDeserializer(t, this._property);
      }

      if (tb != null) {
         tb.writeEndObject();
         p = tb.asParser(p);
         p.nextToken();
      }

      return deser.deserialize(p, ctxt);
   }

   public Object deserializeTypedFromAny(JsonParser p, DeserializationContext ctxt) throws IOException {
      return p.getCurrentToken() == JsonToken.START_ARRAY ? super.deserializeTypedFromArray(p, ctxt) : this.deserializeTypedFromObject(p, ctxt);
   }
}
