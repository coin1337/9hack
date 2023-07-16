package software.bernie.shadowed.fasterxml.jackson.core;

public class JsonGenerationException extends JsonProcessingException {
   private static final long serialVersionUID = 123L;
   protected transient JsonGenerator _processor;

   /** @deprecated */
   @Deprecated
   public JsonGenerationException(Throwable rootCause) {
      super(rootCause);
   }

   /** @deprecated */
   @Deprecated
   public JsonGenerationException(String msg) {
      super(msg, (JsonLocation)null);
   }

   /** @deprecated */
   @Deprecated
   public JsonGenerationException(String msg, Throwable rootCause) {
      super(msg, (JsonLocation)null, rootCause);
   }

   public JsonGenerationException(Throwable rootCause, JsonGenerator g) {
      super(rootCause);
      this._processor = g;
   }

   public JsonGenerationException(String msg, JsonGenerator g) {
      super(msg, (JsonLocation)null);
      this._processor = g;
   }

   public JsonGenerationException(String msg, Throwable rootCause, JsonGenerator g) {
      super(msg, (JsonLocation)null, rootCause);
      this._processor = g;
   }

   public JsonGenerationException withGenerator(JsonGenerator g) {
      this._processor = g;
      return this;
   }

   public JsonGenerator getProcessor() {
      return this._processor;
   }
}
