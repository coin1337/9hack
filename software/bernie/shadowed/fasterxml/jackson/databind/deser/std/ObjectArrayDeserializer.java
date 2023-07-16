package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import java.lang.reflect.Array;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ContextualDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.NullValueProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.AccessPattern;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ObjectBuffer;

@JacksonStdImpl
public class ObjectArrayDeserializer extends ContainerDeserializerBase<Object[]> implements ContextualDeserializer {
   private static final long serialVersionUID = 1L;
   protected static final Object[] NO_OBJECTS = new Object[0];
   protected final boolean _untyped;
   protected final Class<?> _elementClass;
   protected JsonDeserializer<Object> _elementDeserializer;
   protected final TypeDeserializer _elementTypeDeserializer;

   public ObjectArrayDeserializer(JavaType arrayType, JsonDeserializer<Object> elemDeser, TypeDeserializer elemTypeDeser) {
      super((JavaType)arrayType, (NullValueProvider)null, (Boolean)null);
      this._elementClass = arrayType.getContentType().getRawClass();
      this._untyped = this._elementClass == Object.class;
      this._elementDeserializer = elemDeser;
      this._elementTypeDeserializer = elemTypeDeser;
   }

   protected ObjectArrayDeserializer(ObjectArrayDeserializer base, JsonDeserializer<Object> elemDeser, TypeDeserializer elemTypeDeser, NullValueProvider nuller, Boolean unwrapSingle) {
      super((ContainerDeserializerBase)base, nuller, unwrapSingle);
      this._elementClass = base._elementClass;
      this._untyped = base._untyped;
      this._elementDeserializer = elemDeser;
      this._elementTypeDeserializer = elemTypeDeser;
   }

   public ObjectArrayDeserializer withDeserializer(TypeDeserializer elemTypeDeser, JsonDeserializer<?> elemDeser) {
      return this.withResolved(elemTypeDeser, elemDeser, this._nullProvider, this._unwrapSingle);
   }

   public ObjectArrayDeserializer withResolved(TypeDeserializer elemTypeDeser, JsonDeserializer<?> elemDeser, NullValueProvider nuller, Boolean unwrapSingle) {
      return unwrapSingle == this._unwrapSingle && nuller == this._nullProvider && elemDeser == this._elementDeserializer && elemTypeDeser == this._elementTypeDeserializer ? this : new ObjectArrayDeserializer(this, elemDeser, elemTypeDeser, nuller, unwrapSingle);
   }

   public boolean isCachable() {
      return this._elementDeserializer == null && this._elementTypeDeserializer == null;
   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      JsonDeserializer<?> valueDeser = this._elementDeserializer;
      Boolean unwrapSingle = this.findFormatFeature(ctxt, property, this._containerType.getRawClass(), JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      valueDeser = this.findConvertingContentDeserializer(ctxt, property, valueDeser);
      JavaType vt = this._containerType.getContentType();
      if (valueDeser == null) {
         valueDeser = ctxt.findContextualValueDeserializer(vt, property);
      } else {
         valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
      }

      TypeDeserializer elemTypeDeser = this._elementTypeDeserializer;
      if (elemTypeDeser != null) {
         elemTypeDeser = elemTypeDeser.forProperty(property);
      }

      NullValueProvider nuller = this.findContentNullProvider(ctxt, property, valueDeser);
      return this.withResolved(elemTypeDeser, valueDeser, nuller, unwrapSingle);
   }

   public JsonDeserializer<Object> getContentDeserializer() {
      return this._elementDeserializer;
   }

   public AccessPattern getEmptyAccessPattern() {
      return AccessPattern.CONSTANT;
   }

   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
      return NO_OBJECTS;
   }

   public Object[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (!p.isExpectedStartArrayToken()) {
         return this.handleNonArray(p, ctxt);
      } else {
         ObjectBuffer buffer = ctxt.leaseObjectBuffer();
         Object[] chunk = buffer.resetAndStart();
         int ix = 0;
         TypeDeserializer typeDeser = this._elementTypeDeserializer;

         JsonToken t;
         try {
            while((t = p.nextToken()) != JsonToken.END_ARRAY) {
               Object value;
               if (t == JsonToken.VALUE_NULL) {
                  if (this._skipNullValues) {
                     continue;
                  }

                  value = this._nullProvider.getNullValue(ctxt);
               } else if (typeDeser == null) {
                  value = this._elementDeserializer.deserialize(p, ctxt);
               } else {
                  value = this._elementDeserializer.deserializeWithType(p, ctxt, typeDeser);
               }

               if (ix >= chunk.length) {
                  chunk = buffer.appendCompletedChunk(chunk);
                  ix = 0;
               }

               chunk[ix++] = value;
            }
         } catch (Exception var9) {
            throw JsonMappingException.wrapWithPath(var9, chunk, buffer.bufferedSize() + ix);
         }

         Object[] result;
         if (this._untyped) {
            result = buffer.completeAndClearBuffer(chunk, ix);
         } else {
            result = buffer.completeAndClearBuffer(chunk, ix, this._elementClass);
         }

         ctxt.returnObjectBuffer(buffer);
         return result;
      }
   }

   public Object[] deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return (Object[])((Object[])typeDeserializer.deserializeTypedFromArray(p, ctxt));
   }

   public Object[] deserialize(JsonParser p, DeserializationContext ctxt, Object[] intoValue) throws IOException {
      int ix;
      Object[] chunk;
      if (!p.isExpectedStartArrayToken()) {
         Object[] arr = this.handleNonArray(p, ctxt);
         if (arr == null) {
            return intoValue;
         } else {
            ix = intoValue.length;
            chunk = new Object[ix + arr.length];
            System.arraycopy(intoValue, 0, chunk, 0, ix);
            System.arraycopy(arr, 0, chunk, ix, arr.length);
            return chunk;
         }
      } else {
         ObjectBuffer buffer = ctxt.leaseObjectBuffer();
         ix = intoValue.length;
         chunk = buffer.resetAndStart(intoValue, ix);
         TypeDeserializer typeDeser = this._elementTypeDeserializer;

         JsonToken t;
         try {
            while((t = p.nextToken()) != JsonToken.END_ARRAY) {
               Object value;
               if (t == JsonToken.VALUE_NULL) {
                  if (this._skipNullValues) {
                     continue;
                  }

                  value = this._nullProvider.getNullValue(ctxt);
               } else if (typeDeser == null) {
                  value = this._elementDeserializer.deserialize(p, ctxt);
               } else {
                  value = this._elementDeserializer.deserializeWithType(p, ctxt, typeDeser);
               }

               if (ix >= chunk.length) {
                  chunk = buffer.appendCompletedChunk(chunk);
                  ix = 0;
               }

               chunk[ix++] = value;
            }
         } catch (Exception var10) {
            throw JsonMappingException.wrapWithPath(var10, chunk, buffer.bufferedSize() + ix);
         }

         Object[] result;
         if (this._untyped) {
            result = buffer.completeAndClearBuffer(chunk, ix);
         } else {
            result = buffer.completeAndClearBuffer(chunk, ix, this._elementClass);
         }

         ctxt.returnObjectBuffer(buffer);
         return result;
      }
   }

   protected Byte[] deserializeFromBase64(JsonParser p, DeserializationContext ctxt) throws IOException {
      byte[] b = p.getBinaryValue(ctxt.getBase64Variant());
      Byte[] result = new Byte[b.length];
      int i = 0;

      for(int len = b.length; i < len; ++i) {
         result[i] = b[i];
      }

      return result;
   }

   protected Object[] handleNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.hasToken(JsonToken.VALUE_STRING) && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
         String str = p.getText();
         if (str.length() == 0) {
            return null;
         }
      }

      boolean canWrap = this._unwrapSingle == Boolean.TRUE || this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      JsonToken t;
      if (!canWrap) {
         t = p.getCurrentToken();
         return (Object[])(t == JsonToken.VALUE_STRING && this._elementClass == Byte.class ? this.deserializeFromBase64(p, ctxt) : (Object[])((Object[])ctxt.handleUnexpectedToken(this._containerType.getRawClass(), p)));
      } else {
         t = p.getCurrentToken();
         Object value;
         if (t == JsonToken.VALUE_NULL) {
            if (this._skipNullValues) {
               return NO_OBJECTS;
            }

            value = this._nullProvider.getNullValue(ctxt);
         } else if (this._elementTypeDeserializer == null) {
            value = this._elementDeserializer.deserialize(p, ctxt);
         } else {
            value = this._elementDeserializer.deserializeWithType(p, ctxt, this._elementTypeDeserializer);
         }

         Object[] result;
         if (this._untyped) {
            result = new Object[1];
         } else {
            result = (Object[])((Object[])Array.newInstance(this._elementClass, 1));
         }

         result[0] = value;
         return result;
      }
   }
}
