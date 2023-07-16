package software.bernie.shadowed.fasterxml.jackson.core.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import software.bernie.shadowed.fasterxml.jackson.core.JsonFactory;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.io.IOContext;
import software.bernie.shadowed.fasterxml.jackson.core.io.MergedStream;

public class DataFormatMatcher {
   protected final InputStream _originalStream;
   protected final byte[] _bufferedData;
   protected final int _bufferedStart;
   protected final int _bufferedLength;
   protected final JsonFactory _match;
   protected final MatchStrength _matchStrength;

   protected DataFormatMatcher(InputStream in, byte[] buffered, int bufferedStart, int bufferedLength, JsonFactory match, MatchStrength strength) {
      this._originalStream = in;
      this._bufferedData = buffered;
      this._bufferedStart = bufferedStart;
      this._bufferedLength = bufferedLength;
      this._match = match;
      this._matchStrength = strength;
   }

   public boolean hasMatch() {
      return this._match != null;
   }

   public MatchStrength getMatchStrength() {
      return this._matchStrength == null ? MatchStrength.INCONCLUSIVE : this._matchStrength;
   }

   public JsonFactory getMatch() {
      return this._match;
   }

   public String getMatchedFormatName() {
      return this._match.getFormatName();
   }

   public JsonParser createParserWithMatch() throws IOException {
      if (this._match == null) {
         return null;
      } else {
         return this._originalStream == null ? this._match.createParser(this._bufferedData, this._bufferedStart, this._bufferedLength) : this._match.createParser(this.getDataStream());
      }
   }

   public InputStream getDataStream() {
      return (InputStream)(this._originalStream == null ? new ByteArrayInputStream(this._bufferedData, this._bufferedStart, this._bufferedLength) : new MergedStream((IOContext)null, this._originalStream, this._bufferedData, this._bufferedStart, this._bufferedLength));
   }
}
