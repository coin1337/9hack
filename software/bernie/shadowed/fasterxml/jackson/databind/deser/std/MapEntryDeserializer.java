package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ContextualDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;

@JacksonStdImpl
public class MapEntryDeserializer extends ContainerDeserializerBase<Entry<Object, Object>> implements ContextualDeserializer {
   private static final long serialVersionUID = 1L;
   protected final KeyDeserializer _keyDeserializer;
   protected final JsonDeserializer<Object> _valueDeserializer;
   protected final TypeDeserializer _valueTypeDeserializer;

   public MapEntryDeserializer(JavaType type, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser) {
      super(type);
      if (type.containedTypeCount() != 2) {
         throw new IllegalArgumentException("Missing generic type information for " + type);
      } else {
         this._keyDeserializer = keyDeser;
         this._valueDeserializer = valueDeser;
         this._valueTypeDeserializer = valueTypeDeser;
      }
   }

   protected MapEntryDeserializer(MapEntryDeserializer src) {
      super((ContainerDeserializerBase)src);
      this._keyDeserializer = src._keyDeserializer;
      this._valueDeserializer = src._valueDeserializer;
      this._valueTypeDeserializer = src._valueTypeDeserializer;
   }

   protected MapEntryDeserializer(MapEntryDeserializer src, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser) {
      super((ContainerDeserializerBase)src);
      this._keyDeserializer = keyDeser;
      this._valueDeserializer = valueDeser;
      this._valueTypeDeserializer = valueTypeDeser;
   }

   protected MapEntryDeserializer withResolved(KeyDeserializer keyDeser, TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser) {
      return this._keyDeserializer == keyDeser && this._valueDeserializer == valueDeser && this._valueTypeDeserializer == valueTypeDeser ? this : new MapEntryDeserializer(this, keyDeser, valueDeser, valueTypeDeser);
   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      KeyDeserializer kd = this._keyDeserializer;
      if (kd == null) {
         kd = ctxt.findKeyDeserializer(this._containerType.containedType(0), property);
      } else if (kd instanceof ContextualKeyDeserializer) {
         kd = ((ContextualKeyDeserializer)kd).createContextual(ctxt, property);
      }

      JsonDeserializer<?> vd = this._valueDeserializer;
      vd = this.findConvertingContentDeserializer(ctxt, property, vd);
      JavaType contentType = this._containerType.containedType(1);
      if (vd == null) {
         vd = ctxt.findContextualValueDeserializer(contentType, property);
      } else {
         vd = ctxt.handleSecondaryContextualization(vd, property, contentType);
      }

      TypeDeserializer vtd = this._valueTypeDeserializer;
      if (vtd != null) {
         vtd = vtd.forProperty(property);
      }

      return this.withResolved(kd, vtd, vd);
   }

   public JavaType getContentType() {
      return this._containerType.containedType(1);
   }

   public JsonDeserializer<Object> getContentDeserializer() {
      return this._valueDeserializer;
   }

   public Entry<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t = p.getCurrentToken();
      if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME && t != JsonToken.END_OBJECT) {
         return (Entry)this._deserializeFromEmpty(p, ctxt);
      } else {
         if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
         }

         if (t != JsonToken.FIELD_NAME) {
            return t == JsonToken.END_OBJECT ? (Entry)ctxt.reportInputMismatch((JsonDeserializer)this, "Cannot deserialize a Map.Entry out of empty JSON Object") : (Entry)ctxt.handleUnexpectedToken(this.handledType(), p);
         } else {
            KeyDeserializer keyDes = this._keyDeserializer;
            JsonDeserializer<Object> valueDes = this._valueDeserializer;
            TypeDeserializer typeDeser = this._valueTypeDeserializer;
            String keyStr = p.getCurrentName();
            Object key = keyDes.deserializeKey(keyStr, ctxt);
            Object value = null;
            t = p.nextToken();

            try {
               if (t == JsonToken.VALUE_NULL) {
                  value = valueDes.getNullValue(ctxt);
               } else if (typeDeser == null) {
                  value = valueDes.deserialize(p, ctxt);
               } else {
                  value = valueDes.deserializeWithType(p, ctxt, typeDeser);
               }
            } catch (Exception var11) {
               this.wrapAndThrow(var11, Entry.class, keyStr);
            }

            t = p.nextToken();
            if (t != JsonToken.END_OBJECT) {
               if (t == JsonToken.FIELD_NAME) {
                  ctxt.reportInputMismatch((JsonDeserializer)this, "Problem binding JSON into Map.Entry: more than one entry in JSON (second field: '%s')", p.getCurrentName());
               } else {
                  ctxt.reportInputMismatch((JsonDeserializer)this, "Problem binding JSON into Map.Entry: unexpected content after JSON Object entry: " + t);
               }

               return null;
            } else {
               return new SimpleEntry(key, value);
            }
         }
      }
   }

   public Entry<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt, Entry<Object, Object> result) throws IOException {
      throw new IllegalStateException("Cannot update Map.Entry values");
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromObject(p, ctxt);
   }
}
