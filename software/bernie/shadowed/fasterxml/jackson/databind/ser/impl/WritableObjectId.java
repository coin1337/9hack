package software.bernie.shadowed.fasterxml.jackson.databind.ser.impl;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.SerializableString;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;

public final class WritableObjectId {
   public final ObjectIdGenerator<?> generator;
   public Object id;
   protected boolean idWritten = false;

   public WritableObjectId(ObjectIdGenerator<?> generator) {
      this.generator = generator;
   }

   public boolean writeAsId(JsonGenerator gen, SerializerProvider provider, ObjectIdWriter w) throws IOException {
      if (this.id != null && (this.idWritten || w.alwaysAsId)) {
         if (gen.canWriteObjectId()) {
            gen.writeObjectRef(String.valueOf(this.id));
         } else {
            w.serializer.serialize(this.id, gen, provider);
         }

         return true;
      } else {
         return false;
      }
   }

   public Object generateId(Object forPojo) {
      if (this.id == null) {
         this.id = this.generator.generateId(forPojo);
      }

      return this.id;
   }

   public void writeAsField(JsonGenerator gen, SerializerProvider provider, ObjectIdWriter w) throws IOException {
      this.idWritten = true;
      if (gen.canWriteObjectId()) {
         gen.writeObjectId(String.valueOf(this.id));
      } else {
         SerializableString name = w.propertyName;
         if (name != null) {
            gen.writeFieldName(name);
            w.serializer.serialize(this.id, gen, provider);
         }

      }
   }
}
