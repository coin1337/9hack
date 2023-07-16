package software.bernie.shadowed.fasterxml.jackson.databind.deser;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedWithParams;

public abstract class ValueInstantiator {
   public Class<?> getValueClass() {
      return Object.class;
   }

   public String getValueTypeDesc() {
      Class<?> cls = this.getValueClass();
      return cls == null ? "UNKNOWN" : cls.getName();
   }

   public boolean canInstantiate() {
      return this.canCreateUsingDefault() || this.canCreateUsingDelegate() || this.canCreateFromObjectWith() || this.canCreateFromString() || this.canCreateFromInt() || this.canCreateFromLong() || this.canCreateFromDouble() || this.canCreateFromBoolean();
   }

   public boolean canCreateFromString() {
      return false;
   }

   public boolean canCreateFromInt() {
      return false;
   }

   public boolean canCreateFromLong() {
      return false;
   }

   public boolean canCreateFromDouble() {
      return false;
   }

   public boolean canCreateFromBoolean() {
      return false;
   }

   public boolean canCreateUsingDefault() {
      return this.getDefaultCreator() != null;
   }

   public boolean canCreateUsingDelegate() {
      return false;
   }

   public boolean canCreateUsingArrayDelegate() {
      return false;
   }

   public boolean canCreateFromObjectWith() {
      return false;
   }

   public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
      return null;
   }

   public JavaType getDelegateType(DeserializationConfig config) {
      return null;
   }

   public JavaType getArrayDelegateType(DeserializationConfig config) {
      return null;
   }

   public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no default no-arguments constructor found");
   }

   public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no creator with arguments specified");
   }

   public Object createFromObjectWith(DeserializationContext ctxt, SettableBeanProperty[] props, PropertyValueBuffer buffer) throws IOException {
      return this.createFromObjectWith(ctxt, buffer.getParameters(props));
   }

   public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no delegate creator specified");
   }

   public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no array delegate creator specified");
   }

   public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
      return this._createFromStringFallbacks(ctxt, value);
   }

   public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no int/Int-argument constructor/factory method to deserialize from Number value (%s)", value);
   }

   public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no long/Long-argument constructor/factory method to deserialize from Number value (%s)", value);
   }

   public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no double/Double-argument constructor/factory method to deserialize from Number value (%s)", value);
   }

   public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no boolean/Boolean-argument constructor/factory method to deserialize from boolean value (%s)", value);
   }

   public AnnotatedWithParams getDefaultCreator() {
      return null;
   }

   public AnnotatedWithParams getDelegateCreator() {
      return null;
   }

   public AnnotatedWithParams getArrayDelegateCreator() {
      return null;
   }

   public AnnotatedWithParams getWithArgsCreator() {
      return null;
   }

   public AnnotatedParameter getIncompleteParameter() {
      return null;
   }

   protected Object _createFromStringFallbacks(DeserializationContext ctxt, String value) throws IOException {
      if (this.canCreateFromBoolean()) {
         String str = value.trim();
         if ("true".equals(str)) {
            return this.createFromBoolean(ctxt, true);
         }

         if ("false".equals(str)) {
            return this.createFromBoolean(ctxt, false);
         }
      }

      return value.length() == 0 && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) ? null : ctxt.handleMissingInstantiator(this.getValueClass(), this, ctxt.getParser(), "no String-argument constructor/factory method to deserialize from String value ('%s')", value);
   }

   public static class Base extends ValueInstantiator {
      protected final Class<?> _valueType;

      public Base(Class<?> type) {
         this._valueType = type;
      }

      public Base(JavaType type) {
         this._valueType = type.getRawClass();
      }

      public String getValueTypeDesc() {
         return this._valueType.getName();
      }

      public Class<?> getValueClass() {
         return this._valueType;
      }
   }

   public interface Gettable {
      ValueInstantiator getValueInstantiator();
   }
}
