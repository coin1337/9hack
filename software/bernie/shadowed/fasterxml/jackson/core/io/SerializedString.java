package software.bernie.shadowed.fasterxml.jackson.core.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import software.bernie.shadowed.fasterxml.jackson.core.SerializableString;

public class SerializedString implements SerializableString, Serializable {
   private static final long serialVersionUID = 1L;
   protected final String _value;
   protected byte[] _quotedUTF8Ref;
   protected byte[] _unquotedUTF8Ref;
   protected char[] _quotedChars;
   protected transient String _jdkSerializeValue;

   public SerializedString(String v) {
      if (v == null) {
         throw new IllegalStateException("Null String illegal for SerializedString");
      } else {
         this._value = v;
      }
   }

   private void readObject(ObjectInputStream in) throws IOException {
      this._jdkSerializeValue = in.readUTF();
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.writeUTF(this._value);
   }

   protected Object readResolve() {
      return new SerializedString(this._jdkSerializeValue);
   }

   public final String getValue() {
      return this._value;
   }

   public final int charLength() {
      return this._value.length();
   }

   public final char[] asQuotedChars() {
      char[] result = this._quotedChars;
      if (result == null) {
         result = JsonStringEncoder.getInstance().quoteAsString(this._value);
         this._quotedChars = result;
      }

      return result;
   }

   public final byte[] asUnquotedUTF8() {
      byte[] result = this._unquotedUTF8Ref;
      if (result == null) {
         result = JsonStringEncoder.getInstance().encodeAsUTF8(this._value);
         this._unquotedUTF8Ref = result;
      }

      return result;
   }

   public final byte[] asQuotedUTF8() {
      byte[] result = this._quotedUTF8Ref;
      if (result == null) {
         result = JsonStringEncoder.getInstance().quoteAsUTF8(this._value);
         this._quotedUTF8Ref = result;
      }

      return result;
   }

   public int appendQuotedUTF8(byte[] buffer, int offset) {
      byte[] result = this._quotedUTF8Ref;
      if (result == null) {
         result = JsonStringEncoder.getInstance().quoteAsUTF8(this._value);
         this._quotedUTF8Ref = result;
      }

      int length = result.length;
      if (offset + length > buffer.length) {
         return -1;
      } else {
         System.arraycopy(result, 0, buffer, offset, length);
         return length;
      }
   }

   public int appendQuoted(char[] buffer, int offset) {
      char[] result = this._quotedChars;
      if (result == null) {
         result = JsonStringEncoder.getInstance().quoteAsString(this._value);
         this._quotedChars = result;
      }

      int length = result.length;
      if (offset + length > buffer.length) {
         return -1;
      } else {
         System.arraycopy(result, 0, buffer, offset, length);
         return length;
      }
   }

   public int appendUnquotedUTF8(byte[] buffer, int offset) {
      byte[] result = this._unquotedUTF8Ref;
      if (result == null) {
         result = JsonStringEncoder.getInstance().encodeAsUTF8(this._value);
         this._unquotedUTF8Ref = result;
      }

      int length = result.length;
      if (offset + length > buffer.length) {
         return -1;
      } else {
         System.arraycopy(result, 0, buffer, offset, length);
         return length;
      }
   }

   public int appendUnquoted(char[] buffer, int offset) {
      String str = this._value;
      int length = str.length();
      if (offset + length > buffer.length) {
         return -1;
      } else {
         str.getChars(0, length, buffer, offset);
         return length;
      }
   }

   public int writeQuotedUTF8(OutputStream out) throws IOException {
      byte[] result = this._quotedUTF8Ref;
      if (result == null) {
         result = JsonStringEncoder.getInstance().quoteAsUTF8(this._value);
         this._quotedUTF8Ref = result;
      }

      int length = result.length;
      out.write(result, 0, length);
      return length;
   }

   public int writeUnquotedUTF8(OutputStream out) throws IOException {
      byte[] result = this._unquotedUTF8Ref;
      if (result == null) {
         result = JsonStringEncoder.getInstance().encodeAsUTF8(this._value);
         this._unquotedUTF8Ref = result;
      }

      int length = result.length;
      out.write(result, 0, length);
      return length;
   }

   public int putQuotedUTF8(ByteBuffer buffer) {
      byte[] result = this._quotedUTF8Ref;
      if (result == null) {
         result = JsonStringEncoder.getInstance().quoteAsUTF8(this._value);
         this._quotedUTF8Ref = result;
      }

      int length = result.length;
      if (length > buffer.remaining()) {
         return -1;
      } else {
         buffer.put(result, 0, length);
         return length;
      }
   }

   public int putUnquotedUTF8(ByteBuffer buffer) {
      byte[] result = this._unquotedUTF8Ref;
      if (result == null) {
         result = JsonStringEncoder.getInstance().encodeAsUTF8(this._value);
         this._unquotedUTF8Ref = result;
      }

      int length = result.length;
      if (length > buffer.remaining()) {
         return -1;
      } else {
         buffer.put(result, 0, length);
         return length;
      }
   }

   public final String toString() {
      return this._value;
   }

   public final int hashCode() {
      return this._value.hashCode();
   }

   public final boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o != null && o.getClass() == this.getClass()) {
         SerializedString other = (SerializedString)o;
         return this._value.equals(other._value);
      } else {
         return false;
      }
   }
}
