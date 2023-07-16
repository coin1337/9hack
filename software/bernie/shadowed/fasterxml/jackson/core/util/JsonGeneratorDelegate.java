package software.bernie.shadowed.fasterxml.jackson.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variant;
import software.bernie.shadowed.fasterxml.jackson.core.FormatSchema;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonStreamContext;
import software.bernie.shadowed.fasterxml.jackson.core.ObjectCodec;
import software.bernie.shadowed.fasterxml.jackson.core.PrettyPrinter;
import software.bernie.shadowed.fasterxml.jackson.core.SerializableString;
import software.bernie.shadowed.fasterxml.jackson.core.TreeNode;
import software.bernie.shadowed.fasterxml.jackson.core.Version;
import software.bernie.shadowed.fasterxml.jackson.core.io.CharacterEscapes;

public class JsonGeneratorDelegate extends JsonGenerator {
   protected JsonGenerator delegate;
   protected boolean delegateCopyMethods;

   public JsonGeneratorDelegate(JsonGenerator d) {
      this(d, true);
   }

   public JsonGeneratorDelegate(JsonGenerator d, boolean delegateCopyMethods) {
      this.delegate = d;
      this.delegateCopyMethods = delegateCopyMethods;
   }

   public Object getCurrentValue() {
      return this.delegate.getCurrentValue();
   }

   public void setCurrentValue(Object v) {
      this.delegate.setCurrentValue(v);
   }

   public JsonGenerator getDelegate() {
      return this.delegate;
   }

   public ObjectCodec getCodec() {
      return this.delegate.getCodec();
   }

   public JsonGenerator setCodec(ObjectCodec oc) {
      this.delegate.setCodec(oc);
      return this;
   }

   public void setSchema(FormatSchema schema) {
      this.delegate.setSchema(schema);
   }

   public FormatSchema getSchema() {
      return this.delegate.getSchema();
   }

   public Version version() {
      return this.delegate.version();
   }

   public Object getOutputTarget() {
      return this.delegate.getOutputTarget();
   }

   public int getOutputBuffered() {
      return this.delegate.getOutputBuffered();
   }

   public boolean canUseSchema(FormatSchema schema) {
      return this.delegate.canUseSchema(schema);
   }

   public boolean canWriteTypeId() {
      return this.delegate.canWriteTypeId();
   }

   public boolean canWriteObjectId() {
      return this.delegate.canWriteObjectId();
   }

   public boolean canWriteBinaryNatively() {
      return this.delegate.canWriteBinaryNatively();
   }

   public boolean canOmitFields() {
      return this.delegate.canOmitFields();
   }

   public JsonGenerator enable(JsonGenerator.Feature f) {
      this.delegate.enable(f);
      return this;
   }

   public JsonGenerator disable(JsonGenerator.Feature f) {
      this.delegate.disable(f);
      return this;
   }

   public boolean isEnabled(JsonGenerator.Feature f) {
      return this.delegate.isEnabled(f);
   }

   public int getFeatureMask() {
      return this.delegate.getFeatureMask();
   }

   /** @deprecated */
   @Deprecated
   public JsonGenerator setFeatureMask(int mask) {
      this.delegate.setFeatureMask(mask);
      return this;
   }

   public JsonGenerator overrideStdFeatures(int values, int mask) {
      this.delegate.overrideStdFeatures(values, mask);
      return this;
   }

   public JsonGenerator overrideFormatFeatures(int values, int mask) {
      this.delegate.overrideFormatFeatures(values, mask);
      return this;
   }

   public JsonGenerator setPrettyPrinter(PrettyPrinter pp) {
      this.delegate.setPrettyPrinter(pp);
      return this;
   }

   public PrettyPrinter getPrettyPrinter() {
      return this.delegate.getPrettyPrinter();
   }

   public JsonGenerator useDefaultPrettyPrinter() {
      this.delegate.useDefaultPrettyPrinter();
      return this;
   }

   public JsonGenerator setHighestNonEscapedChar(int charCode) {
      this.delegate.setHighestNonEscapedChar(charCode);
      return this;
   }

   public int getHighestEscapedChar() {
      return this.delegate.getHighestEscapedChar();
   }

   public CharacterEscapes getCharacterEscapes() {
      return this.delegate.getCharacterEscapes();
   }

   public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
      this.delegate.setCharacterEscapes(esc);
      return this;
   }

   public JsonGenerator setRootValueSeparator(SerializableString sep) {
      this.delegate.setRootValueSeparator(sep);
      return this;
   }

   public void writeStartArray() throws IOException {
      this.delegate.writeStartArray();
   }

   public void writeStartArray(int size) throws IOException {
      this.delegate.writeStartArray(size);
   }

   public void writeEndArray() throws IOException {
      this.delegate.writeEndArray();
   }

   public void writeStartObject() throws IOException {
      this.delegate.writeStartObject();
   }

   public void writeStartObject(Object forValue) throws IOException {
      this.delegate.writeStartObject(forValue);
   }

   public void writeEndObject() throws IOException {
      this.delegate.writeEndObject();
   }

   public void writeFieldName(String name) throws IOException {
      this.delegate.writeFieldName(name);
   }

   public void writeFieldName(SerializableString name) throws IOException {
      this.delegate.writeFieldName(name);
   }

   public void writeFieldId(long id) throws IOException {
      this.delegate.writeFieldId(id);
   }

   public void writeArray(int[] array, int offset, int length) throws IOException {
      this.delegate.writeArray(array, offset, length);
   }

   public void writeArray(long[] array, int offset, int length) throws IOException {
      this.delegate.writeArray(array, offset, length);
   }

   public void writeArray(double[] array, int offset, int length) throws IOException {
      this.delegate.writeArray(array, offset, length);
   }

   public void writeString(String text) throws IOException {
      this.delegate.writeString(text);
   }

   public void writeString(Reader reader, int len) throws IOException {
      this.delegate.writeString(reader, len);
   }

   public void writeString(char[] text, int offset, int len) throws IOException {
      this.delegate.writeString(text, offset, len);
   }

   public void writeString(SerializableString text) throws IOException {
      this.delegate.writeString(text);
   }

   public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
      this.delegate.writeRawUTF8String(text, offset, length);
   }

   public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
      this.delegate.writeUTF8String(text, offset, length);
   }

   public void writeRaw(String text) throws IOException {
      this.delegate.writeRaw(text);
   }

   public void writeRaw(String text, int offset, int len) throws IOException {
      this.delegate.writeRaw(text, offset, len);
   }

   public void writeRaw(SerializableString raw) throws IOException {
      this.delegate.writeRaw(raw);
   }

   public void writeRaw(char[] text, int offset, int len) throws IOException {
      this.delegate.writeRaw(text, offset, len);
   }

   public void writeRaw(char c) throws IOException {
      this.delegate.writeRaw(c);
   }

   public void writeRawValue(String text) throws IOException {
      this.delegate.writeRawValue(text);
   }

   public void writeRawValue(String text, int offset, int len) throws IOException {
      this.delegate.writeRawValue(text, offset, len);
   }

   public void writeRawValue(char[] text, int offset, int len) throws IOException {
      this.delegate.writeRawValue(text, offset, len);
   }

   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
      this.delegate.writeBinary(b64variant, data, offset, len);
   }

   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException {
      return this.delegate.writeBinary(b64variant, data, dataLength);
   }

   public void writeNumber(short v) throws IOException {
      this.delegate.writeNumber(v);
   }

   public void writeNumber(int v) throws IOException {
      this.delegate.writeNumber(v);
   }

   public void writeNumber(long v) throws IOException {
      this.delegate.writeNumber(v);
   }

   public void writeNumber(BigInteger v) throws IOException {
      this.delegate.writeNumber(v);
   }

   public void writeNumber(double v) throws IOException {
      this.delegate.writeNumber(v);
   }

   public void writeNumber(float v) throws IOException {
      this.delegate.writeNumber(v);
   }

   public void writeNumber(BigDecimal v) throws IOException {
      this.delegate.writeNumber(v);
   }

   public void writeNumber(String encodedValue) throws IOException, UnsupportedOperationException {
      this.delegate.writeNumber(encodedValue);
   }

   public void writeBoolean(boolean state) throws IOException {
      this.delegate.writeBoolean(state);
   }

   public void writeNull() throws IOException {
      this.delegate.writeNull();
   }

   public void writeOmittedField(String fieldName) throws IOException {
      this.delegate.writeOmittedField(fieldName);
   }

   public void writeObjectId(Object id) throws IOException {
      this.delegate.writeObjectId(id);
   }

   public void writeObjectRef(Object id) throws IOException {
      this.delegate.writeObjectRef(id);
   }

   public void writeTypeId(Object id) throws IOException {
      this.delegate.writeTypeId(id);
   }

   public void writeEmbeddedObject(Object object) throws IOException {
      this.delegate.writeEmbeddedObject(object);
   }

   public void writeObject(Object pojo) throws IOException {
      if (this.delegateCopyMethods) {
         this.delegate.writeObject(pojo);
      } else {
         if (pojo == null) {
            this.writeNull();
         } else {
            ObjectCodec c = this.getCodec();
            if (c != null) {
               c.writeValue(this, pojo);
               return;
            }

            this._writeSimpleObject(pojo);
         }

      }
   }

   public void writeTree(TreeNode tree) throws IOException {
      if (this.delegateCopyMethods) {
         this.delegate.writeTree(tree);
      } else {
         if (tree == null) {
            this.writeNull();
         } else {
            ObjectCodec c = this.getCodec();
            if (c == null) {
               throw new IllegalStateException("No ObjectCodec defined");
            }

            c.writeTree(this, tree);
         }

      }
   }

   public void copyCurrentEvent(JsonParser p) throws IOException {
      if (this.delegateCopyMethods) {
         this.delegate.copyCurrentEvent(p);
      } else {
         super.copyCurrentEvent(p);
      }

   }

   public void copyCurrentStructure(JsonParser p) throws IOException {
      if (this.delegateCopyMethods) {
         this.delegate.copyCurrentStructure(p);
      } else {
         super.copyCurrentStructure(p);
      }

   }

   public JsonStreamContext getOutputContext() {
      return this.delegate.getOutputContext();
   }

   public void flush() throws IOException {
      this.delegate.flush();
   }

   public void close() throws IOException {
      this.delegate.close();
   }

   public boolean isClosed() {
      return this.delegate.isClosed();
   }
}
