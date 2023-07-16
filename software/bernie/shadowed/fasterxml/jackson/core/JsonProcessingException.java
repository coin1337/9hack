package software.bernie.shadowed.fasterxml.jackson.core;

import java.io.IOException;

public class JsonProcessingException extends IOException {
   static final long serialVersionUID = 123L;
   protected JsonLocation _location;

   protected JsonProcessingException(String msg, JsonLocation loc, Throwable rootCause) {
      super(msg);
      if (rootCause != null) {
         this.initCause(rootCause);
      }

      this._location = loc;
   }

   protected JsonProcessingException(String msg) {
      super(msg);
   }

   protected JsonProcessingException(String msg, JsonLocation loc) {
      this(msg, loc, (Throwable)null);
   }

   protected JsonProcessingException(String msg, Throwable rootCause) {
      this(msg, (JsonLocation)null, rootCause);
   }

   protected JsonProcessingException(Throwable rootCause) {
      this((String)null, (JsonLocation)null, rootCause);
   }

   public JsonLocation getLocation() {
      return this._location;
   }

   public void clearLocation() {
      this._location = null;
   }

   public String getOriginalMessage() {
      return super.getMessage();
   }

   public Object getProcessor() {
      return null;
   }

   protected String getMessageSuffix() {
      return null;
   }

   public String getMessage() {
      String msg = super.getMessage();
      if (msg == null) {
         msg = "N/A";
      }

      JsonLocation loc = this.getLocation();
      String suffix = this.getMessageSuffix();
      if (loc != null || suffix != null) {
         StringBuilder sb = new StringBuilder(100);
         sb.append(msg);
         if (suffix != null) {
            sb.append(suffix);
         }

         if (loc != null) {
            sb.append('\n');
            sb.append(" at ");
            sb.append(loc.toString());
         }

         msg = sb.toString();
      }

      return msg;
   }

   public String toString() {
      return this.getClass().getName() + ": " + this.getMessage();
   }
}
