package software.bernie.shadowed.fasterxml.jackson.databind.ser;

import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.std.StdSerializer;

public abstract class ContainerSerializer<T> extends StdSerializer<T> {
   protected ContainerSerializer(Class<T> t) {
      super(t);
   }

   protected ContainerSerializer(JavaType fullType) {
      super(fullType);
   }

   protected ContainerSerializer(Class<?> t, boolean dummy) {
      super(t, dummy);
   }

   protected ContainerSerializer(ContainerSerializer<?> src) {
      super(src._handledType, false);
   }

   public ContainerSerializer<?> withValueTypeSerializer(TypeSerializer vts) {
      return vts == null ? this : this._withValueTypeSerializer(vts);
   }

   public abstract JavaType getContentType();

   public abstract JsonSerializer<?> getContentSerializer();

   public abstract boolean hasSingleElement(T var1);

   protected abstract ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer var1);

   /** @deprecated */
   @Deprecated
   protected boolean hasContentTypeAnnotation(SerializerProvider provider, BeanProperty property) {
      return false;
   }
}
