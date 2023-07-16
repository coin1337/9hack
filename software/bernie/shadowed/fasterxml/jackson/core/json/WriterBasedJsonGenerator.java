package software.bernie.shadowed.fasterxml.jackson.core.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variant;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerationException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonStreamContext;
import software.bernie.shadowed.fasterxml.jackson.core.ObjectCodec;
import software.bernie.shadowed.fasterxml.jackson.core.SerializableString;
import software.bernie.shadowed.fasterxml.jackson.core.io.CharTypes;
import software.bernie.shadowed.fasterxml.jackson.core.io.CharacterEscapes;
import software.bernie.shadowed.fasterxml.jackson.core.io.IOContext;
import software.bernie.shadowed.fasterxml.jackson.core.io.NumberOutput;

public class WriterBasedJsonGenerator extends JsonGeneratorImpl {
   protected static final int SHORT_WRITE = 32;
   protected static final char[] HEX_CHARS = CharTypes.copyHexChars();
   protected final Writer _writer;
   protected char _quoteChar = '"';
   protected char[] _outputBuffer;
   protected int _outputHead;
   protected int _outputTail;
   protected int _outputEnd;
   protected char[] _entityBuffer;
   protected SerializableString _currentEscape;
   protected char[] _charBuffer;

   public WriterBasedJsonGenerator(IOContext ctxt, int features, ObjectCodec codec, Writer w) {
      super(ctxt, features, codec);
      this._writer = w;
      this._outputBuffer = ctxt.allocConcatBuffer();
      this._outputEnd = this._outputBuffer.length;
   }

   public Object getOutputTarget() {
      return this._writer;
   }

   public int getOutputBuffered() {
      int len = this._outputTail - this._outputHead;
      return Math.max(0, len);
   }

   public boolean canWriteFormattedNumbers() {
      return true;
   }

   public void writeFieldName(String name) throws IOException {
      int status = this._writeContext.writeFieldName(name);
      if (status == 4) {
         this._reportError("Can not write a field name, expecting a value");
      }

      this._writeFieldName(name, status == 1);
   }

   public void writeFieldName(SerializableString name) throws IOException {
      int status = this._writeContext.writeFieldName(name.getValue());
      if (status == 4) {
         this._reportError("Can not write a field name, expecting a value");
      }

      this._writeFieldName(name, status == 1);
   }

   protected final void _writeFieldName(String name, boolean commaBefore) throws IOException {
      if (this._cfgPrettyPrinter != null) {
         this._writePPFieldName(name, commaBefore);
      } else {
         if (this._outputTail + 1 >= this._outputEnd) {
            this._flushBuffer();
         }

         if (commaBefore) {
            this._outputBuffer[this._outputTail++] = ',';
         }

         if (this._cfgUnqNames) {
            this._writeString(name);
         } else {
            this._outputBuffer[this._outputTail++] = this._quoteChar;
            this._writeString(name);
            if (this._outputTail >= this._outputEnd) {
               this._flushBuffer();
            }

            this._outputBuffer[this._outputTail++] = this._quoteChar;
         }
      }
   }

   protected final void _writeFieldName(SerializableString name, boolean commaBefore) throws IOException {
      if (this._cfgPrettyPrinter != null) {
         this._writePPFieldName(name, commaBefore);
      } else {
         if (this._outputTail + 1 >= this._outputEnd) {
            this._flushBuffer();
         }

         if (commaBefore) {
            this._outputBuffer[this._outputTail++] = ',';
         }

         char[] quoted = name.asQuotedChars();
         if (this._cfgUnqNames) {
            this.writeRaw((char[])quoted, 0, quoted.length);
         } else {
            this._outputBuffer[this._outputTail++] = this._quoteChar;
            int qlen = quoted.length;
            if (this._outputTail + qlen + 1 >= this._outputEnd) {
               this.writeRaw((char[])quoted, 0, qlen);
               if (this._outputTail >= this._outputEnd) {
                  this._flushBuffer();
               }

               this._outputBuffer[this._outputTail++] = this._quoteChar;
            } else {
               System.arraycopy(quoted, 0, this._outputBuffer, this._outputTail, qlen);
               this._outputTail += qlen;
               this._outputBuffer[this._outputTail++] = this._quoteChar;
            }

         }
      }
   }

   public void writeStartArray() throws IOException {
      this._verifyValueWrite("start an array");
      this._writeContext = this._writeContext.createChildArrayContext();
      if (this._cfgPrettyPrinter != null) {
         this._cfgPrettyPrinter.writeStartArray(this);
      } else {
         if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputBuffer[this._outputTail++] = '[';
      }

   }

   public void writeEndArray() throws IOException {
      if (!this._writeContext.inArray()) {
         this._reportError("Current context not Array but " + this._writeContext.typeDesc());
      }

      if (this._cfgPrettyPrinter != null) {
         this._cfgPrettyPrinter.writeEndArray(this, this._writeContext.getEntryCount());
      } else {
         if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputBuffer[this._outputTail++] = ']';
      }

      this._writeContext = this._writeContext.clearAndGetParent();
   }

   public void writeStartObject(Object forValue) throws IOException {
      this._verifyValueWrite("start an object");
      JsonWriteContext ctxt = this._writeContext.createChildObjectContext();
      this._writeContext = ctxt;
      if (forValue != null) {
         ctxt.setCurrentValue(forValue);
      }

      if (this._cfgPrettyPrinter != null) {
         this._cfgPrettyPrinter.writeStartObject(this);
      } else {
         if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputBuffer[this._outputTail++] = '{';
      }

   }

   public void writeStartObject() throws IOException {
      this._verifyValueWrite("start an object");
      this._writeContext = this._writeContext.createChildObjectContext();
      if (this._cfgPrettyPrinter != null) {
         this._cfgPrettyPrinter.writeStartObject(this);
      } else {
         if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputBuffer[this._outputTail++] = '{';
      }

   }

   public void writeEndObject() throws IOException {
      if (!this._writeContext.inObject()) {
         this._reportError("Current context not Object but " + this._writeContext.typeDesc());
      }

      if (this._cfgPrettyPrinter != null) {
         this._cfgPrettyPrinter.writeEndObject(this, this._writeContext.getEntryCount());
      } else {
         if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputBuffer[this._outputTail++] = '}';
      }

      this._writeContext = this._writeContext.clearAndGetParent();
   }

   protected final void _writePPFieldName(String name, boolean commaBefore) throws IOException {
      if (commaBefore) {
         this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
      } else {
         this._cfgPrettyPrinter.beforeObjectEntries(this);
      }

      if (this._cfgUnqNames) {
         this._writeString(name);
      } else {
         if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputBuffer[this._outputTail++] = this._quoteChar;
         this._writeString(name);
         if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputBuffer[this._outputTail++] = this._quoteChar;
      }

   }

   protected final void _writePPFieldName(SerializableString name, boolean commaBefore) throws IOException {
      if (commaBefore) {
         this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
      } else {
         this._cfgPrettyPrinter.beforeObjectEntries(this);
      }

      char[] quoted = name.asQuotedChars();
      if (this._cfgUnqNames) {
         this.writeRaw((char[])quoted, 0, quoted.length);
      } else {
         if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputBuffer[this._outputTail++] = this._quoteChar;
         this.writeRaw((char[])quoted, 0, quoted.length);
         if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputBuffer[this._outputTail++] = this._quoteChar;
      }

   }

   public void writeString(String text) throws IOException {
      this._verifyValueWrite("write a string");
      if (text == null) {
         this._writeNull();
      } else {
         if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputBuffer[this._outputTail++] = this._quoteChar;
         this._writeString(text);
         if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputBuffer[this._outputTail++] = this._quoteChar;
      }
   }

   public void writeString(Reader reader, int len) throws IOException {
      this._verifyValueWrite("write a string");
      if (reader == null) {
         this._reportError("null reader");
      }

      int toRead = len >= 0 ? len : Integer.MAX_VALUE;
      char[] buf = this._allocateCopyBuffer();
      if (this._outputTail + len >= this._outputEnd) {
         this._flushBuffer();
      }

      int numRead;
      for(this._outputBuffer[this._outputTail++] = this._quoteChar; toRead > 0; toRead -= numRead) {
         int toReadNow = Math.min(toRead, buf.length);
         numRead = reader.read(buf, 0, toReadNow);
         if (numRead <= 0) {
            break;
         }

         if (this._outputTail + len >= this._outputEnd) {
            this._flushBuffer();
         }

         this._writeString(buf, 0, numRead);
      }

      if (this._outputTail + len >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = this._quoteChar;
      if (toRead > 0 && len >= 0) {
         this._reportError("Didn't read enough from reader");
      }

   }

   public void writeString(char[] text, int offset, int len) throws IOException {
      this._verifyValueWrite("write a string");
      if (this._outputTail >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = this._quoteChar;
      this._writeString(text, offset, len);
      if (this._outputTail >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = this._quoteChar;
   }

   public void writeString(SerializableString sstr) throws IOException {
      this._verifyValueWrite("write a string");
      if (this._outputTail >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = this._quoteChar;
      char[] text = sstr.asQuotedChars();
      int len = text.length;
      if (len < 32) {
         int room = this._outputEnd - this._outputTail;
         if (len > room) {
            this._flushBuffer();
         }

         System.arraycopy(text, 0, this._outputBuffer, this._outputTail, len);
         this._outputTail += len;
      } else {
         this._flushBuffer();
         this._writer.write(text, 0, len);
      }

      if (this._outputTail >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = this._quoteChar;
   }

   public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
      this._reportUnsupportedOperation();
   }

   public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
      this._reportUnsupportedOperation();
   }

   public void writeRaw(String text) throws IOException {
      int len = text.length();
      int room = this._outputEnd - this._outputTail;
      if (room == 0) {
         this._flushBuffer();
         room = this._outputEnd - this._outputTail;
      }

      if (room >= len) {
         text.getChars(0, len, this._outputBuffer, this._outputTail);
         this._outputTail += len;
      } else {
         this.writeRawLong(text);
      }

   }

   public void writeRaw(String text, int start, int len) throws IOException {
      int room = this._outputEnd - this._outputTail;
      if (room < len) {
         this._flushBuffer();
         room = this._outputEnd - this._outputTail;
      }

      if (room >= len) {
         text.getChars(start, start + len, this._outputBuffer, this._outputTail);
         this._outputTail += len;
      } else {
         this.writeRawLong(text.substring(start, start + len));
      }

   }

   public void writeRaw(SerializableString text) throws IOException {
      this.writeRaw(text.getValue());
   }

   public void writeRaw(char[] text, int offset, int len) throws IOException {
      if (len < 32) {
         int room = this._outputEnd - this._outputTail;
         if (len > room) {
            this._flushBuffer();
         }

         System.arraycopy(text, offset, this._outputBuffer, this._outputTail, len);
         this._outputTail += len;
      } else {
         this._flushBuffer();
         this._writer.write(text, offset, len);
      }
   }

   public void writeRaw(char c) throws IOException {
      if (this._outputTail >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = c;
   }

   private void writeRawLong(String text) throws IOException {
      int room = this._outputEnd - this._outputTail;
      text.getChars(0, room, this._outputBuffer, this._outputTail);
      this._outputTail += room;
      this._flushBuffer();
      int offset = room;

      int len;
      int amount;
      for(len = text.length() - room; len > this._outputEnd; len -= amount) {
         amount = this._outputEnd;
         text.getChars(offset, offset + amount, this._outputBuffer, 0);
         this._outputHead = 0;
         this._outputTail = amount;
         this._flushBuffer();
         offset += amount;
      }

      text.getChars(offset, offset + len, this._outputBuffer, 0);
      this._outputHead = 0;
      this._outputTail = len;
   }

   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
      this._verifyValueWrite("write a binary value");
      if (this._outputTail >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = this._quoteChar;
      this._writeBinary(b64variant, data, offset, offset + len);
      if (this._outputTail >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = this._quoteChar;
   }

   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException, JsonGenerationException {
      this._verifyValueWrite("write a binary value");
      if (this._outputTail >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = this._quoteChar;
      byte[] encodingBuffer = this._ioContext.allocBase64Buffer();

      int bytes;
      try {
         if (dataLength < 0) {
            bytes = this._writeBinary(b64variant, data, encodingBuffer);
         } else {
            int missing = this._writeBinary(b64variant, data, encodingBuffer, dataLength);
            if (missing > 0) {
               this._reportError("Too few bytes available: missing " + missing + " bytes (out of " + dataLength + ")");
            }

            bytes = dataLength;
         }
      } finally {
         this._ioContext.releaseBase64Buffer(encodingBuffer);
      }

      if (this._outputTail >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = this._quoteChar;
      return bytes;
   }

   public void writeNumber(short s) throws IOException {
      this._verifyValueWrite("write a number");
      if (this._cfgNumbersAsStrings) {
         this._writeQuotedShort(s);
      } else {
         if (this._outputTail + 6 >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputTail = NumberOutput.outputInt(s, (char[])this._outputBuffer, this._outputTail);
      }
   }

   private void _writeQuotedShort(short s) throws IOException {
      if (this._outputTail + 8 >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = this._quoteChar;
      this._outputTail = NumberOutput.outputInt(s, (char[])this._outputBuffer, this._outputTail);
      this._outputBuffer[this._outputTail++] = this._quoteChar;
   }

   public void writeNumber(int i) throws IOException {
      this._verifyValueWrite("write a number");
      if (this._cfgNumbersAsStrings) {
         this._writeQuotedInt(i);
      } else {
         if (this._outputTail + 11 >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
      }
   }

   private void _writeQuotedInt(int i) throws IOException {
      if (this._outputTail + 13 >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = this._quoteChar;
      this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
      this._outputBuffer[this._outputTail++] = this._quoteChar;
   }

   public void writeNumber(long l) throws IOException {
      this._verifyValueWrite("write a number");
      if (this._cfgNumbersAsStrings) {
         this._writeQuotedLong(l);
      } else {
         if (this._outputTail + 21 >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
      }
   }

   private void _writeQuotedLong(long l) throws IOException {
      if (this._outputTail + 23 >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = this._quoteChar;
      this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
      this._outputBuffer[this._outputTail++] = this._quoteChar;
   }

   public void writeNumber(BigInteger value) throws IOException {
      this._verifyValueWrite("write a number");
      if (value == null) {
         this._writeNull();
      } else if (this._cfgNumbersAsStrings) {
         this._writeQuotedRaw(value.toString());
      } else {
         this.writeRaw(value.toString());
      }

   }

   public void writeNumber(double d) throws IOException {
      if (!this._cfgNumbersAsStrings && (!this.isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS) || !Double.isNaN(d) && !Double.isInfinite(d))) {
         this._verifyValueWrite("write a number");
         this.writeRaw(String.valueOf(d));
      } else {
         this.writeString(String.valueOf(d));
      }
   }

   public void writeNumber(float f) throws IOException {
      if (!this._cfgNumbersAsStrings && (!this.isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS) || !Float.isNaN(f) && !Float.isInfinite(f))) {
         this._verifyValueWrite("write a number");
         this.writeRaw(String.valueOf(f));
      } else {
         this.writeString(String.valueOf(f));
      }
   }

   public void writeNumber(BigDecimal value) throws IOException {
      this._verifyValueWrite("write a number");
      if (value == null) {
         this._writeNull();
      } else if (this._cfgNumbersAsStrings) {
         this._writeQuotedRaw(this._asString(value));
      } else {
         this.writeRaw(this._asString(value));
      }

   }

   public void writeNumber(String encodedValue) throws IOException {
      this._verifyValueWrite("write a number");
      if (this._cfgNumbersAsStrings) {
         this._writeQuotedRaw(encodedValue);
      } else {
         this.writeRaw(encodedValue);
      }

   }

   private void _writeQuotedRaw(String value) throws IOException {
      if (this._outputTail >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = this._quoteChar;
      this.writeRaw(value);
      if (this._outputTail >= this._outputEnd) {
         this._flushBuffer();
      }

      this._outputBuffer[this._outputTail++] = this._quoteChar;
   }

   public void writeBoolean(boolean state) throws IOException {
      this._verifyValueWrite("write a boolean value");
      if (this._outputTail + 5 >= this._outputEnd) {
         this._flushBuffer();
      }

      int ptr = this._outputTail;
      char[] buf = this._outputBuffer;
      if (state) {
         buf[ptr] = 't';
         ++ptr;
         buf[ptr] = 'r';
         ++ptr;
         buf[ptr] = 'u';
         ++ptr;
         buf[ptr] = 'e';
      } else {
         buf[ptr] = 'f';
         ++ptr;
         buf[ptr] = 'a';
         ++ptr;
         buf[ptr] = 'l';
         ++ptr;
         buf[ptr] = 's';
         ++ptr;
         buf[ptr] = 'e';
      }

      this._outputTail = ptr + 1;
   }

   public void writeNull() throws IOException {
      this._verifyValueWrite("write a null");
      this._writeNull();
   }

   protected final void _verifyValueWrite(String typeMsg) throws IOException {
      int status = this._writeContext.writeValue();
      if (this._cfgPrettyPrinter != null) {
         this._verifyPrettyValueWrite(typeMsg, status);
      } else {
         byte c;
         switch(status) {
         case 0:
         case 4:
         default:
            return;
         case 1:
            c = 44;
            break;
         case 2:
            c = 58;
            break;
         case 3:
            if (this._rootValueSeparator != null) {
               this.writeRaw(this._rootValueSeparator.getValue());
            }

            return;
         case 5:
            this._reportCantWriteValueExpectName(typeMsg);
            return;
         }

         if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
         }

         this._outputBuffer[this._outputTail++] = (char)c;
      }
   }

   public void flush() throws IOException {
      this._flushBuffer();
      if (this._writer != null && this.isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
         this._writer.flush();
      }

   }

   public void close() throws IOException {
      super.close();
      if (this._outputBuffer != null && this.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)) {
         label34:
         while(true) {
            while(true) {
               JsonStreamContext ctxt = this.getOutputContext();
               if (ctxt.inArray()) {
                  this.writeEndArray();
               } else {
                  if (!ctxt.inObject()) {
                     break label34;
                  }

                  this.writeEndObject();
               }
            }
         }
      }

      this._flushBuffer();
      this._outputHead = 0;
      this._outputTail = 0;
      if (this._writer != null) {
         if (!this._ioContext.isResourceManaged() && !this.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
            if (this.isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
               this._writer.flush();
            }
         } else {
            this._writer.close();
         }
      }

      this._releaseBuffers();
   }

   protected void _releaseBuffers() {
      char[] buf = this._outputBuffer;
      if (buf != null) {
         this._outputBuffer = null;
         this._ioContext.releaseConcatBuffer(buf);
      }

      buf = this._charBuffer;
      if (buf != null) {
         this._charBuffer = null;
         this._ioContext.releaseNameCopyBuffer(buf);
      }

   }

   private void _writeString(String text) throws IOException {
      int len = text.length();
      if (len > this._outputEnd) {
         this._writeLongString(text);
      } else {
         if (this._outputTail + len > this._outputEnd) {
            this._flushBuffer();
         }

         text.getChars(0, len, this._outputBuffer, this._outputTail);
         if (this._characterEscapes != null) {
            this._writeStringCustom(len);
         } else if (this._maximumNonEscapedChar != 0) {
            this._writeStringASCII(len, this._maximumNonEscapedChar);
         } else {
            this._writeString2(len);
         }

      }
   }

   private void _writeString2(int len) throws IOException {
      int end = this._outputTail + len;
      int[] escCodes = this._outputEscapes;
      int escLen = escCodes.length;

      while(this._outputTail < end) {
         while(true) {
            char c = this._outputBuffer[this._outputTail];
            if (c < escLen && escCodes[c] != 0) {
               int flushLen = this._outputTail - this._outputHead;
               if (flushLen > 0) {
                  this._writer.write(this._outputBuffer, this._outputHead, flushLen);
               }

               char c = this._outputBuffer[this._outputTail++];
               this._prependOrWriteCharacterEscape(c, escCodes[c]);
            } else if (++this._outputTail >= end) {
               return;
            }
         }
      }

   }

   private void _writeLongString(String text) throws IOException {
      this._flushBuffer();
      int textLen = text.length();
      int offset = 0;

      do {
         int max = this._outputEnd;
         int segmentLen = offset + max > textLen ? textLen - offset : max;
         text.getChars(offset, offset + segmentLen, this._outputBuffer, 0);
         if (this._characterEscapes != null) {
            this._writeSegmentCustom(segmentLen);
         } else if (this._maximumNonEscapedChar != 0) {
            this._writeSegmentASCII(segmentLen, this._maximumNonEscapedChar);
         } else {
            this._writeSegment(segmentLen);
         }

         offset += segmentLen;
      } while(offset < textLen);

   }

   private void _writeSegment(int end) throws IOException {
      int[] escCodes = this._outputEscapes;
      int escLen = escCodes.length;
      int ptr = 0;

      char c;
      for(int start = ptr; ptr < end; start = this._prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCodes[c])) {
         do {
            c = this._outputBuffer[ptr];
            if (c < escLen && escCodes[c] != 0) {
               break;
            }

            ++ptr;
         } while(ptr < end);

         int flushLen = ptr - start;
         if (flushLen > 0) {
            this._writer.write(this._outputBuffer, start, flushLen);
            if (ptr >= end) {
               break;
            }
         }

         ++ptr;
      }

   }

   private void _writeString(char[] text, int offset, int len) throws IOException {
      if (this._characterEscapes != null) {
         this._writeStringCustom(text, offset, len);
      } else if (this._maximumNonEscapedChar != 0) {
         this._writeStringASCII(text, offset, len, this._maximumNonEscapedChar);
      } else {
         len += offset;
         int[] escCodes = this._outputEscapes;
         int escLen = escCodes.length;

         while(offset < len) {
            int start = offset;

            do {
               char c = text[offset];
               if (c < escLen && escCodes[c] != 0) {
                  break;
               }

               ++offset;
            } while(offset < len);

            int newAmount = offset - start;
            if (newAmount < 32) {
               if (this._outputTail + newAmount > this._outputEnd) {
                  this._flushBuffer();
               }

               if (newAmount > 0) {
                  System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
                  this._outputTail += newAmount;
               }
            } else {
               this._flushBuffer();
               this._writer.write(text, start, newAmount);
            }

            if (offset >= len) {
               break;
            }

            char c = text[offset++];
            this._appendCharacterEscape(c, escCodes[c]);
         }

      }
   }

   private void _writeStringASCII(int len, int maxNonEscaped) throws IOException, JsonGenerationException {
      int end = this._outputTail + len;
      int[] escCodes = this._outputEscapes;
      int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
      boolean var6 = false;

      while(this._outputTail < end) {
         char c;
         int escCode;
         while(true) {
            c = this._outputBuffer[this._outputTail];
            if (c < escLimit) {
               escCode = escCodes[c];
               if (escCode != 0) {
                  break;
               }
            } else if (c > maxNonEscaped) {
               escCode = -1;
               break;
            }

            if (++this._outputTail >= end) {
               return;
            }
         }

         int flushLen = this._outputTail - this._outputHead;
         if (flushLen > 0) {
            this._writer.write(this._outputBuffer, this._outputHead, flushLen);
         }

         ++this._outputTail;
         this._prependOrWriteCharacterEscape(c, escCode);
      }

   }

   private void _writeSegmentASCII(int end, int maxNonEscaped) throws IOException, JsonGenerationException {
      int[] escCodes = this._outputEscapes;
      int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
      int ptr = 0;
      int escCode = 0;

      char c;
      for(int start = ptr; ptr < end; start = this._prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCode)) {
         do {
            c = this._outputBuffer[ptr];
            if (c < escLimit) {
               escCode = escCodes[c];
               if (escCode != 0) {
                  break;
               }
            } else if (c > maxNonEscaped) {
               escCode = -1;
               break;
            }

            ++ptr;
         } while(ptr < end);

         int flushLen = ptr - start;
         if (flushLen > 0) {
            this._writer.write(this._outputBuffer, start, flushLen);
            if (ptr >= end) {
               break;
            }
         }

         ++ptr;
      }

   }

   private void _writeStringASCII(char[] text, int offset, int len, int maxNonEscaped) throws IOException, JsonGenerationException {
      len += offset;
      int[] escCodes = this._outputEscapes;
      int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
      int escCode = 0;

      while(offset < len) {
         int start = offset;

         char c;
         do {
            c = text[offset];
            if (c < escLimit) {
               escCode = escCodes[c];
               if (escCode != 0) {
                  break;
               }
            } else if (c > maxNonEscaped) {
               escCode = -1;
               break;
            }

            ++offset;
         } while(offset < len);

         int newAmount = offset - start;
         if (newAmount < 32) {
            if (this._outputTail + newAmount > this._outputEnd) {
               this._flushBuffer();
            }

            if (newAmount > 0) {
               System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
               this._outputTail += newAmount;
            }
         } else {
            this._flushBuffer();
            this._writer.write(text, start, newAmount);
         }

         if (offset >= len) {
            break;
         }

         ++offset;
         this._appendCharacterEscape(c, escCode);
      }

   }

   private void _writeStringCustom(int len) throws IOException, JsonGenerationException {
      int end = this._outputTail + len;
      int[] escCodes = this._outputEscapes;
      int maxNonEscaped = this._maximumNonEscapedChar < 1 ? '\uffff' : this._maximumNonEscapedChar;
      int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
      int escCode = false;
      CharacterEscapes customEscapes = this._characterEscapes;

      while(this._outputTail < end) {
         char c;
         int escCode;
         while(true) {
            c = this._outputBuffer[this._outputTail];
            if (c < escLimit) {
               escCode = escCodes[c];
               if (escCode != 0) {
                  break;
               }
            } else {
               if (c > maxNonEscaped) {
                  escCode = -1;
                  break;
               }

               if ((this._currentEscape = customEscapes.getEscapeSequence(c)) != null) {
                  escCode = -2;
                  break;
               }
            }

            if (++this._outputTail >= end) {
               return;
            }
         }

         int flushLen = this._outputTail - this._outputHead;
         if (flushLen > 0) {
            this._writer.write(this._outputBuffer, this._outputHead, flushLen);
         }

         ++this._outputTail;
         this._prependOrWriteCharacterEscape(c, escCode);
      }

   }

   private void _writeSegmentCustom(int end) throws IOException, JsonGenerationException {
      int[] escCodes = this._outputEscapes;
      int maxNonEscaped = this._maximumNonEscapedChar < 1 ? '\uffff' : this._maximumNonEscapedChar;
      int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
      CharacterEscapes customEscapes = this._characterEscapes;
      int ptr = 0;
      int escCode = 0;

      char c;
      for(int start = ptr; ptr < end; start = this._prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCode)) {
         do {
            c = this._outputBuffer[ptr];
            if (c < escLimit) {
               escCode = escCodes[c];
               if (escCode != 0) {
                  break;
               }
            } else {
               if (c > maxNonEscaped) {
                  escCode = -1;
                  break;
               }

               if ((this._currentEscape = customEscapes.getEscapeSequence(c)) != null) {
                  escCode = -2;
                  break;
               }
            }

            ++ptr;
         } while(ptr < end);

         int flushLen = ptr - start;
         if (flushLen > 0) {
            this._writer.write(this._outputBuffer, start, flushLen);
            if (ptr >= end) {
               break;
            }
         }

         ++ptr;
      }

   }

   private void _writeStringCustom(char[] text, int offset, int len) throws IOException, JsonGenerationException {
      len += offset;
      int[] escCodes = this._outputEscapes;
      int maxNonEscaped = this._maximumNonEscapedChar < 1 ? '\uffff' : this._maximumNonEscapedChar;
      int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
      CharacterEscapes customEscapes = this._characterEscapes;
      int escCode = 0;

      while(offset < len) {
         int start = offset;

         char c;
         do {
            c = text[offset];
            if (c < escLimit) {
               escCode = escCodes[c];
               if (escCode != 0) {
                  break;
               }
            } else {
               if (c > maxNonEscaped) {
                  escCode = -1;
                  break;
               }

               if ((this._currentEscape = customEscapes.getEscapeSequence(c)) != null) {
                  escCode = -2;
                  break;
               }
            }

            ++offset;
         } while(offset < len);

         int newAmount = offset - start;
         if (newAmount < 32) {
            if (this._outputTail + newAmount > this._outputEnd) {
               this._flushBuffer();
            }

            if (newAmount > 0) {
               System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
               this._outputTail += newAmount;
            }
         } else {
            this._flushBuffer();
            this._writer.write(text, start, newAmount);
         }

         if (offset >= len) {
            break;
         }

         ++offset;
         this._appendCharacterEscape(c, escCode);
      }

   }

   protected final void _writeBinary(Base64Variant b64variant, byte[] input, int inputPtr, int inputEnd) throws IOException, JsonGenerationException {
      int safeInputEnd = inputEnd - 3;
      int safeOutputEnd = this._outputEnd - 6;
      int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;

      int inputLeft;
      while(inputPtr <= safeInputEnd) {
         if (this._outputTail > safeOutputEnd) {
            this._flushBuffer();
         }

         inputLeft = input[inputPtr++] << 8;
         inputLeft |= input[inputPtr++] & 255;
         inputLeft = inputLeft << 8 | input[inputPtr++] & 255;
         this._outputTail = b64variant.encodeBase64Chunk(inputLeft, this._outputBuffer, this._outputTail);
         --chunksBeforeLF;
         if (chunksBeforeLF <= 0) {
            this._outputBuffer[this._outputTail++] = '\\';
            this._outputBuffer[this._outputTail++] = 'n';
            chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
         }
      }

      inputLeft = inputEnd - inputPtr;
      if (inputLeft > 0) {
         if (this._outputTail > safeOutputEnd) {
            this._flushBuffer();
         }

         int b24 = input[inputPtr++] << 16;
         if (inputLeft == 2) {
            b24 |= (input[inputPtr++] & 255) << 8;
         }

         this._outputTail = b64variant.encodeBase64Partial(b24, inputLeft, this._outputBuffer, this._outputTail);
      }

   }

   protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer, int bytesLeft) throws IOException, JsonGenerationException {
      int inputPtr = 0;
      int inputEnd = 0;
      int lastFullOffset = -3;
      int safeOutputEnd = this._outputEnd - 6;
      int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;

      int b24;
      while(bytesLeft > 2) {
         if (inputPtr > lastFullOffset) {
            inputEnd = this._readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
            inputPtr = 0;
            if (inputEnd < 3) {
               break;
            }

            lastFullOffset = inputEnd - 3;
         }

         if (this._outputTail > safeOutputEnd) {
            this._flushBuffer();
         }

         b24 = readBuffer[inputPtr++] << 8;
         b24 |= readBuffer[inputPtr++] & 255;
         b24 = b24 << 8 | readBuffer[inputPtr++] & 255;
         bytesLeft -= 3;
         this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
         --chunksBeforeLF;
         if (chunksBeforeLF <= 0) {
            this._outputBuffer[this._outputTail++] = '\\';
            this._outputBuffer[this._outputTail++] = 'n';
            chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
         }
      }

      if (bytesLeft > 0) {
         inputEnd = this._readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
         int inputPtr = 0;
         if (inputEnd > 0) {
            if (this._outputTail > safeOutputEnd) {
               this._flushBuffer();
            }

            inputPtr = inputPtr + 1;
            b24 = readBuffer[inputPtr] << 16;
            byte amount;
            if (inputPtr < inputEnd) {
               b24 |= (readBuffer[inputPtr] & 255) << 8;
               amount = 2;
            } else {
               amount = 1;
            }

            this._outputTail = b64variant.encodeBase64Partial(b24, amount, (char[])this._outputBuffer, this._outputTail);
            bytesLeft -= amount;
         }
      }

      return bytesLeft;
   }

   protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer) throws IOException, JsonGenerationException {
      int inputPtr = 0;
      int inputEnd = 0;
      int lastFullOffset = -3;
      int bytesDone = 0;
      int safeOutputEnd = this._outputEnd - 6;
      int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;

      while(true) {
         int b24;
         if (inputPtr > lastFullOffset) {
            inputEnd = this._readMore(data, readBuffer, inputPtr, inputEnd, readBuffer.length);
            inputPtr = 0;
            if (inputEnd < 3) {
               if (inputPtr < inputEnd) {
                  if (this._outputTail > safeOutputEnd) {
                     this._flushBuffer();
                  }

                  b24 = readBuffer[inputPtr++] << 16;
                  int amount = 1;
                  if (inputPtr < inputEnd) {
                     b24 |= (readBuffer[inputPtr] & 255) << 8;
                     amount = 2;
                  }

                  bytesDone += amount;
                  this._outputTail = b64variant.encodeBase64Partial(b24, amount, (char[])this._outputBuffer, this._outputTail);
               }

               return bytesDone;
            }

            lastFullOffset = inputEnd - 3;
         }

         if (this._outputTail > safeOutputEnd) {
            this._flushBuffer();
         }

         b24 = readBuffer[inputPtr++] << 8;
         b24 |= readBuffer[inputPtr++] & 255;
         b24 = b24 << 8 | readBuffer[inputPtr++] & 255;
         bytesDone += 3;
         this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
         --chunksBeforeLF;
         if (chunksBeforeLF <= 0) {
            this._outputBuffer[this._outputTail++] = '\\';
            this._outputBuffer[this._outputTail++] = 'n';
            chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
         }
      }
   }

   private int _readMore(InputStream in, byte[] readBuffer, int inputPtr, int inputEnd, int maxRead) throws IOException {
      int i;
      for(i = 0; inputPtr < inputEnd; readBuffer[i++] = readBuffer[inputPtr++]) {
      }

      int inputPtr = false;
      inputEnd = i;
      maxRead = Math.min(maxRead, readBuffer.length);

      do {
         int length = maxRead - inputEnd;
         if (length == 0) {
            break;
         }

         int count = in.read(readBuffer, inputEnd, length);
         if (count < 0) {
            return inputEnd;
         }

         inputEnd += count;
      } while(inputEnd < 3);

      return inputEnd;
   }

   private final void _writeNull() throws IOException {
      if (this._outputTail + 4 >= this._outputEnd) {
         this._flushBuffer();
      }

      int ptr = this._outputTail;
      char[] buf = this._outputBuffer;
      buf[ptr] = 'n';
      ++ptr;
      buf[ptr] = 'u';
      ++ptr;
      buf[ptr] = 'l';
      ++ptr;
      buf[ptr] = 'l';
      this._outputTail = ptr + 1;
   }

   private void _prependOrWriteCharacterEscape(char ch, int escCode) throws IOException, JsonGenerationException {
      char[] buf;
      if (escCode >= 0) {
         if (this._outputTail >= 2) {
            int ptr = this._outputTail - 2;
            this._outputHead = ptr;
            this._outputBuffer[ptr++] = '\\';
            this._outputBuffer[ptr] = (char)escCode;
         } else {
            buf = this._entityBuffer;
            if (buf == null) {
               buf = this._allocateEntityBuffer();
            }

            this._outputHead = this._outputTail;
            buf[1] = (char)escCode;
            this._writer.write(buf, 0, 2);
         }
      } else {
         int hi;
         int lo;
         if (escCode != -2) {
            if (this._outputTail >= 6) {
               buf = this._outputBuffer;
               hi = this._outputTail - 6;
               this._outputHead = hi;
               buf[hi] = '\\';
               ++hi;
               buf[hi] = 'u';
               if (ch > 255) {
                  lo = ch >> 8 & 255;
                  ++hi;
                  buf[hi] = HEX_CHARS[lo >> 4];
                  ++hi;
                  buf[hi] = HEX_CHARS[lo & 15];
                  ch = (char)(ch & 255);
               } else {
                  ++hi;
                  buf[hi] = '0';
                  ++hi;
                  buf[hi] = '0';
               }

               ++hi;
               buf[hi] = HEX_CHARS[ch >> 4];
               ++hi;
               buf[hi] = HEX_CHARS[ch & 15];
            } else {
               buf = this._entityBuffer;
               if (buf == null) {
                  buf = this._allocateEntityBuffer();
               }

               this._outputHead = this._outputTail;
               if (ch > 255) {
                  hi = ch >> 8 & 255;
                  lo = ch & 255;
                  buf[10] = HEX_CHARS[hi >> 4];
                  buf[11] = HEX_CHARS[hi & 15];
                  buf[12] = HEX_CHARS[lo >> 4];
                  buf[13] = HEX_CHARS[lo & 15];
                  this._writer.write(buf, 8, 6);
               } else {
                  buf[6] = HEX_CHARS[ch >> 4];
                  buf[7] = HEX_CHARS[ch & 15];
                  this._writer.write(buf, 2, 6);
               }

            }
         } else {
            String escape;
            if (this._currentEscape == null) {
               escape = this._characterEscapes.getEscapeSequence(ch).getValue();
            } else {
               escape = this._currentEscape.getValue();
               this._currentEscape = null;
            }

            hi = escape.length();
            if (this._outputTail >= hi) {
               lo = this._outputTail - hi;
               this._outputHead = lo;
               escape.getChars(0, hi, this._outputBuffer, lo);
            } else {
               this._outputHead = this._outputTail;
               this._writer.write(escape);
            }
         }
      }
   }

   private int _prependOrWriteCharacterEscape(char[] buffer, int ptr, int end, char ch, int escCode) throws IOException, JsonGenerationException {
      char[] ent;
      if (escCode >= 0) {
         if (ptr > 1 && ptr < end) {
            ptr -= 2;
            buffer[ptr] = '\\';
            buffer[ptr + 1] = (char)escCode;
         } else {
            ent = this._entityBuffer;
            if (ent == null) {
               ent = this._allocateEntityBuffer();
            }

            ent[1] = (char)escCode;
            this._writer.write(ent, 0, 2);
         }

         return ptr;
      } else {
         int hi;
         if (escCode != -2) {
            if (ptr > 5 && ptr < end) {
               ptr -= 6;
               buffer[ptr++] = '\\';
               buffer[ptr++] = 'u';
               if (ch > 255) {
                  int hi = ch >> 8 & 255;
                  buffer[ptr++] = HEX_CHARS[hi >> 4];
                  buffer[ptr++] = HEX_CHARS[hi & 15];
                  ch = (char)(ch & 255);
               } else {
                  buffer[ptr++] = '0';
                  buffer[ptr++] = '0';
               }

               buffer[ptr++] = HEX_CHARS[ch >> 4];
               buffer[ptr] = HEX_CHARS[ch & 15];
               ptr -= 5;
            } else {
               ent = this._entityBuffer;
               if (ent == null) {
                  ent = this._allocateEntityBuffer();
               }

               this._outputHead = this._outputTail;
               if (ch > 255) {
                  hi = ch >> 8 & 255;
                  int lo = ch & 255;
                  ent[10] = HEX_CHARS[hi >> 4];
                  ent[11] = HEX_CHARS[hi & 15];
                  ent[12] = HEX_CHARS[lo >> 4];
                  ent[13] = HEX_CHARS[lo & 15];
                  this._writer.write(ent, 8, 6);
               } else {
                  ent[6] = HEX_CHARS[ch >> 4];
                  ent[7] = HEX_CHARS[ch & 15];
                  this._writer.write(ent, 2, 6);
               }
            }

            return ptr;
         } else {
            String escape;
            if (this._currentEscape == null) {
               escape = this._characterEscapes.getEscapeSequence(ch).getValue();
            } else {
               escape = this._currentEscape.getValue();
               this._currentEscape = null;
            }

            hi = escape.length();
            if (ptr >= hi && ptr < end) {
               ptr -= hi;
               escape.getChars(0, hi, buffer, ptr);
            } else {
               this._writer.write(escape);
            }

            return ptr;
         }
      }
   }

   private void _appendCharacterEscape(char ch, int escCode) throws IOException, JsonGenerationException {
      if (escCode >= 0) {
         if (this._outputTail + 2 > this._outputEnd) {
            this._flushBuffer();
         }

         this._outputBuffer[this._outputTail++] = '\\';
         this._outputBuffer[this._outputTail++] = (char)escCode;
      } else if (escCode != -2) {
         if (this._outputTail + 5 >= this._outputEnd) {
            this._flushBuffer();
         }

         int ptr = this._outputTail;
         char[] buf = this._outputBuffer;
         buf[ptr++] = '\\';
         buf[ptr++] = 'u';
         if (ch > 255) {
            int hi = ch >> 8 & 255;
            buf[ptr++] = HEX_CHARS[hi >> 4];
            buf[ptr++] = HEX_CHARS[hi & 15];
            ch = (char)(ch & 255);
         } else {
            buf[ptr++] = '0';
            buf[ptr++] = '0';
         }

         buf[ptr++] = HEX_CHARS[ch >> 4];
         buf[ptr++] = HEX_CHARS[ch & 15];
         this._outputTail = ptr;
      } else {
         String escape;
         if (this._currentEscape == null) {
            escape = this._characterEscapes.getEscapeSequence(ch).getValue();
         } else {
            escape = this._currentEscape.getValue();
            this._currentEscape = null;
         }

         int len = escape.length();
         if (this._outputTail + len > this._outputEnd) {
            this._flushBuffer();
            if (len > this._outputEnd) {
               this._writer.write(escape);
               return;
            }
         }

         escape.getChars(0, len, this._outputBuffer, this._outputTail);
         this._outputTail += len;
      }
   }

   private char[] _allocateEntityBuffer() {
      char[] buf = new char[14];
      buf[0] = '\\';
      buf[2] = '\\';
      buf[3] = 'u';
      buf[4] = '0';
      buf[5] = '0';
      buf[8] = '\\';
      buf[9] = 'u';
      this._entityBuffer = buf;
      return buf;
   }

   private char[] _allocateCopyBuffer() {
      if (this._charBuffer == null) {
         this._charBuffer = this._ioContext.allocNameCopyBuffer(2000);
      }

      return this._charBuffer;
   }

   protected void _flushBuffer() throws IOException {
      int len = this._outputTail - this._outputHead;
      if (len > 0) {
         int offset = this._outputHead;
         this._outputTail = this._outputHead = 0;
         this._writer.write(this._outputBuffer, offset, len);
      }

   }
}
