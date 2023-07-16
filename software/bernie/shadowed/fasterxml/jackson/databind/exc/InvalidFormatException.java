package software.bernie.shadowed.fasterxml.jackson.databind.exc;

import software.bernie.shadowed.fasterxml.jackson.core.JsonLocation;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;

public class InvalidFormatException extends MismatchedInputException {
   private static final long serialVersionUID = 1L;
   protected final Object _value;

   /** @deprecated */
   @Deprecated
   public InvalidFormatException(String msg, Object value, Class<?> targetType) {
      super((JsonParser)null, msg);
      this._value = value;
      this._targetType = targetType;
   }

   /** @deprecated */
   @Deprecated
   public InvalidFormatException(String msg, JsonLocation loc, Object value, Class<?> targetType) {
      super((JsonParser)null, msg, (JsonLocation)loc);
      this._value = value;
      this._targetType = targetType;
   }

   public InvalidFormatException(JsonParser p, String msg, Object value, Class<?> targetType) {
      super(p, msg, targetType);
      this._value = value;
   }

   public static InvalidFormatException from(JsonParser p, String msg, Object value, Class<?> targetType) {
      return new InvalidFormatException(p, msg, value, targetType);
   }

   public Object getValue() {
      return this._value;
   }
}
