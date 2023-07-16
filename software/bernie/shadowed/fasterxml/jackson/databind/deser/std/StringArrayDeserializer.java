package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ContextualDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.NullValueProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.AccessPattern;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ObjectBuffer;

@JacksonStdImpl
public final class StringArrayDeserializer extends StdDeserializer<String[]> implements ContextualDeserializer {
   private static final long serialVersionUID = 2L;
   private static final String[] NO_STRINGS = new String[0];
   public static final StringArrayDeserializer instance = new StringArrayDeserializer();
   protected JsonDeserializer<String> _elementDeserializer;
   protected final NullValueProvider _nullProvider;
   protected final Boolean _unwrapSingle;
   protected final boolean _skipNullValues;

   public StringArrayDeserializer() {
      this((JsonDeserializer)null, (NullValueProvider)null, (Boolean)null);
   }

   protected StringArrayDeserializer(JsonDeserializer<?> deser, NullValueProvider nuller, Boolean unwrapSingle) {
      super(String[].class);
      this._elementDeserializer = deser;
      this._nullProvider = nuller;
      this._unwrapSingle = unwrapSingle;
      this._skipNullValues = NullsConstantProvider.isSkipper(nuller);
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return Boolean.TRUE;
   }

   public AccessPattern getEmptyAccessPattern() {
      return AccessPattern.CONSTANT;
   }

   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
      return NO_STRINGS;
   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      JsonDeserializer<?> deser = this._elementDeserializer;
      deser = this.findConvertingContentDeserializer(ctxt, property, deser);
      JavaType type = ctxt.constructType(String.class);
      if (deser == null) {
         deser = ctxt.findContextualValueDeserializer(type, property);
      } else {
         deser = ctxt.handleSecondaryContextualization(deser, property, type);
      }

      Boolean unwrapSingle = this.findFormatFeature(ctxt, property, String[].class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      NullValueProvider nuller = this.findContentNullProvider(ctxt, property, deser);
      if (deser != null && this.isDefaultDeserializer(deser)) {
         deser = null;
      }

      return this._elementDeserializer == deser && this._unwrapSingle == unwrapSingle && this._nullProvider == nuller ? this : new StringArrayDeserializer(deser, nuller, unwrapSingle);
   }

   public String[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (!p.isExpectedStartArrayToken()) {
         return this.handleNonArray(p, ctxt);
      } else if (this._elementDeserializer != null) {
         return this._deserializeCustom(p, ctxt, (String[])null);
      } else {
         ObjectBuffer buffer = ctxt.leaseObjectBuffer();
         Object[] chunk = buffer.resetAndStart();
         int ix = 0;

         try {
            while(true) {
               String value = p.nextTextValue();
               if (value == null) {
                  JsonToken t = p.getCurrentToken();
                  if (t == JsonToken.END_ARRAY) {
                     break;
                  }

                  if (t == JsonToken.VALUE_NULL) {
                     if (this._skipNullValues) {
                        continue;
                     }

                     value = (String)this._nullProvider.getNullValue(ctxt);
                  } else {
                     value = this._parseString(p, ctxt);
                  }
               }

               if (ix >= chunk.length) {
                  chunk = buffer.appendCompletedChunk(chunk);
                  ix = 0;
               }

               chunk[ix++] = value;
            }
         } catch (Exception var8) {
            throw JsonMappingException.wrapWithPath(var8, chunk, buffer.bufferedSize() + ix);
         }

         String[] result = (String[])buffer.completeAndClearBuffer(chunk, ix, String.class);
         ctxt.returnObjectBuffer(buffer);
         return result;
      }
   }

   protected final String[] _deserializeCustom(JsonParser p, DeserializationContext ctxt, String[] old) throws IOException {
      ObjectBuffer buffer = ctxt.leaseObjectBuffer();
      int ix;
      Object[] chunk;
      if (old == null) {
         ix = 0;
         chunk = buffer.resetAndStart();
      } else {
         ix = old.length;
         chunk = buffer.resetAndStart(old, ix);
      }

      JsonDeserializer deser = this._elementDeserializer;

      try {
         while(true) {
            String value;
            if (p.nextTextValue() == null) {
               JsonToken t = p.getCurrentToken();
               if (t == JsonToken.END_ARRAY) {
                  break;
               }

               if (t == JsonToken.VALUE_NULL) {
                  if (this._skipNullValues) {
                     continue;
                  }

                  value = (String)this._nullProvider.getNullValue(ctxt);
               } else {
                  value = (String)deser.deserialize(p, ctxt);
               }
            } else {
               value = (String)deser.deserialize(p, ctxt);
            }

            if (ix >= chunk.length) {
               chunk = buffer.appendCompletedChunk(chunk);
               ix = 0;
            }

            chunk[ix++] = value;
         }
      } catch (Exception var10) {
         throw JsonMappingException.wrapWithPath(var10, String.class, ix);
      }

      String[] result = (String[])buffer.completeAndClearBuffer(chunk, ix, String.class);
      ctxt.returnObjectBuffer(buffer);
      return result;
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromArray(p, ctxt);
   }

   public String[] deserialize(JsonParser p, DeserializationContext ctxt, String[] intoValue) throws IOException {
      int ix;
      if (!p.isExpectedStartArrayToken()) {
         String[] arr = this.handleNonArray(p, ctxt);
         if (arr == null) {
            return intoValue;
         } else {
            ix = intoValue.length;
            String[] result = new String[ix + arr.length];
            System.arraycopy(intoValue, 0, result, 0, ix);
            System.arraycopy(arr, 0, result, ix, arr.length);
            return result;
         }
      } else if (this._elementDeserializer != null) {
         return this._deserializeCustom(p, ctxt, intoValue);
      } else {
         ObjectBuffer buffer = ctxt.leaseObjectBuffer();
         ix = intoValue.length;
         Object[] chunk = buffer.resetAndStart(intoValue, ix);

         try {
            while(true) {
               String value = p.nextTextValue();
               if (value == null) {
                  JsonToken t = p.getCurrentToken();
                  if (t == JsonToken.END_ARRAY) {
                     break;
                  }

                  if (t == JsonToken.VALUE_NULL) {
                     if (this._skipNullValues) {
                        return NO_STRINGS;
                     }

                     value = (String)this._nullProvider.getNullValue(ctxt);
                  } else {
                     value = this._parseString(p, ctxt);
                  }
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

         String[] result = (String[])buffer.completeAndClearBuffer(chunk, ix, String.class);
         ctxt.returnObjectBuffer(buffer);
         return result;
      }
   }

   private final String[] handleNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
      boolean canWrap = this._unwrapSingle == Boolean.TRUE || this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      String str;
      if (canWrap) {
         str = p.hasToken(JsonToken.VALUE_NULL) ? (String)this._nullProvider.getNullValue(ctxt) : this._parseString(p, ctxt);
         return new String[]{str};
      } else {
         if (p.hasToken(JsonToken.VALUE_STRING) && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            str = p.getText();
            if (str.length() == 0) {
               return null;
            }
         }

         return (String[])((String[])ctxt.handleUnexpectedToken(this._valueClass, p));
      }
   }
}
