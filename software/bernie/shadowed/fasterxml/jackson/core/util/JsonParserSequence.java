package software.bernie.shadowed.fasterxml.jackson.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;

public class JsonParserSequence extends JsonParserDelegate {
   protected final JsonParser[] _parsers;
   protected final boolean _checkForExistingToken;
   protected int _nextParserIndex;
   protected boolean _hasToken;

   /** @deprecated */
   @Deprecated
   protected JsonParserSequence(JsonParser[] parsers) {
      this(false, parsers);
   }

   protected JsonParserSequence(boolean checkForExistingToken, JsonParser[] parsers) {
      super(parsers[0]);
      this._checkForExistingToken = checkForExistingToken;
      this._hasToken = checkForExistingToken && this.delegate.hasCurrentToken();
      this._parsers = parsers;
      this._nextParserIndex = 1;
   }

   public static JsonParserSequence createFlattened(boolean checkForExistingToken, JsonParser first, JsonParser second) {
      if (!(first instanceof JsonParserSequence) && !(second instanceof JsonParserSequence)) {
         return new JsonParserSequence(checkForExistingToken, new JsonParser[]{first, second});
      } else {
         ArrayList<JsonParser> p = new ArrayList();
         if (first instanceof JsonParserSequence) {
            ((JsonParserSequence)first).addFlattenedActiveParsers(p);
         } else {
            p.add(first);
         }

         if (second instanceof JsonParserSequence) {
            ((JsonParserSequence)second).addFlattenedActiveParsers(p);
         } else {
            p.add(second);
         }

         return new JsonParserSequence(checkForExistingToken, (JsonParser[])p.toArray(new JsonParser[p.size()]));
      }
   }

   /** @deprecated */
   @Deprecated
   public static JsonParserSequence createFlattened(JsonParser first, JsonParser second) {
      return createFlattened(false, first, second);
   }

   protected void addFlattenedActiveParsers(List<JsonParser> listToAddIn) {
      int i = this._nextParserIndex - 1;

      for(int len = this._parsers.length; i < len; ++i) {
         JsonParser p = this._parsers[i];
         if (p instanceof JsonParserSequence) {
            ((JsonParserSequence)p).addFlattenedActiveParsers(listToAddIn);
         } else {
            listToAddIn.add(p);
         }
      }

   }

   public void close() throws IOException {
      do {
         this.delegate.close();
      } while(this.switchToNext());

   }

   public JsonToken nextToken() throws IOException {
      if (this.delegate == null) {
         return null;
      } else if (this._hasToken) {
         this._hasToken = false;
         return this.delegate.currentToken();
      } else {
         JsonToken t = this.delegate.nextToken();
         return t == null ? this.switchAndReturnNext() : t;
      }
   }

   public JsonParser skipChildren() throws IOException {
      if (this.delegate.currentToken() != JsonToken.START_OBJECT && this.delegate.currentToken() != JsonToken.START_ARRAY) {
         return this;
      } else {
         int open = 1;

         while(true) {
            JsonToken t = this.nextToken();
            if (t == null) {
               return this;
            }

            if (t.isStructStart()) {
               ++open;
            } else if (t.isStructEnd()) {
               --open;
               if (open == 0) {
                  return this;
               }
            }
         }
      }
   }

   public int containedParsersCount() {
      return this._parsers.length;
   }

   protected boolean switchToNext() {
      if (this._nextParserIndex < this._parsers.length) {
         this.delegate = this._parsers[this._nextParserIndex++];
         return true;
      } else {
         return false;
      }
   }

   protected JsonToken switchAndReturnNext() throws IOException {
      while(true) {
         if (this._nextParserIndex < this._parsers.length) {
            this.delegate = this._parsers[this._nextParserIndex++];
            if (this._checkForExistingToken && this.delegate.hasCurrentToken()) {
               return this.delegate.getCurrentToken();
            }

            JsonToken t = this.delegate.nextToken();
            if (t == null) {
               continue;
            }

            return t;
         }

         return null;
      }
   }
}
