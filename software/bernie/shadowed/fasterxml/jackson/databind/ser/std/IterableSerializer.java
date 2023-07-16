package software.bernie.shadowed.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import java.util.Iterator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ContainerSerializer;

@JacksonStdImpl
public class IterableSerializer extends AsArraySerializerBase<Iterable<?>> {
   public IterableSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts) {
      super(Iterable.class, elemType, staticTyping, vts, (JsonSerializer)null);
   }

   public IterableSerializer(IterableSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSerializer, Boolean unwrapSingle) {
      super(src, property, vts, valueSerializer, unwrapSingle);
   }

   public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
      return new IterableSerializer(this, this._property, vts, this._elementSerializer, this._unwrapSingle);
   }

   public IterableSerializer withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle) {
      return new IterableSerializer(this, property, vts, elementSerializer, unwrapSingle);
   }

   public boolean isEmpty(SerializerProvider prov, Iterable<?> value) {
      return !value.iterator().hasNext();
   }

   public boolean hasSingleElement(Iterable<?> value) {
      if (value != null) {
         Iterator<?> it = value.iterator();
         if (it.hasNext()) {
            it.next();
            if (!it.hasNext()) {
               return true;
            }
         }
      }

      return false;
   }

   public final void serialize(Iterable<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      if ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) || this._unwrapSingle == Boolean.TRUE) && this.hasSingleElement(value)) {
         this.serializeContents(value, gen, provider);
      } else {
         gen.writeStartArray();
         this.serializeContents(value, gen, provider);
         gen.writeEndArray();
      }
   }

   public void serializeContents(Iterable<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
      Iterator<?> it = value.iterator();
      if (it.hasNext()) {
         TypeSerializer typeSer = this._valueTypeSerializer;
         JsonSerializer<Object> prevSerializer = null;
         Class prevClass = null;

         do {
            Object elem = it.next();
            if (elem == null) {
               provider.defaultSerializeNull(jgen);
            } else {
               JsonSerializer<Object> currSerializer = this._elementSerializer;
               if (currSerializer == null) {
                  Class<?> cc = elem.getClass();
                  if (cc == prevClass) {
                     currSerializer = prevSerializer;
                  } else {
                     currSerializer = provider.findValueSerializer(cc, this._property);
                     prevSerializer = currSerializer;
                     prevClass = cc;
                  }
               }

               if (typeSer == null) {
                  currSerializer.serialize(elem, jgen, provider);
               } else {
                  currSerializer.serializeWithType(elem, jgen, provider, typeSer);
               }
            }
         } while(it.hasNext());
      }

   }
}
