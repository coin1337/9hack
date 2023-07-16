package software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors;

import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;

public interface JsonArrayFormatVisitor extends JsonFormatVisitorWithSerializerProvider {
   void itemsFormat(JsonFormatVisitable var1, JavaType var2) throws JsonMappingException;

   void itemsFormat(JsonFormatTypes var1) throws JsonMappingException;

   public static class Base implements JsonArrayFormatVisitor {
      protected SerializerProvider _provider;

      public Base() {
      }

      public Base(SerializerProvider p) {
         this._provider = p;
      }

      public SerializerProvider getProvider() {
         return this._provider;
      }

      public void setProvider(SerializerProvider p) {
         this._provider = p;
      }

      public void itemsFormat(JsonFormatVisitable handler, JavaType elementType) throws JsonMappingException {
      }

      public void itemsFormat(JsonFormatTypes format) throws JsonMappingException {
      }
   }
}
