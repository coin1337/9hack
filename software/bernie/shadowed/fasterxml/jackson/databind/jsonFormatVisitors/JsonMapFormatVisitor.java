package software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors;

import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;

public interface JsonMapFormatVisitor extends JsonFormatVisitorWithSerializerProvider {
   void keyFormat(JsonFormatVisitable var1, JavaType var2) throws JsonMappingException;

   void valueFormat(JsonFormatVisitable var1, JavaType var2) throws JsonMappingException;

   public static class Base implements JsonMapFormatVisitor {
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

      public void keyFormat(JsonFormatVisitable handler, JavaType keyType) throws JsonMappingException {
      }

      public void valueFormat(JsonFormatVisitable handler, JavaType valueType) throws JsonMappingException {
      }
   }
}
