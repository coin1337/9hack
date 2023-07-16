package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ValueInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

@JacksonStdImpl
public class StdValueInstantiator extends ValueInstantiator implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final String _valueTypeDesc;
   protected final Class<?> _valueClass;
   protected AnnotatedWithParams _defaultCreator;
   protected AnnotatedWithParams _withArgsCreator;
   protected SettableBeanProperty[] _constructorArguments;
   protected JavaType _delegateType;
   protected AnnotatedWithParams _delegateCreator;
   protected SettableBeanProperty[] _delegateArguments;
   protected JavaType _arrayDelegateType;
   protected AnnotatedWithParams _arrayDelegateCreator;
   protected SettableBeanProperty[] _arrayDelegateArguments;
   protected AnnotatedWithParams _fromStringCreator;
   protected AnnotatedWithParams _fromIntCreator;
   protected AnnotatedWithParams _fromLongCreator;
   protected AnnotatedWithParams _fromDoubleCreator;
   protected AnnotatedWithParams _fromBooleanCreator;
   protected AnnotatedParameter _incompleteParameter;

   /** @deprecated */
   @Deprecated
   public StdValueInstantiator(DeserializationConfig config, Class<?> valueType) {
      this._valueTypeDesc = ClassUtil.nameOf(valueType);
      this._valueClass = valueType == null ? Object.class : valueType;
   }

   public StdValueInstantiator(DeserializationConfig config, JavaType valueType) {
      this._valueTypeDesc = valueType == null ? "UNKNOWN TYPE" : valueType.toString();
      this._valueClass = valueType == null ? Object.class : valueType.getRawClass();
   }

   protected StdValueInstantiator(StdValueInstantiator src) {
      this._valueTypeDesc = src._valueTypeDesc;
      this._valueClass = src._valueClass;
      this._defaultCreator = src._defaultCreator;
      this._constructorArguments = src._constructorArguments;
      this._withArgsCreator = src._withArgsCreator;
      this._delegateType = src._delegateType;
      this._delegateCreator = src._delegateCreator;
      this._delegateArguments = src._delegateArguments;
      this._arrayDelegateType = src._arrayDelegateType;
      this._arrayDelegateCreator = src._arrayDelegateCreator;
      this._arrayDelegateArguments = src._arrayDelegateArguments;
      this._fromStringCreator = src._fromStringCreator;
      this._fromIntCreator = src._fromIntCreator;
      this._fromLongCreator = src._fromLongCreator;
      this._fromDoubleCreator = src._fromDoubleCreator;
      this._fromBooleanCreator = src._fromBooleanCreator;
   }

   public void configureFromObjectSettings(AnnotatedWithParams defaultCreator, AnnotatedWithParams delegateCreator, JavaType delegateType, SettableBeanProperty[] delegateArgs, AnnotatedWithParams withArgsCreator, SettableBeanProperty[] constructorArgs) {
      this._defaultCreator = defaultCreator;
      this._delegateCreator = delegateCreator;
      this._delegateType = delegateType;
      this._delegateArguments = delegateArgs;
      this._withArgsCreator = withArgsCreator;
      this._constructorArguments = constructorArgs;
   }

   public void configureFromArraySettings(AnnotatedWithParams arrayDelegateCreator, JavaType arrayDelegateType, SettableBeanProperty[] arrayDelegateArgs) {
      this._arrayDelegateCreator = arrayDelegateCreator;
      this._arrayDelegateType = arrayDelegateType;
      this._arrayDelegateArguments = arrayDelegateArgs;
   }

   public void configureFromStringCreator(AnnotatedWithParams creator) {
      this._fromStringCreator = creator;
   }

   public void configureFromIntCreator(AnnotatedWithParams creator) {
      this._fromIntCreator = creator;
   }

   public void configureFromLongCreator(AnnotatedWithParams creator) {
      this._fromLongCreator = creator;
   }

   public void configureFromDoubleCreator(AnnotatedWithParams creator) {
      this._fromDoubleCreator = creator;
   }

   public void configureFromBooleanCreator(AnnotatedWithParams creator) {
      this._fromBooleanCreator = creator;
   }

   public void configureIncompleteParameter(AnnotatedParameter parameter) {
      this._incompleteParameter = parameter;
   }

   public String getValueTypeDesc() {
      return this._valueTypeDesc;
   }

   public Class<?> getValueClass() {
      return this._valueClass;
   }

   public boolean canCreateFromString() {
      return this._fromStringCreator != null;
   }

   public boolean canCreateFromInt() {
      return this._fromIntCreator != null;
   }

   public boolean canCreateFromLong() {
      return this._fromLongCreator != null;
   }

   public boolean canCreateFromDouble() {
      return this._fromDoubleCreator != null;
   }

   public boolean canCreateFromBoolean() {
      return this._fromBooleanCreator != null;
   }

   public boolean canCreateUsingDefault() {
      return this._defaultCreator != null;
   }

   public boolean canCreateUsingDelegate() {
      return this._delegateType != null;
   }

   public boolean canCreateUsingArrayDelegate() {
      return this._arrayDelegateType != null;
   }

   public boolean canCreateFromObjectWith() {
      return this._withArgsCreator != null;
   }

   public JavaType getDelegateType(DeserializationConfig config) {
      return this._delegateType;
   }

   public JavaType getArrayDelegateType(DeserializationConfig config) {
      return this._arrayDelegateType;
   }

   public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
      return this._constructorArguments;
   }

   public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
      if (this._defaultCreator == null) {
         return super.createUsingDefault(ctxt);
      } else {
         try {
            return this._defaultCreator.call();
         } catch (Exception var3) {
            return ctxt.handleInstantiationProblem(this._valueClass, (Object)null, this.rewrapCtorProblem(ctxt, var3));
         }
      }
   }

   public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
      if (this._withArgsCreator == null) {
         return super.createFromObjectWith(ctxt, args);
      } else {
         try {
            return this._withArgsCreator.call(args);
         } catch (Exception var4) {
            return ctxt.handleInstantiationProblem(this._valueClass, args, this.rewrapCtorProblem(ctxt, var4));
         }
      }
   }

   public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
      return this._delegateCreator == null && this._arrayDelegateCreator != null ? this._createUsingDelegate(this._arrayDelegateCreator, this._arrayDelegateArguments, ctxt, delegate) : this._createUsingDelegate(this._delegateCreator, this._delegateArguments, ctxt, delegate);
   }

   public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
      return this._arrayDelegateCreator == null && this._delegateCreator != null ? this.createUsingDelegate(ctxt, delegate) : this._createUsingDelegate(this._arrayDelegateCreator, this._arrayDelegateArguments, ctxt, delegate);
   }

   public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
      if (this._fromStringCreator == null) {
         return this._createFromStringFallbacks(ctxt, value);
      } else {
         try {
            return this._fromStringCreator.call1(value);
         } catch (Throwable var4) {
            return ctxt.handleInstantiationProblem(this._fromStringCreator.getDeclaringClass(), value, this.rewrapCtorProblem(ctxt, var4));
         }
      }
   }

   public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
      if (this._fromIntCreator != null) {
         Integer arg = value;

         try {
            return this._fromIntCreator.call1(arg);
         } catch (Throwable var5) {
            return ctxt.handleInstantiationProblem(this._fromIntCreator.getDeclaringClass(), arg, this.rewrapCtorProblem(ctxt, var5));
         }
      } else if (this._fromLongCreator != null) {
         Long arg = (long)value;

         try {
            return this._fromLongCreator.call1(arg);
         } catch (Throwable var6) {
            return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, this.rewrapCtorProblem(ctxt, var6));
         }
      } else {
         return super.createFromInt(ctxt, value);
      }
   }

   public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
      if (this._fromLongCreator == null) {
         return super.createFromLong(ctxt, value);
      } else {
         Long arg = value;

         try {
            return this._fromLongCreator.call1(arg);
         } catch (Throwable var6) {
            return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, this.rewrapCtorProblem(ctxt, var6));
         }
      }
   }

   public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
      if (this._fromDoubleCreator == null) {
         return super.createFromDouble(ctxt, value);
      } else {
         Double arg = value;

         try {
            return this._fromDoubleCreator.call1(arg);
         } catch (Throwable var6) {
            return ctxt.handleInstantiationProblem(this._fromDoubleCreator.getDeclaringClass(), arg, this.rewrapCtorProblem(ctxt, var6));
         }
      }
   }

   public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
      if (this._fromBooleanCreator == null) {
         return super.createFromBoolean(ctxt, value);
      } else {
         Boolean arg = value;

         try {
            return this._fromBooleanCreator.call1(arg);
         } catch (Throwable var5) {
            return ctxt.handleInstantiationProblem(this._fromBooleanCreator.getDeclaringClass(), arg, this.rewrapCtorProblem(ctxt, var5));
         }
      }
   }

   public AnnotatedWithParams getDelegateCreator() {
      return this._delegateCreator;
   }

   public AnnotatedWithParams getArrayDelegateCreator() {
      return this._arrayDelegateCreator;
   }

   public AnnotatedWithParams getDefaultCreator() {
      return this._defaultCreator;
   }

   public AnnotatedWithParams getWithArgsCreator() {
      return this._withArgsCreator;
   }

   public AnnotatedParameter getIncompleteParameter() {
      return this._incompleteParameter;
   }

   /** @deprecated */
   @Deprecated
   protected JsonMappingException wrapException(Throwable t) {
      for(Throwable curr = t; curr != null; curr = curr.getCause()) {
         if (curr instanceof JsonMappingException) {
            return (JsonMappingException)curr;
         }
      }

      return new JsonMappingException((Closeable)null, "Instantiation of " + this.getValueTypeDesc() + " value failed: " + t.getMessage(), t);
   }

   protected JsonMappingException unwrapAndWrapException(DeserializationContext ctxt, Throwable t) {
      for(Throwable curr = t; curr != null; curr = curr.getCause()) {
         if (curr instanceof JsonMappingException) {
            return (JsonMappingException)curr;
         }
      }

      return ctxt.instantiationException(this.getValueClass(), t);
   }

   protected JsonMappingException wrapAsJsonMappingException(DeserializationContext ctxt, Throwable t) {
      return t instanceof JsonMappingException ? (JsonMappingException)t : ctxt.instantiationException(this.getValueClass(), t);
   }

   protected JsonMappingException rewrapCtorProblem(DeserializationContext ctxt, Throwable t) {
      if (t instanceof ExceptionInInitializerError || t instanceof InvocationTargetException) {
         Throwable cause = t.getCause();
         if (cause != null) {
            t = cause;
         }
      }

      return this.wrapAsJsonMappingException(ctxt, t);
   }

   private Object _createUsingDelegate(AnnotatedWithParams delegateCreator, SettableBeanProperty[] delegateArguments, DeserializationContext ctxt, Object delegate) throws IOException {
      if (delegateCreator == null) {
         throw new IllegalStateException("No delegate constructor for " + this.getValueTypeDesc());
      } else {
         try {
            if (delegateArguments == null) {
               return delegateCreator.call1(delegate);
            } else {
               int len = delegateArguments.length;
               Object[] args = new Object[len];

               for(int i = 0; i < len; ++i) {
                  SettableBeanProperty prop = delegateArguments[i];
                  if (prop == null) {
                     args[i] = delegate;
                  } else {
                     args[i] = ctxt.findInjectableValue(prop.getInjectableValueId(), prop, (Object)null);
                  }
               }

               return delegateCreator.call(args);
            }
         } catch (Throwable var9) {
            throw this.rewrapCtorProblem(ctxt, var9);
         }
      }
   }
}
