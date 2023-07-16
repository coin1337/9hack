package software.bernie.shadowed.fasterxml.jackson.databind.ext;

import java.io.IOException;
import java.lang.reflect.Type;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerationException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DOMSerializer extends StdSerializer<Node> {
   protected final DOMImplementationLS _domImpl;

   public DOMSerializer() {
      super(Node.class);

      DOMImplementationRegistry registry;
      try {
         registry = DOMImplementationRegistry.newInstance();
      } catch (Exception var3) {
         throw new IllegalStateException("Could not instantiate DOMImplementationRegistry: " + var3.getMessage(), var3);
      }

      this._domImpl = (DOMImplementationLS)registry.getDOMImplementation("LS");
   }

   public void serialize(Node value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
      if (this._domImpl == null) {
         throw new IllegalStateException("Could not find DOM LS");
      } else {
         LSSerializer writer = this._domImpl.createLSSerializer();
         jgen.writeString(writer.writeToString(value));
      }
   }

   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
      return this.createSchemaNode("string", true);
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      if (visitor != null) {
         visitor.expectAnyFormat(typeHint);
      }

   }
}
