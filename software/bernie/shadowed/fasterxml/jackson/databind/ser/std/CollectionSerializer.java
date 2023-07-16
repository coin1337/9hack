package software.bernie.shadowed.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ContainerSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;

public class CollectionSerializer extends AsArraySerializerBase<Collection<?>> {
   private static final long serialVersionUID = 1L;

   public CollectionSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
      super(Collection.class, elemType, staticTyping, vts, valueSerializer);
   }

   /** @deprecated */
   @Deprecated
   public CollectionSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, BeanProperty property, JsonSerializer<Object> valueSerializer) {
      this(elemType, staticTyping, vts, valueSerializer);
   }

   public CollectionSerializer(CollectionSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSerializer, Boolean unwrapSingle) {
      super(src, property, vts, valueSerializer, unwrapSingle);
   }

   public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
      return new CollectionSerializer(this, this._property, vts, this._elementSerializer, this._unwrapSingle);
   }

   public CollectionSerializer withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle) {
      return new CollectionSerializer(this, property, vts, elementSerializer, unwrapSingle);
   }

   public boolean isEmpty(SerializerProvider prov, Collection<?> value) {
      return value.isEmpty();
   }

   public boolean hasSingleElement(Collection<?> value) {
      return value.size() == 1;
   }

   public final void serialize(Collection<?> value, JsonGenerator g, SerializerProvider provider) throws IOException {
      int len = value.size();
      if (len != 1 || (this._unwrapSingle != null || !provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) && this._unwrapSingle != Boolean.TRUE) {
         g.writeStartArray(len);
         this.serializeContents(value, g, provider);
         g.writeEndArray();
      } else {
         this.serializeContents(value, g, provider);
      }
   }

   public void serializeContents(Collection<?> value, JsonGenerator g, SerializerProvider provider) throws IOException {
      g.setCurrentValue(value);
      if (this._elementSerializer != null) {
         this.serializeContentsUsing(value, g, provider, this._elementSerializer);
      } else {
         Iterator<?> it = value.iterator();
         if (it.hasNext()) {
            PropertySerializerMap serializers = this._dynamicSerializers;
            TypeSerializer typeSer = this._valueTypeSerializer;
            int i = 0;

            try {
               do {
                  Object elem = it.next();
                  if (elem == null) {
                     provider.defaultSerializeNull(g);
                  } else {
                     Class<?> cc = elem.getClass();
                     JsonSerializer<Object> serializer = serializers.serializerFor(cc);
                     if (serializer == null) {
                        if (this._elementType.hasGenericTypes()) {
                           serializer = this._findAndAddDynamic(serializers, provider.constructSpecializedType(this._elementType, cc), provider);
                        } else {
                           serializer = this._findAndAddDynamic(serializers, cc, provider);
                        }

                        serializers = this._dynamicSerializers;
                     }

                     if (typeSer == null) {
                        serializer.serialize(elem, g, provider);
                     } else {
                        serializer.serializeWithType(elem, g, provider, typeSer);
                     }
                  }

                  ++i;
               } while(it.hasNext());
            } catch (Exception var11) {
               this.wrapAndThrow(provider, var11, value, i);
            }

         }
      }
   }

   public void serializeContentsUsing(Collection<?> value, JsonGenerator g, SerializerProvider provider, JsonSerializer<Object> ser) throws IOException {
      Iterator<?> it = value.iterator();
      if (it.hasNext()) {
         TypeSerializer typeSer = this._valueTypeSerializer;
         int i = 0;

         do {
            Object elem = it.next();

            try {
               if (elem == null) {
                  provider.defaultSerializeNull(g);
               } else if (typeSer == null) {
                  ser.serialize(elem, g, provider);
               } else {
                  ser.serializeWithType(elem, g, provider, typeSer);
               }

               ++i;
            } catch (Exception var10) {
               this.wrapAndThrow(provider, var10, value, i);
            }
         } while(it.hasNext());
      }

   }
}
