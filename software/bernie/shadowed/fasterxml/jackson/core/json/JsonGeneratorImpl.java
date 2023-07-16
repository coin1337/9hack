package software.bernie.shadowed.fasterxml.jackson.core.json;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.ObjectCodec;
import software.bernie.shadowed.fasterxml.jackson.core.SerializableString;
import software.bernie.shadowed.fasterxml.jackson.core.Version;
import software.bernie.shadowed.fasterxml.jackson.core.base.GeneratorBase;
import software.bernie.shadowed.fasterxml.jackson.core.io.CharTypes;
import software.bernie.shadowed.fasterxml.jackson.core.io.CharacterEscapes;
import software.bernie.shadowed.fasterxml.jackson.core.io.IOContext;
import software.bernie.shadowed.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import software.bernie.shadowed.fasterxml.jackson.core.util.VersionUtil;

public abstract class JsonGeneratorImpl extends GeneratorBase {
   protected static final int[] sOutputEscapes = CharTypes.get7BitOutputEscapes();
   protected final IOContext _ioContext;
   protected int[] _outputEscapes;
   protected int _maximumNonEscapedChar;
   protected CharacterEscapes _characterEscapes;
   protected SerializableString _rootValueSeparator;
   protected boolean _cfgUnqNames;

   public JsonGeneratorImpl(IOContext ctxt, int features, ObjectCodec codec) {
      super(features, codec);
      this._outputEscapes = sOutputEscapes;
      this._rootValueSeparator = DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
      this._ioContext = ctxt;
      if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(features)) {
         this._maximumNonEscapedChar = 127;
      }

      this._cfgUnqNames = !JsonGenerator.Feature.QUOTE_FIELD_NAMES.enabledIn(features);
   }

   public Version version() {
      return VersionUtil.versionFor(this.getClass());
   }

   public JsonGenerator enable(JsonGenerator.Feature f) {
      super.enable(f);
      if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES) {
         this._cfgUnqNames = false;
      }

      return this;
   }

   public JsonGenerator disable(JsonGenerator.Feature f) {
      super.disable(f);
      if (f == JsonGenerator.Feature.QUOTE_FIELD_NAMES) {
         this._cfgUnqNames = true;
      }

      return this;
   }

   protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
      super._checkStdFeatureChanges(newFeatureFlags, changedFeatures);
      this._cfgUnqNames = !JsonGenerator.Feature.QUOTE_FIELD_NAMES.enabledIn(newFeatureFlags);
   }

   public JsonGenerator setHighestNonEscapedChar(int charCode) {
      this._maximumNonEscapedChar = charCode < 0 ? 0 : charCode;
      return this;
   }

   public int getHighestEscapedChar() {
      return this._maximumNonEscapedChar;
   }

   public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
      this._characterEscapes = esc;
      if (esc == null) {
         this._outputEscapes = sOutputEscapes;
      } else {
         this._outputEscapes = esc.getEscapeCodesForAscii();
      }

      return this;
   }

   public CharacterEscapes getCharacterEscapes() {
      return this._characterEscapes;
   }

   public JsonGenerator setRootValueSeparator(SerializableString sep) {
      this._rootValueSeparator = sep;
      return this;
   }

   public final void writeStringField(String fieldName, String value) throws IOException {
      this.writeFieldName(fieldName);
      this.writeString(value);
   }

   protected void _verifyPrettyValueWrite(String typeMsg, int status) throws IOException {
      switch(status) {
      case 0:
         if (this._writeContext.inArray()) {
            this._cfgPrettyPrinter.beforeArrayValues(this);
         } else if (this._writeContext.inObject()) {
            this._cfgPrettyPrinter.beforeObjectEntries(this);
         }
         break;
      case 1:
         this._cfgPrettyPrinter.writeArrayValueSeparator(this);
         break;
      case 2:
         this._cfgPrettyPrinter.writeObjectFieldValueSeparator(this);
         break;
      case 3:
         this._cfgPrettyPrinter.writeRootValueSeparator(this);
         break;
      case 4:
      default:
         this._throwInternal();
         break;
      case 5:
         this._reportCantWriteValueExpectName(typeMsg);
      }

   }

   protected void _reportCantWriteValueExpectName(String typeMsg) throws IOException {
      this._reportError(String.format("Can not %s, expecting field name (context: %s)", typeMsg, this._writeContext.typeDesc()));
   }
}
