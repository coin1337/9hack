package software.bernie.shadowed.fasterxml.jackson.databind.deser.impl;

import java.io.Serializable;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.NullValueProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.util.AccessPattern;

public class NullsConstantProvider implements NullValueProvider, Serializable {
   private static final long serialVersionUID = 1L;
   private static final NullsConstantProvider SKIPPER = new NullsConstantProvider((Object)null);
   private static final NullsConstantProvider NULLER = new NullsConstantProvider((Object)null);
   protected final Object _nullValue;
   protected final AccessPattern _access;

   protected NullsConstantProvider(Object nvl) {
      this._nullValue = nvl;
      this._access = this._nullValue == null ? AccessPattern.ALWAYS_NULL : AccessPattern.CONSTANT;
   }

   public static NullsConstantProvider skipper() {
      return SKIPPER;
   }

   public static NullsConstantProvider nuller() {
      return NULLER;
   }

   public static NullsConstantProvider forValue(Object nvl) {
      return nvl == null ? NULLER : new NullsConstantProvider(nvl);
   }

   public static boolean isSkipper(NullValueProvider p) {
      return p == SKIPPER;
   }

   public static boolean isNuller(NullValueProvider p) {
      return p == NULLER;
   }

   public AccessPattern getNullAccessPattern() {
      return this._access;
   }

   public Object getNullValue(DeserializationContext ctxt) {
      return this._nullValue;
   }
}
