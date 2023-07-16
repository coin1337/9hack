package software.bernie.shadowed.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.type.WritableTypeId;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class InetSocketAddressSerializer extends StdScalarSerializer<InetSocketAddress> {
   public InetSocketAddressSerializer() {
      super(InetSocketAddress.class);
   }

   public void serialize(InetSocketAddress value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
      InetAddress addr = value.getAddress();
      String str = addr == null ? value.getHostName() : addr.toString().trim();
      int ix = str.indexOf(47);
      if (ix >= 0) {
         if (ix == 0) {
            str = addr instanceof Inet6Address ? "[" + str.substring(1) + "]" : str.substring(1);
         } else {
            str = str.substring(0, ix);
         }
      }

      jgen.writeString(str + ":" + value.getPort());
   }

   public void serializeWithType(InetSocketAddress value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, (Class)InetSocketAddress.class, (JsonToken)JsonToken.VALUE_STRING));
      this.serialize(value, g, provider);
      typeSer.writeTypeSuffix(g, typeIdDef);
   }
}
