package software.bernie.shadowed.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;

public class ByteBufferSerializer extends StdScalarSerializer<ByteBuffer> {
   public ByteBufferSerializer() {
      super(ByteBuffer.class);
   }

   public void serialize(ByteBuffer bbuf, JsonGenerator gen, SerializerProvider provider) throws IOException {
      if (bbuf.hasArray()) {
         gen.writeBinary(bbuf.array(), 0, bbuf.limit());
      } else {
         ByteBuffer copy = bbuf.asReadOnlyBuffer();
         if (copy.position() > 0) {
            copy.rewind();
         }

         InputStream in = new ByteBufferBackedInputStream(copy);
         gen.writeBinary(in, copy.remaining());
         in.close();
      }
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
      if (v2 != null) {
         v2.itemsFormat(JsonFormatTypes.INTEGER);
      }

   }
}
