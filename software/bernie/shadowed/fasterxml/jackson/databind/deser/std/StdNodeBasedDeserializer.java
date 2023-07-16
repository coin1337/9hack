package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;

public abstract class StdNodeBasedDeserializer<T> extends StdDeserializer<T> implements ResolvableDeserializer {
   private static final long serialVersionUID = 1L;
   protected JsonDeserializer<Object> _treeDeserializer;

   protected StdNodeBasedDeserializer(JavaType targetType) {
      super(targetType);
   }

   protected StdNodeBasedDeserializer(Class<T> targetType) {
      super(targetType);
   }

   protected StdNodeBasedDeserializer(StdNodeBasedDeserializer<?> src) {
      super((StdDeserializer)src);
      this._treeDeserializer = src._treeDeserializer;
   }

   public void resolve(DeserializationContext ctxt) throws JsonMappingException {
      this._treeDeserializer = ctxt.findRootValueDeserializer(ctxt.constructType(JsonNode.class));
   }

   public abstract T convert(JsonNode var1, DeserializationContext var2) throws IOException;

   public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
      JsonNode n = (JsonNode)this._treeDeserializer.deserialize(jp, ctxt);
      return this.convert(n, ctxt);
   }

   public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer td) throws IOException, JsonProcessingException {
      JsonNode n = (JsonNode)this._treeDeserializer.deserializeWithType(jp, ctxt, td);
      return this.convert(n, ctxt);
   }
}
