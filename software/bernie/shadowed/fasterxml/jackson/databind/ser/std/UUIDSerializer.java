package software.bernie.shadowed.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import java.util.UUID;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.util.TokenBuffer;

public class UUIDSerializer extends StdScalarSerializer<UUID> {
   static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

   public UUIDSerializer() {
      super(UUID.class);
   }

   public boolean isEmpty(SerializerProvider prov, UUID value) {
      return value.getLeastSignificantBits() == 0L && value.getMostSignificantBits() == 0L;
   }

   public void serialize(UUID value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      if (gen.canWriteBinaryNatively() && !(gen instanceof TokenBuffer)) {
         gen.writeBinary(_asBytes(value));
      } else {
         char[] ch = new char[36];
         long msb = value.getMostSignificantBits();
         _appendInt((int)(msb >> 32), (char[])ch, 0);
         ch[8] = '-';
         int i = (int)msb;
         _appendShort(i >>> 16, ch, 9);
         ch[13] = '-';
         _appendShort(i, ch, 14);
         ch[18] = '-';
         long lsb = value.getLeastSignificantBits();
         _appendShort((int)(lsb >>> 48), ch, 19);
         ch[23] = '-';
         _appendShort((int)(lsb >>> 32), ch, 24);
         _appendInt((int)lsb, (char[])ch, 28);
         gen.writeString(ch, 0, 36);
      }
   }

   private static void _appendInt(int bits, char[] ch, int offset) {
      _appendShort(bits >> 16, ch, offset);
      _appendShort(bits, ch, offset + 4);
   }

   private static void _appendShort(int bits, char[] ch, int offset) {
      ch[offset] = HEX_CHARS[bits >> 12 & 15];
      ++offset;
      ch[offset] = HEX_CHARS[bits >> 8 & 15];
      ++offset;
      ch[offset] = HEX_CHARS[bits >> 4 & 15];
      ++offset;
      ch[offset] = HEX_CHARS[bits & 15];
   }

   private static final byte[] _asBytes(UUID uuid) {
      byte[] buffer = new byte[16];
      long hi = uuid.getMostSignificantBits();
      long lo = uuid.getLeastSignificantBits();
      _appendInt((int)(hi >> 32), (byte[])buffer, 0);
      _appendInt((int)hi, (byte[])buffer, 4);
      _appendInt((int)(lo >> 32), (byte[])buffer, 8);
      _appendInt((int)lo, (byte[])buffer, 12);
      return buffer;
   }

   private static final void _appendInt(int value, byte[] buffer, int offset) {
      buffer[offset] = (byte)(value >> 24);
      ++offset;
      buffer[offset] = (byte)(value >> 16);
      ++offset;
      buffer[offset] = (byte)(value >> 8);
      ++offset;
      buffer[offset] = (byte)value;
   }
}
