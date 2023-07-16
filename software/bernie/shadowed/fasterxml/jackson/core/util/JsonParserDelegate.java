package software.bernie.shadowed.fasterxml.jackson.core.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variant;
import software.bernie.shadowed.fasterxml.jackson.core.FormatSchema;
import software.bernie.shadowed.fasterxml.jackson.core.JsonLocation;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonStreamContext;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.ObjectCodec;
import software.bernie.shadowed.fasterxml.jackson.core.Version;

public class JsonParserDelegate extends JsonParser {
   protected JsonParser delegate;

   public JsonParserDelegate(JsonParser d) {
      this.delegate = d;
   }

   public Object getCurrentValue() {
      return this.delegate.getCurrentValue();
   }

   public void setCurrentValue(Object v) {
      this.delegate.setCurrentValue(v);
   }

   public void setCodec(ObjectCodec c) {
      this.delegate.setCodec(c);
   }

   public ObjectCodec getCodec() {
      return this.delegate.getCodec();
   }

   public JsonParser enable(JsonParser.Feature f) {
      this.delegate.enable(f);
      return this;
   }

   public JsonParser disable(JsonParser.Feature f) {
      this.delegate.disable(f);
      return this;
   }

   public boolean isEnabled(JsonParser.Feature f) {
      return this.delegate.isEnabled(f);
   }

   public int getFeatureMask() {
      return this.delegate.getFeatureMask();
   }

   /** @deprecated */
   @Deprecated
   public JsonParser setFeatureMask(int mask) {
      this.delegate.setFeatureMask(mask);
      return this;
   }

   public JsonParser overrideStdFeatures(int values, int mask) {
      this.delegate.overrideStdFeatures(values, mask);
      return this;
   }

   public JsonParser overrideFormatFeatures(int values, int mask) {
      this.delegate.overrideFormatFeatures(values, mask);
      return this;
   }

   public FormatSchema getSchema() {
      return this.delegate.getSchema();
   }

   public void setSchema(FormatSchema schema) {
      this.delegate.setSchema(schema);
   }

   public boolean canUseSchema(FormatSchema schema) {
      return this.delegate.canUseSchema(schema);
   }

   public Version version() {
      return this.delegate.version();
   }

   public Object getInputSource() {
      return this.delegate.getInputSource();
   }

   public boolean requiresCustomCodec() {
      return this.delegate.requiresCustomCodec();
   }

   public void close() throws IOException {
      this.delegate.close();
   }

   public boolean isClosed() {
      return this.delegate.isClosed();
   }

   public JsonToken currentToken() {
      return this.delegate.currentToken();
   }

   public int currentTokenId() {
      return this.delegate.currentTokenId();
   }

   public JsonToken getCurrentToken() {
      return this.delegate.getCurrentToken();
   }

   public int getCurrentTokenId() {
      return this.delegate.getCurrentTokenId();
   }

   public boolean hasCurrentToken() {
      return this.delegate.hasCurrentToken();
   }

   public boolean hasTokenId(int id) {
      return this.delegate.hasTokenId(id);
   }

   public boolean hasToken(JsonToken t) {
      return this.delegate.hasToken(t);
   }

   public String getCurrentName() throws IOException {
      return this.delegate.getCurrentName();
   }

   public JsonLocation getCurrentLocation() {
      return this.delegate.getCurrentLocation();
   }

   public JsonStreamContext getParsingContext() {
      return this.delegate.getParsingContext();
   }

   public boolean isExpectedStartArrayToken() {
      return this.delegate.isExpectedStartArrayToken();
   }

   public boolean isExpectedStartObjectToken() {
      return this.delegate.isExpectedStartObjectToken();
   }

   public boolean isNaN() throws IOException {
      return this.delegate.isNaN();
   }

   public void clearCurrentToken() {
      this.delegate.clearCurrentToken();
   }

   public JsonToken getLastClearedToken() {
      return this.delegate.getLastClearedToken();
   }

   public void overrideCurrentName(String name) {
      this.delegate.overrideCurrentName(name);
   }

   public String getText() throws IOException {
      return this.delegate.getText();
   }

   public boolean hasTextCharacters() {
      return this.delegate.hasTextCharacters();
   }

   public char[] getTextCharacters() throws IOException {
      return this.delegate.getTextCharacters();
   }

   public int getTextLength() throws IOException {
      return this.delegate.getTextLength();
   }

   public int getTextOffset() throws IOException {
      return this.delegate.getTextOffset();
   }

   public int getText(Writer writer) throws IOException, UnsupportedOperationException {
      return this.delegate.getText(writer);
   }

   public BigInteger getBigIntegerValue() throws IOException {
      return this.delegate.getBigIntegerValue();
   }

   public boolean getBooleanValue() throws IOException {
      return this.delegate.getBooleanValue();
   }

   public byte getByteValue() throws IOException {
      return this.delegate.getByteValue();
   }

   public short getShortValue() throws IOException {
      return this.delegate.getShortValue();
   }

   public BigDecimal getDecimalValue() throws IOException {
      return this.delegate.getDecimalValue();
   }

   public double getDoubleValue() throws IOException {
      return this.delegate.getDoubleValue();
   }

   public float getFloatValue() throws IOException {
      return this.delegate.getFloatValue();
   }

   public int getIntValue() throws IOException {
      return this.delegate.getIntValue();
   }

   public long getLongValue() throws IOException {
      return this.delegate.getLongValue();
   }

   public JsonParser.NumberType getNumberType() throws IOException {
      return this.delegate.getNumberType();
   }

   public Number getNumberValue() throws IOException {
      return this.delegate.getNumberValue();
   }

   public int getValueAsInt() throws IOException {
      return this.delegate.getValueAsInt();
   }

   public int getValueAsInt(int defaultValue) throws IOException {
      return this.delegate.getValueAsInt(defaultValue);
   }

   public long getValueAsLong() throws IOException {
      return this.delegate.getValueAsLong();
   }

   public long getValueAsLong(long defaultValue) throws IOException {
      return this.delegate.getValueAsLong(defaultValue);
   }

   public double getValueAsDouble() throws IOException {
      return this.delegate.getValueAsDouble();
   }

   public double getValueAsDouble(double defaultValue) throws IOException {
      return this.delegate.getValueAsDouble(defaultValue);
   }

   public boolean getValueAsBoolean() throws IOException {
      return this.delegate.getValueAsBoolean();
   }

   public boolean getValueAsBoolean(boolean defaultValue) throws IOException {
      return this.delegate.getValueAsBoolean(defaultValue);
   }

   public String getValueAsString() throws IOException {
      return this.delegate.getValueAsString();
   }

   public String getValueAsString(String defaultValue) throws IOException {
      return this.delegate.getValueAsString(defaultValue);
   }

   public Object getEmbeddedObject() throws IOException {
      return this.delegate.getEmbeddedObject();
   }

   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
      return this.delegate.getBinaryValue(b64variant);
   }

   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
      return this.delegate.readBinaryValue(b64variant, out);
   }

   public JsonLocation getTokenLocation() {
      return this.delegate.getTokenLocation();
   }

   public JsonToken nextToken() throws IOException {
      return this.delegate.nextToken();
   }

   public JsonToken nextValue() throws IOException {
      return this.delegate.nextValue();
   }

   public void finishToken() throws IOException {
      this.delegate.finishToken();
   }

   public JsonParser skipChildren() throws IOException {
      this.delegate.skipChildren();
      return this;
   }

   public boolean canReadObjectId() {
      return this.delegate.canReadObjectId();
   }

   public boolean canReadTypeId() {
      return this.delegate.canReadTypeId();
   }

   public Object getObjectId() throws IOException {
      return this.delegate.getObjectId();
   }

   public Object getTypeId() throws IOException {
      return this.delegate.getTypeId();
   }
}
