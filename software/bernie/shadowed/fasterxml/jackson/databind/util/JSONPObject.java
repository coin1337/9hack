package software.bernie.shadowed.fasterxml.jackson.databind.util;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonpCharacterEscapes;
import software.bernie.shadowed.fasterxml.jackson.core.io.CharacterEscapes;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializable;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class JSONPObject implements JsonSerializable {
   protected final String _function;
   protected final Object _value;
   protected final JavaType _serializationType;

   public JSONPObject(String function, Object value) {
      this(function, value, (JavaType)null);
   }

   public JSONPObject(String function, Object value, JavaType asType) {
      this._function = function;
      this._value = value;
      this._serializationType = asType;
   }

   public void serializeWithType(JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      this.serialize(gen, provider);
   }

   public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeRaw(this._function);
      gen.writeRaw('(');
      if (this._value == null) {
         provider.defaultSerializeNull(gen);
      } else {
         boolean override = gen.getCharacterEscapes() == null;
         if (override) {
            gen.setCharacterEscapes(JsonpCharacterEscapes.instance());
         }

         try {
            if (this._serializationType != null) {
               provider.findTypedValueSerializer((JavaType)this._serializationType, true, (BeanProperty)null).serialize(this._value, gen, provider);
            } else {
               provider.findTypedValueSerializer((Class)this._value.getClass(), true, (BeanProperty)null).serialize(this._value, gen, provider);
            }
         } finally {
            if (override) {
               gen.setCharacterEscapes((CharacterEscapes)null);
            }

         }
      }

      gen.writeRaw(')');
   }

   public String getFunction() {
      return this._function;
   }

   public Object getValue() {
      return this._value;
   }

   public JavaType getSerializationType() {
      return this._serializationType;
   }
}
