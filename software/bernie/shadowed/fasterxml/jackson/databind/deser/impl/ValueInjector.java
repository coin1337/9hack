package software.bernie.shadowed.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyMetadata;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;

public class ValueInjector extends BeanProperty.Std {
   private static final long serialVersionUID = 1L;
   protected final Object _valueId;

   public ValueInjector(PropertyName propName, JavaType type, AnnotatedMember mutator, Object valueId) {
      super(propName, type, (PropertyName)null, mutator, PropertyMetadata.STD_OPTIONAL);
      this._valueId = valueId;
   }

   public Object findValue(DeserializationContext context, Object beanInstance) throws JsonMappingException {
      return context.findInjectableValue(this._valueId, this, beanInstance);
   }

   public void inject(DeserializationContext context, Object beanInstance) throws IOException {
      this._member.setValue(beanInstance, this.findValue(context, beanInstance));
   }
}
