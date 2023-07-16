package software.bernie.shadowed.fasterxml.jackson.core.io;

import software.bernie.shadowed.fasterxml.jackson.core.JsonParseException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;

public class JsonEOFException extends JsonParseException {
   private static final long serialVersionUID = 1L;
   protected final JsonToken _token;

   public JsonEOFException(JsonParser p, JsonToken token, String msg) {
      super(p, msg);
      this._token = token;
   }

   public JsonToken getTokenBeingDecoded() {
      return this._token;
   }
}
