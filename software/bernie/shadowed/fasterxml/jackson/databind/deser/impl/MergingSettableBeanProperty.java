package software.bernie.shadowed.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;

public class MergingSettableBeanProperty extends SettableBeanProperty.Delegating {
   private static final long serialVersionUID = 1L;
   protected final AnnotatedMember _accessor;

   protected MergingSettableBeanProperty(SettableBeanProperty delegate, AnnotatedMember accessor) {
      super(delegate);
      this._accessor = accessor;
   }

   protected MergingSettableBeanProperty(MergingSettableBeanProperty src, SettableBeanProperty delegate) {
      super(delegate);
      this._accessor = src._accessor;
   }

   public static MergingSettableBeanProperty construct(SettableBeanProperty delegate, AnnotatedMember accessor) {
      return new MergingSettableBeanProperty(delegate, accessor);
   }

   protected SettableBeanProperty withDelegate(SettableBeanProperty d) {
      return new MergingSettableBeanProperty(d, this._accessor);
   }

   public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
      Object oldValue = this._accessor.getValue(instance);
      Object newValue;
      if (oldValue == null) {
         newValue = this.delegate.deserialize(p, ctxt);
      } else {
         newValue = this.delegate.deserializeWith(p, ctxt, oldValue);
      }

      if (newValue != oldValue) {
         this.delegate.set(instance, newValue);
      }

   }

   public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
      Object oldValue = this._accessor.getValue(instance);
      Object newValue;
      if (oldValue == null) {
         newValue = this.delegate.deserialize(p, ctxt);
      } else {
         newValue = this.delegate.deserializeWith(p, ctxt, oldValue);
      }

      return newValue != oldValue && newValue != null ? this.delegate.setAndReturn(instance, newValue) : instance;
   }

   public void set(Object instance, Object value) throws IOException {
      if (value != null) {
         this.delegate.set(instance, value);
      }

   }

   public Object setAndReturn(Object instance, Object value) throws IOException {
      return value != null ? this.delegate.setAndReturn(instance, value) : instance;
   }
}
