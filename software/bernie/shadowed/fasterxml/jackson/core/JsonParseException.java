package software.bernie.shadowed.fasterxml.jackson.core;

import software.bernie.shadowed.fasterxml.jackson.core.util.RequestPayload;

public class JsonParseException extends JsonProcessingException {
   private static final long serialVersionUID = 2L;
   protected transient JsonParser _processor;
   protected RequestPayload _requestPayload;

   /** @deprecated */
   @Deprecated
   public JsonParseException(String msg, JsonLocation loc) {
      super(msg, loc);
   }

   /** @deprecated */
   @Deprecated
   public JsonParseException(String msg, JsonLocation loc, Throwable root) {
      super(msg, loc, root);
   }

   public JsonParseException(JsonParser p, String msg) {
      super(msg, p == null ? null : p.getCurrentLocation());
      this._processor = p;
   }

   public JsonParseException(JsonParser p, String msg, Throwable root) {
      super(msg, p == null ? null : p.getCurrentLocation(), root);
      this._processor = p;
   }

   public JsonParseException(JsonParser p, String msg, JsonLocation loc) {
      super(msg, loc);
      this._processor = p;
   }

   public JsonParseException(JsonParser p, String msg, JsonLocation loc, Throwable root) {
      super(msg, loc, root);
      this._processor = p;
   }

   public JsonParseException withParser(JsonParser p) {
      this._processor = p;
      return this;
   }

   public JsonParseException withRequestPayload(RequestPayload p) {
      this._requestPayload = p;
      return this;
   }

   public JsonParser getProcessor() {
      return this._processor;
   }

   public RequestPayload getRequestPayload() {
      return this._requestPayload;
   }

   public String getRequestPayloadAsString() {
      return this._requestPayload != null ? this._requestPayload.toString() : null;
   }

   public String getMessage() {
      String msg = super.getMessage();
      if (this._requestPayload != null) {
         msg = msg + "\nRequest payload : " + this._requestPayload.toString();
      }

      return msg;
   }
}
