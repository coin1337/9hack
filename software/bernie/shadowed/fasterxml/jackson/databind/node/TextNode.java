package software.bernie.shadowed.fasterxml.jackson.databind.node;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variant;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variants;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.io.CharTypes;
import software.bernie.shadowed.fasterxml.jackson.core.io.NumberInput;
import software.bernie.shadowed.fasterxml.jackson.core.util.ByteArrayBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.exc.InvalidFormatException;

public class TextNode extends ValueNode {
   static final TextNode EMPTY_STRING_NODE = new TextNode("");
   protected final String _value;

   public TextNode(String v) {
      this._value = v;
   }

   public static TextNode valueOf(String v) {
      if (v == null) {
         return null;
      } else {
         return v.length() == 0 ? EMPTY_STRING_NODE : new TextNode(v);
      }
   }

   public JsonNodeType getNodeType() {
      return JsonNodeType.STRING;
   }

   public JsonToken asToken() {
      return JsonToken.VALUE_STRING;
   }

   public String textValue() {
      return this._value;
   }

   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
      String str = this._value.trim();
      ByteArrayBuilder builder = new ByteArrayBuilder(4 + (str.length() * 3 << 2));

      try {
         b64variant.decode(str, builder);
      } catch (IllegalArgumentException var5) {
         throw InvalidFormatException.from((JsonParser)null, String.format("Cannot access contents of TextNode as binary due to broken Base64 encoding: %s", var5.getMessage()), str, byte[].class);
      }

      return builder.toByteArray();
   }

   public byte[] binaryValue() throws IOException {
      return this.getBinaryValue(Base64Variants.getDefaultVariant());
   }

   public String asText() {
      return this._value;
   }

   public String asText(String defaultValue) {
      return this._value == null ? defaultValue : this._value;
   }

   public boolean asBoolean(boolean defaultValue) {
      if (this._value != null) {
         String v = this._value.trim();
         if ("true".equals(v)) {
            return true;
         }

         if ("false".equals(v)) {
            return false;
         }
      }

      return defaultValue;
   }

   public int asInt(int defaultValue) {
      return NumberInput.parseAsInt(this._value, defaultValue);
   }

   public long asLong(long defaultValue) {
      return NumberInput.parseAsLong(this._value, defaultValue);
   }

   public double asDouble(double defaultValue) {
      return NumberInput.parseAsDouble(this._value, defaultValue);
   }

   public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
      if (this._value == null) {
         g.writeNull();
      } else {
         g.writeString(this._value);
      }

   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else {
         return o instanceof TextNode ? ((TextNode)o)._value.equals(this._value) : false;
      }
   }

   public int hashCode() {
      return this._value.hashCode();
   }

   public String toString() {
      int len = this._value.length();
      len = len + 2 + (len >> 4);
      StringBuilder sb = new StringBuilder(len);
      appendQuoted(sb, this._value);
      return sb.toString();
   }

   protected static void appendQuoted(StringBuilder sb, String content) {
      sb.append('"');
      CharTypes.appendQuoted(sb, content);
      sb.append('"');
   }
}
