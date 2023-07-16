package software.bernie.shadowed.fasterxml.jackson.databind.jsontype.impl;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonTypeInfo;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public class AsWrapperTypeSerializer extends TypeSerializerBase {
   public AsWrapperTypeSerializer(TypeIdResolver idRes, BeanProperty property) {
      super(idRes, property);
   }

   public AsWrapperTypeSerializer forProperty(BeanProperty prop) {
      return this._property == prop ? this : new AsWrapperTypeSerializer(this._idResolver, prop);
   }

   public JsonTypeInfo.As getTypeInclusion() {
      return JsonTypeInfo.As.WRAPPER_OBJECT;
   }

   protected String _validTypeId(String typeId) {
      return ClassUtil.nonNullString(typeId);
   }

   protected final void _writeTypeId(JsonGenerator g, String typeId) throws IOException {
      if (typeId != null) {
         g.writeTypeId(typeId);
      }

   }
}
