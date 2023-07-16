package software.bernie.shadowed.fasterxml.jackson.databind.jsontype.impl;

import java.io.IOException;
import java.io.Serializable;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonTypeInfo;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.ObjectCodec;
import software.bernie.shadowed.fasterxml.jackson.core.util.JsonParserSequence;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.util.TokenBuffer;

public class AsWrapperTypeDeserializer extends TypeDeserializerBase implements Serializable {
   private static final long serialVersionUID = 1L;

   public AsWrapperTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl) {
      super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
   }

   protected AsWrapperTypeDeserializer(AsWrapperTypeDeserializer src, BeanProperty property) {
      super(src, property);
   }

   public TypeDeserializer forProperty(BeanProperty prop) {
      return prop == this._property ? this : new AsWrapperTypeDeserializer(this, prop);
   }

   public JsonTypeInfo.As getTypeInclusion() {
      return JsonTypeInfo.As.WRAPPER_OBJECT;
   }

   public Object deserializeTypedFromObject(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return this._deserialize(jp, ctxt);
   }

   public Object deserializeTypedFromArray(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return this._deserialize(jp, ctxt);
   }

   public Object deserializeTypedFromScalar(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return this._deserialize(jp, ctxt);
   }

   public Object deserializeTypedFromAny(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return this._deserialize(jp, ctxt);
   }

   protected Object _deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (((JsonParser)p).canReadTypeId()) {
         Object typeId = ((JsonParser)p).getTypeId();
         if (typeId != null) {
            return this._deserializeWithNativeTypeId((JsonParser)p, ctxt, typeId);
         }
      }

      JsonToken t = ((JsonParser)p).getCurrentToken();
      if (t == JsonToken.START_OBJECT) {
         if (((JsonParser)p).nextToken() != JsonToken.FIELD_NAME) {
            ctxt.reportWrongTokenException(this.baseType(), JsonToken.FIELD_NAME, "need JSON String that contains type id (for subtype of " + this.baseTypeName() + ")");
         }
      } else if (t != JsonToken.FIELD_NAME) {
         ctxt.reportWrongTokenException(this.baseType(), JsonToken.START_OBJECT, "need JSON Object to contain As.WRAPPER_OBJECT type information for class " + this.baseTypeName());
      }

      String typeId = ((JsonParser)p).getText();
      JsonDeserializer<Object> deser = this._findDeserializer(ctxt, typeId);
      ((JsonParser)p).nextToken();
      if (this._typeIdVisible && ((JsonParser)p).getCurrentToken() == JsonToken.START_OBJECT) {
         TokenBuffer tb = new TokenBuffer((ObjectCodec)null, false);
         tb.writeStartObject();
         tb.writeFieldName(this._typePropertyName);
         tb.writeString(typeId);
         ((JsonParser)p).clearCurrentToken();
         p = JsonParserSequence.createFlattened(false, tb.asParser((JsonParser)p), (JsonParser)p);
         ((JsonParser)p).nextToken();
      }

      Object value = deser.deserialize((JsonParser)p, ctxt);
      if (((JsonParser)p).nextToken() != JsonToken.END_OBJECT) {
         ctxt.reportWrongTokenException(this.baseType(), JsonToken.END_OBJECT, "expected closing END_OBJECT after type information and deserialized value");
      }

      return value;
   }
}
