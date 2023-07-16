package software.bernie.shadowed.fasterxml.jackson.databind.deser;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedField;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public class SettableAnyProperty implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final BeanProperty _property;
   protected final AnnotatedMember _setter;
   final boolean _setterIsField;
   protected final JavaType _type;
   protected JsonDeserializer<Object> _valueDeserializer;
   protected final TypeDeserializer _valueTypeDeserializer;
   protected final KeyDeserializer _keyDeserializer;

   public SettableAnyProperty(BeanProperty property, AnnotatedMember setter, JavaType type, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser) {
      this._property = property;
      this._setter = setter;
      this._type = type;
      this._valueDeserializer = valueDeser;
      this._valueTypeDeserializer = typeDeser;
      this._keyDeserializer = keyDeser;
      this._setterIsField = setter instanceof AnnotatedField;
   }

   /** @deprecated */
   @Deprecated
   public SettableAnyProperty(BeanProperty property, AnnotatedMember setter, JavaType type, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser) {
      this(property, setter, type, (KeyDeserializer)null, valueDeser, typeDeser);
   }

   public SettableAnyProperty withValueDeserializer(JsonDeserializer<Object> deser) {
      return new SettableAnyProperty(this._property, this._setter, this._type, this._keyDeserializer, deser, this._valueTypeDeserializer);
   }

   public void fixAccess(DeserializationConfig config) {
      this._setter.fixAccess(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
   }

   Object readResolve() {
      if (this._setter != null && this._setter.getAnnotated() != null) {
         return this;
      } else {
         throw new IllegalArgumentException("Missing method (broken JDK (de)serialization?)");
      }
   }

   public BeanProperty getProperty() {
      return this._property;
   }

   public boolean hasValueDeserializer() {
      return this._valueDeserializer != null;
   }

   public JavaType getType() {
      return this._type;
   }

   public final void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance, String propName) throws IOException {
      try {
         Object key = this._keyDeserializer == null ? propName : this._keyDeserializer.deserializeKey(propName, ctxt);
         this.set(instance, key, this.deserialize(p, ctxt));
      } catch (UnresolvedForwardReference var7) {
         if (this._valueDeserializer.getObjectIdReader() == null) {
            throw JsonMappingException.from((JsonParser)p, "Unresolved forward reference but no identity info.", var7);
         }

         SettableAnyProperty.AnySetterReferring referring = new SettableAnyProperty.AnySetterReferring(this, var7, this._type.getRawClass(), instance, propName);
         var7.getRoid().appendReferring(referring);
      }

   }

   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.VALUE_NULL) {
         return this._valueDeserializer.getNullValue(ctxt);
      } else {
         return this._valueTypeDeserializer != null ? this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer) : this._valueDeserializer.deserialize(p, ctxt);
      }
   }

   public void set(Object instance, Object propName, Object value) throws IOException {
      try {
         if (this._setterIsField) {
            AnnotatedField field = (AnnotatedField)this._setter;
            Map<Object, Object> val = (Map)field.getValue(instance);
            if (val != null) {
               val.put(propName, value);
            }
         } else {
            ((AnnotatedMethod)this._setter).callOnWith(instance, propName, value);
         }
      } catch (Exception var6) {
         this._throwAsIOE(var6, propName, value);
      }

   }

   protected void _throwAsIOE(Exception e, Object propName, Object value) throws IOException {
      if (e instanceof IllegalArgumentException) {
         String actType = ClassUtil.classNameOf(value);
         StringBuilder msg = (new StringBuilder("Problem deserializing \"any\" property '")).append(propName);
         msg.append("' of class " + this.getClassName() + " (expected type: ").append(this._type);
         msg.append("; actual type: ").append(actType).append(")");
         String origMsg = e.getMessage();
         if (origMsg != null) {
            msg.append(", problem: ").append(origMsg);
         } else {
            msg.append(" (no error message provided)");
         }

         throw new JsonMappingException((Closeable)null, msg.toString(), e);
      } else {
         ClassUtil.throwIfIOE(e);
         ClassUtil.throwIfRTE(e);
         Throwable t = ClassUtil.getRootCause(e);
         throw new JsonMappingException((Closeable)null, t.getMessage(), t);
      }
   }

   private String getClassName() {
      return this._setter.getDeclaringClass().getName();
   }

   public String toString() {
      return "[any property on class " + this.getClassName() + "]";
   }

   private static class AnySetterReferring extends ReadableObjectId.Referring {
      private final SettableAnyProperty _parent;
      private final Object _pojo;
      private final String _propName;

      public AnySetterReferring(SettableAnyProperty parent, UnresolvedForwardReference reference, Class<?> type, Object instance, String propName) {
         super(reference, type);
         this._parent = parent;
         this._pojo = instance;
         this._propName = propName;
      }

      public void handleResolvedForwardReference(Object id, Object value) throws IOException {
         if (!this.hasId(id)) {
            throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id.toString() + "] that wasn't previously registered.");
         } else {
            this._parent.set(this._pojo, this._propName, value);
         }
      }
   }
}
