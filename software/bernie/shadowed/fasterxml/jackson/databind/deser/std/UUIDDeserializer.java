package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variants;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.exc.InvalidFormatException;

public class UUIDDeserializer extends FromStringDeserializer<UUID> {
   private static final long serialVersionUID = 1L;
   static final int[] HEX_DIGITS = new int[127];

   public UUIDDeserializer() {
      super(UUID.class);
   }

   protected UUID _deserialize(String id, DeserializationContext ctxt) throws IOException {
      if (id.length() != 36) {
         if (id.length() == 24) {
            byte[] stuff = Base64Variants.getDefaultVariant().decode(id);
            return this._fromBytes(stuff, ctxt);
         } else {
            return this._badFormat(id, ctxt);
         }
      } else {
         if (id.charAt(8) != '-' || id.charAt(13) != '-' || id.charAt(18) != '-' || id.charAt(23) != '-') {
            this._badFormat(id, ctxt);
         }

         long l1 = (long)this.intFromChars(id, 0, ctxt);
         l1 <<= 32;
         long l2 = (long)this.shortFromChars(id, 9, ctxt) << 16;
         l2 |= (long)this.shortFromChars(id, 14, ctxt);
         long hi = l1 + l2;
         int i1 = this.shortFromChars(id, 19, ctxt) << 16 | this.shortFromChars(id, 24, ctxt);
         l1 = (long)i1;
         l1 <<= 32;
         l2 = (long)this.intFromChars(id, 28, ctxt);
         l2 = l2 << 32 >>> 32;
         long lo = l1 | l2;
         return new UUID(hi, lo);
      }
   }

   protected UUID _deserializeEmbedded(Object ob, DeserializationContext ctxt) throws IOException {
      if (ob instanceof byte[]) {
         return this._fromBytes((byte[])((byte[])ob), ctxt);
      } else {
         super._deserializeEmbedded(ob, ctxt);
         return null;
      }
   }

   private UUID _badFormat(String uuidStr, DeserializationContext ctxt) throws IOException {
      return (UUID)ctxt.handleWeirdStringValue(this.handledType(), uuidStr, "UUID has to be represented by standard 36-char representation");
   }

   int intFromChars(String str, int index, DeserializationContext ctxt) throws JsonMappingException {
      return (this.byteFromChars(str, index, ctxt) << 24) + (this.byteFromChars(str, index + 2, ctxt) << 16) + (this.byteFromChars(str, index + 4, ctxt) << 8) + this.byteFromChars(str, index + 6, ctxt);
   }

   int shortFromChars(String str, int index, DeserializationContext ctxt) throws JsonMappingException {
      return (this.byteFromChars(str, index, ctxt) << 8) + this.byteFromChars(str, index + 2, ctxt);
   }

   int byteFromChars(String str, int index, DeserializationContext ctxt) throws JsonMappingException {
      char c1 = str.charAt(index);
      char c2 = str.charAt(index + 1);
      if (c1 <= 127 && c2 <= 127) {
         int hex = HEX_DIGITS[c1] << 4 | HEX_DIGITS[c2];
         if (hex >= 0) {
            return hex;
         }
      }

      return c1 <= 127 && HEX_DIGITS[c1] >= 0 ? this._badChar(str, index + 1, ctxt, c2) : this._badChar(str, index, ctxt, c1);
   }

   int _badChar(String uuidStr, int index, DeserializationContext ctxt, char c) throws JsonMappingException {
      throw ctxt.weirdStringException(uuidStr, this.handledType(), String.format("Non-hex character '%c' (value 0x%s), not valid for UUID String", c, Integer.toHexString(c)));
   }

   private UUID _fromBytes(byte[] bytes, DeserializationContext ctxt) throws JsonMappingException {
      if (bytes.length != 16) {
         throw InvalidFormatException.from(ctxt.getParser(), "Can only construct UUIDs from byte[16]; got " + bytes.length + " bytes", bytes, this.handledType());
      } else {
         return new UUID(_long(bytes, 0), _long(bytes, 8));
      }
   }

   private static long _long(byte[] b, int offset) {
      long l1 = (long)_int(b, offset) << 32;
      long l2 = (long)_int(b, offset + 4);
      l2 = l2 << 32 >>> 32;
      return l1 | l2;
   }

   private static int _int(byte[] b, int offset) {
      return b[offset] << 24 | (b[offset + 1] & 255) << 16 | (b[offset + 2] & 255) << 8 | b[offset + 3] & 255;
   }

   static {
      Arrays.fill(HEX_DIGITS, -1);

      int i;
      for(i = 0; i < 10; HEX_DIGITS[48 + i] = i++) {
      }

      for(i = 0; i < 6; ++i) {
         HEX_DIGITS[97 + i] = 10 + i;
         HEX_DIGITS[65 + i] = 10 + i;
      }

   }
}
